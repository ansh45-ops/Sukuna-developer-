package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class SukunaViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("sukuna_tool_prefs", Context.MODE_PRIVATE)

    private val _selectedMode = MutableStateFlow(GameMode.M1M)
    val selectedMode: StateFlow<GameMode> = _selectedMode.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    private val _isDemo = MutableStateFlow(false)
    val isDemo: StateFlow<Boolean> = _isDemo.asStateFlow()

    private val _isAnalysing = MutableStateFlow(false)
    val isAnalysing: StateFlow<Boolean> = _isAnalysing.asStateFlow()

    private val _currentTheme = MutableStateFlow(ThemePreset.CRIMSON)
    val currentTheme: StateFlow<ThemePreset> = _currentTheme.asStateFlow()

    private val _activeTab = MutableStateFlow("home")
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    private val _secondsRemaining = MutableStateFlow(60)
    val secondsRemaining: StateFlow<Int> = _secondsRemaining.asStateFlow()

    private val _currentFusion = MutableStateFlow<FusionResult?>(null)
    val currentFusion: StateFlow<FusionResult?> = _currentFusion.asStateFlow()

    private val _historyList = MutableStateFlow<List<RoundRecord>>(emptyList())
    val historyList: StateFlow<List<RoundRecord>> = _historyList.asStateFlow()

    private val _winCount = MutableStateFlow(0)
    val winCount: StateFlow<Int> = _winCount.asStateFlow()

    private val _lossCount = MutableStateFlow(0)
    val lossCount: StateFlow<Int> = _lossCount.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _bestStreak = MutableStateFlow(0)
    val bestStreak: StateFlow<Int> = _bestStreak.asStateFlow()

    private val _step = MutableStateFlow(1)
    val step: StateFlow<Int> = _step.asStateFlow()

    private val _winCelebration = MutableStateFlow<RoundRecord?>(null)
    val winCelebration: StateFlow<RoundRecord?> = _winCelebration.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    val userName: String

    // Stored records per mode
    private val recordsMap = mutableMapOf<GameMode, MutableList<GameRecord>>()
    private val adaptiveWeights = mutableMapOf("RDX" to 1.0, "VANTA" to 1.0, "NOCTIS" to 1.0, "BRAIN" to 1.2, "MARKET" to 1.2)

    private var pollJob: Job? = null
    private var timerJob: Job? = null
    private var demoJob: Job? = null
    private var lastSeenPeriod: String? = null
    private var pendingTargetPeriod: String? = null
    private var pendingPrediction: String? = null
    private var pendingNums: List<Int> = emptyList()

    init {
        val savedThemeId = prefs.getString("theme_id", ThemePreset.CRIMSON.id)
        _currentTheme.value = ThemePreset.values().firstOrNull { it.id == savedThemeId } ?: ThemePreset.CRIMSON

        val randomId = prefs.getInt("user_id", Random.nextInt(100, 999))
        prefs.edit().putInt("user_id", randomId).apply()
        userName = "SUKUNA USER $randomId"

        // Initialize dummy records
        GameMode.values().forEach { mode ->
            recordsMap[mode] = ApiService.generateSampleHistory(120).toMutableList()
        }

        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                val interval = _selectedMode.value.intervalSeconds
                val nowSec = (System.currentTimeMillis() / 1000).toInt()
                val rem = interval - (nowSec % interval)
                _secondsRemaining.value = rem
                delay(1000)
            }
        }
    }

    fun switchTab(tab: String) {
        _activeTab.value = tab
    }

    fun setTheme(preset: ThemePreset) {
        _currentTheme.value = preset
        prefs.edit().putString("theme_id", preset.id).apply()
        _toastEvent.tryEmit("✓ Theme: ${preset.title}")
    }

    fun selectMode(mode: GameMode) {
        if (_isRunning.value) {
            _toastEvent.tryEmit("Stop engine first to switch timer")
            return
        }
        _selectedMode.value = mode
        _toastEvent.tryEmit("Mode selected: ${mode.displayName}")
    }

    fun toggleEngine() {
        if (_isRunning.value) {
            stopEngine()
        } else {
            startEngine()
        }
    }

    private fun startEngine() {
        _isRunning.value = true
        _isDemo.value = false
        demoJob?.cancel()
        lastSeenPeriod = null
        _toastEvent.tryEmit("▶ SUKUNA NOVIX V1 Started • ${_selectedMode.value.displayName}")

        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            // First fetch immediately
            fetchAndEvaluate()

            val delayMs = if (_selectedMode.value == GameMode.M30S) 4000L else 7000L
            while (isActive && _isRunning.value) {
                delay(delayMs)
                fetchAndEvaluate()
            }
        }
    }

    private suspend fun fetchAndEvaluate() {
        val mode = _selectedMode.value
        val fetched = ApiService.fetchHistory(mode)

        val currentList = recordsMap[mode] ?: mutableListOf()
        if (fetched.isNotEmpty()) {
            val existingPeriods = currentList.map { it.period }.toSet()
            val newItems = fetched.filter { it.period !in existingPeriods }
            if (newItems.isNotEmpty()) {
                currentList.addAll(0, newItems)
                // keep up to 600 records
                if (currentList.size > 600) {
                    recordsMap[mode] = currentList.take(600).toMutableList()
                }
            }
        }

        val latest = currentList.firstOrNull() ?: return
        if (latest.period != lastSeenPeriod) {
            lastSeenPeriod = latest.period
            evaluateCycle(latest, currentList)
        }
    }

    private suspend fun evaluateCycle(latest: GameRecord, history: List<GameRecord>) {
        _isAnalysing.value = true
        delay(600) // Aesthetic analysis animation

        val actualType = latest.size
        val actualNum = latest.number

        // Check pending prediction outcome
        if (pendingTargetPeriod == latest.period && pendingPrediction != null) {
            val pred = pendingPrediction!!
            if (pred == "SKIP") {
                val record = RoundRecord(
                    period = latest.period,
                    mode = _selectedMode.value.displayName,
                    prediction = "SKIP",
                    predNums = pendingNums,
                    actual = actualType,
                    actualNum = actualNum,
                    win = null,
                    jackpot = false,
                    skip = true,
                    step = _step.value,
                    streak = _lossCount.value,
                    engines = _currentFusion.value?.cands ?: emptyMap(),
                    conf = (_currentFusion.value?.conf?.times(100))?.toInt() ?: 60
                )
                addHistory(record)
            } else {
                val sideWin = pred == actualType
                val jackpot = pendingNums.contains(actualNum)
                val win = sideWin || jackpot

                // Update adaptive learning weights
                updateAdaptiveWeights(actualType)

                if (win) {
                    _winCount.value += 1
                    _currentStreak.value += 1
                    if (_currentStreak.value > _bestStreak.value) {
                        _bestStreak.value = _currentStreak.value
                    }
                    _step.value = 1
                } else {
                    _lossCount.value += 1
                    _currentStreak.value = 0
                    _step.value = min(10, _step.value + 1)
                }

                val record = RoundRecord(
                    period = latest.period,
                    mode = _selectedMode.value.displayName,
                    prediction = pred,
                    predNums = pendingNums,
                    actual = actualType,
                    actualNum = actualNum,
                    win = win,
                    jackpot = jackpot,
                    skip = false,
                    step = _step.value,
                    streak = if (win) _currentStreak.value else _lossCount.value,
                    engines = _currentFusion.value?.cands ?: emptyMap(),
                    conf = (_currentFusion.value?.conf?.times(100))?.toInt() ?: 60
                )
                addHistory(record)

                if (win) {
                    _winCelebration.value = record
                }
            }
        }

        // Generate next prediction
        val targetPeriod = nextPeriod(latest.period)
        val fusion = Engines.fuse(history, targetPeriod, _historyList.value.firstOrNull(), adaptiveWeights)
        _currentFusion.value = fusion

        pendingTargetPeriod = targetPeriod
        pendingPrediction = if (fusion.skip) "SKIP" else fusion.call
        pendingNums = fusion.nums

        _isAnalysing.value = false
    }

    private fun updateAdaptiveWeights(actual: String) {
        val lastCands = _currentFusion.value?.cands ?: return
        val eta = 0.35
        for ((id, call) in lastCands) {
            val hit = call == actual
            val oldW = adaptiveWeights[id] ?: 1.0
            val factor = if (hit) Math.exp(eta) else Math.exp(-eta)
            adaptiveWeights[id] = (oldW * factor).coerceIn(0.35, 2.6)
        }
    }

    fun startDemo() {
        if (_isRunning.value) stopEngine()
        _isDemo.value = true
        _isRunning.value = true
        _activeTab.value = "home"
        _toastEvent.tryEmit("▷ Offline Demo Simulation Running")

        val mode = _selectedMode.value
        val sampleList = ApiService.generateSampleHistory(150).toMutableList()
        recordsMap[mode] = sampleList

        demoJob?.cancel()
        demoJob = viewModelScope.launch {
            var counter = 1
            while (isActive && _isDemo.value) {
                val latest = sampleList.first()
                evaluateCycle(latest, sampleList)

                val waitSec = if (mode == GameMode.M30S) 6L else 9L
                delay(waitSec * 1000)

                // Advance one simulated round
                val nextP = nextPeriod(sampleList.first().period)
                val randNum = Random.nextInt(0, 10)
                sampleList.add(0, GameRecord(nextP, randNum))
                counter++
            }
        }
    }

    fun stopEngine() {
        _isRunning.value = false
        _isDemo.value = false
        pollJob?.cancel()
        demoJob?.cancel()
        _isAnalysing.value = false
        _toastEvent.tryEmit("■ Engine Stopped")
    }

    fun closeWinCelebration() {
        _winCelebration.value = null
    }

    fun resetStats() {
        _winCount.value = 0
        _lossCount.value = 0
        _currentStreak.value = 0
        _bestStreak.value = 0
        _step.value = 1
        _historyList.value = emptyList()
        _toastEvent.tryEmit("Stats reset successfully")
    }

    fun clearCache() {
        recordsMap.clear()
        GameMode.values().forEach { mode ->
            recordsMap[mode] = ApiService.generateSampleHistory(120).toMutableList()
        }
        _toastEvent.tryEmit("Cache cleared & sample seeds repaired")
    }

    private fun addHistory(record: RoundRecord) {
        val current = _historyList.value.toMutableList()
        current.add(0, record)
        if (current.size > 80) {
            _historyList.value = current.take(80)
        } else {
            _historyList.value = current
        }
    }

    private fun nextPeriod(p: String): String {
        return try {
            val numPart = p.takeLastWhile { it.isDigit() }
            if (numPart.isEmpty()) return (p.hashCode() + 1).toString()
            val prefix = p.dropLast(numPart.length)
            val nextNum = (numPart.toLong() + 1).toString().padStart(numPart.length, '0')
            prefix + nextNum
        } catch (e: Exception) {
            (System.currentTimeMillis() / 1000).toString()
        }
    }
}
