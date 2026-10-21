package com.example.data

enum class GameMode(val id: String, val displayName: String, val intervalSeconds: Int) {
    M30S("30s", "30 SEC", 30),
    M1M("1m", "1 MIN", 60),
    TRX("trx", "TRX 1M", 60)
}

data class GameRecord(
    val period: String,
    val number: Int
) {
    val size: String get() = if (number >= 5) "BIG" else "SMALL"
    val sizeLetter: String get() = if (number >= 5) "B" else "S"
}

data class MissingStat(
    val cur: Int,
    val avg: Double,
    val max: Int
)

data class MarketMetrics(
    val alt10: Double,
    val alt20: Double,
    val avgRun: Double,
    val curRun: Int,
    val curSide: String,
    val runsZ: Double,
    val pCont: Double?,
    val missB: MissingStat,
    val missS: MissingStat,
    val p20: Double,
    val follow12: Double,
    val reverse12: Double
)

data class MarketVote(
    val name: String,
    val call: String,
    val conf: Double,
    val note: String
)

data class MarketResult(
    val id: String = "MARKET",
    val name: String = "SUKUNA MARKET",
    val call: String,
    val pBig: Double,
    val conf: Int,
    val state: String,
    val votes: List<MarketVote>,
    val metrics: MarketMetrics
)

data class BrainResult(
    val id: String = "BRAIN",
    val name: String = "SUKUNA BRAIN",
    val call: String,
    val pBig: Double,
    val conf: Int,
    val road: List<GameRecord>,
    val pattern: String,
    val patternNote: String,
    val humanCall: String,
    val humanConf: Int,
    val aiCall: String,
    val aiConf: Int,
    val verdict: String,
    val breakInfo: String? = null,
    val breakRisk: Int? = null,
    val run: Int = 1,
    val bigCount: Int = 5
)

data class LayerScore(
    val name: String,
    val detail: String,
    val pred: String,
    val conf: Int
)

data class RdxResult(
    val id: String = "RDX",
    val name: String = "SUKUNA CORE",
    val call: String,
    val pBig: Double,
    val conf: Int,
    val layers: List<LayerScore>,
    val numBig: Int,
    val numSmall: Int
)

data class VantaResult(
    val id: String = "VANTA",
    val name: String = "SUKUNA VISION",
    val call: String,
    val pBig: Double,
    val conf: Int,
    val basis: String,
    val digit: Int,
    val markov: Int,
    val entropy: Double,
    val volatility: Double,
    val momentum: Double
)

data class NoctisResult(
    val id: String = "NOCTIS",
    val name: String = "SUKUNA GUARD",
    val call: String,
    val candidate: String,
    val pBig: Double,
    val conf: Int,
    val edge: Double,
    val reasons: List<String>,
    val consensus: Double,
    val randomness: Double,
    val stability: Double,
    val skill: Double,
    val nums: List<Int>
)

data class DigitScore(
    val digit: Int,
    val score: Double,
    val gap: Int,
    val reasons: List<String>,
    val tags: List<String>
)

data class NumberBrainResult(
    val main: DigitScore,
    val backup: DigitScore,
    val stats: List<DigitScore>,
    val avg10: Double
)

data class LossAnalysis(
    val type: String,
    val detail: String,
    val note: String,
    val biasCall: String?,
    val strength: Double
)

data class FusionResult(
    val targetPeriod: String,
    val call: String,
    val pBig: Double,
    val edge: Double,
    val conf: Double,
    val agree: Int,
    val cands: Map<String, String>,
    val nums: List<Int>,
    val numInfo: NumberBrainResult,
    val regime: String,
    val risk: String,
    val skip: Boolean,
    val skipWhy: String,
    val LA: LossAnalysis?,
    val forced: Boolean,
    val weights: Map<String, Double>,
    val wfAcc: Map<String, Double>,
    val market: MarketResult,
    val brain: BrainResult,
    val rdx: RdxResult,
    val vanta: VantaResult,
    val noctis: NoctisResult
)

data class RoundRecord(
    val id: Long = System.currentTimeMillis(),
    val period: String,
    val mode: String,
    val prediction: String,
    val predNums: List<Int>,
    val actual: String,
    val actualNum: Int,
    val win: Boolean?,
    val jackpot: Boolean,
    val skip: Boolean,
    val step: Int,
    val streak: Int,
    val engines: Map<String, String>,
    val conf: Int
)

enum class ThemePreset(
    val id: String,
    val title: String,
    val primaryHex: Long,
    val secondaryHex: Long,
    val bgHex: Long,
    val cardHex: Long,
    val isLight: Boolean = false
) {
    CRIMSON("crimson", "CRIMSON NOIR", 0xFFFF3156, 0xFFFF714E, 0xFF09070E, 0xFF17111F, false),
    GOLD("gold", "GOLD NOIR", 0xFFF5B40A, 0xFFE09A00, 0xFF070A14, 0xFF10152A, false),
    VIOLET("violet", "VIOLET LIQUID", 0xFF7C6CFF, 0xFF5B4BE8, 0xFF0B0918, 0xFF16122C, false),
    MINT("mint", "CYBER MINT", 0xFF22E5A4, 0xFF0FBF85, 0xFF06130F, 0xFF0F241E, false),
    ICE("ice", "ICE COLD", 0xFF38C8FF, 0xFF1AA3E0, 0xFF07111D, 0xFF0F2236, false),
    EMBER("ember", "EMBER NEON", 0xFFFF7A1A, 0xFFE05A00, 0xFF140904, 0xFF261208, false),
    PEARL("pearl", "PEARL GLASS", 0xFF4F46E5, 0xFF06B6D4, 0xFFEEF1F9, 0xFFFFFFFF, true),
    ROSE("rose", "ROSE GLASS", 0xFFE11D74, 0xFFA855F7, 0xFFFFF7FB, 0xFFFFFFFF, true)
}
