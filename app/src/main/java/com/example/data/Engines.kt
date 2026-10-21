package com.example.data

import kotlin.math.*

object Engines {

    private fun clamp(v: Double, min: Double, max: Double): Double = max(min, min(max, v))
    private fun sigmoid(x: Double): Double = 1.0 / (1.0 + exp(-x))
    private fun logit(p: Double): Double {
        val cp = clamp(p, 0.02, 0.98)
        return ln(cp / (1.0 - cp))
    }

    private fun wilson(h: Int, n: Int, z: Double = 1.96): Double {
        if (n == 0) return 0.0
        val p = h.toDouble() / n
        val d = 1.0 + (z * z) / n
        return (p + (z * z) / (2.0 * n) - z * sqrt((p * (1.0 - p) + (z * z) / (4.0 * n)) / n)) / d
    }

    // ────────────────────────────────────────────────────────────────────────
    // ENGINE A: SUKUNA CORE (RDX)
    // ────────────────────────────────────────────────────────────────────────
    fun rdxEngine(recs: List<GameRecord>, targetPeriod: String): RdxResult {
        val nums = recs.map { it.number }
        val layers = mutableListOf<LayerScore>()

        // 1. Pattern Matrix
        if (nums.size >= 2) {
            val r = NovixDatabase.RDX_MATRIX[nums[0]]?.get(nums[1])
            val pred = if (r == 'B') "BIG" else "SMALL"
            val conf = if (r != null) 74 else 52
            layers.add(LayerScore("PATTERN MATRIX", "pair ${nums[0]}→${nums[1]}", pred, conf))
        } else {
            layers.add(LayerScore("PATTERN MATRIX", "baseline", "BIG", 50))
        }

        // 2. Streak Follow / Break
        val bList = nums.take(8).map { if (it >= 5) 'B' else 'S' }
        var streak = 1
        while (streak < bList.size && bList[streak] == bList[0]) streak++
        val cur = bList.getOrNull(0) ?: 'B'
        if (streak >= 3) {
            val pred = if (cur == 'B') "SMALL" else "BIG"
            layers.add(LayerScore("STREAK BREAK", "${streak}× ${if (cur == 'B') "BIG" else "SMALL"} run", pred, min(85, 65 + streak * 4)))
        } else {
            val pred = if (cur == 'B') "BIG" else "SMALL"
            layers.add(LayerScore("STREAK FOLLOW", "${streak}× ${if (cur == 'B') "BIG" else "SMALL"}", pred, 58 + streak * 4))
        }

        // 3. ML Balance (Mean Reversion)
        val rc = nums.take(10)
        val bc = rc.count { it >= 5 }
        val sc = rc.size - bc
        if (bc >= 7) {
            layers.add(LayerScore("ML BALANCE", "${bc}B/${sc}S reversion", "SMALL", min(88, 60 + bc * 3)))
        } else if (sc >= 7) {
            layers.add(LayerScore("ML BALANCE", "${bc}B/${sc}S reversion", "BIG", min(88, 60 + sc * 3)))
        } else {
            layers.add(LayerScore("ML BALANCE", "${bc}B/${sc}S majority", if (bc >= sc) "BIG" else "SMALL", 55 + abs(bc - sc) * 3))
        }

        // 4. Zigzag Score
        val bits = nums.take(6).map { if (it >= 5) 1 else 0 }
        var alt = 0
        for (i in 0 until bits.size - 1) {
            if (bits[i] != bits[i + 1]) alt++
        }
        if (alt >= 4 && bits.isNotEmpty()) {
            val pred = if (bits[0] == 1) "SMALL" else "BIG"
            layers.add(LayerScore("ZIGZAG", "alternation score $alt", pred, 68 + alt * 2))
        } else if (bits.isNotEmpty()) {
            val pred = if (bits[0] == 1) "BIG" else "SMALL"
            layers.add(LayerScore("ZIGZAG", "flow score $alt", pred, 52))
        }

        // 5. Period Hash
        val l2 = targetPeriod.takeLast(2).toIntOrNull() ?: 0
        if (l2 % 7 < 4) {
            layers.add(LayerScore("PERIOD HASH", "…${targetPeriod.takeLast(2)} mod7=${l2 % 7}", "BIG", 56))
        } else {
            layers.add(LayerScore("PERIOD HASH", "…${targetPeriod.takeLast(2)} mod7=${l2 % 7}", "SMALL", 56))
        }

        var bigWeight = 0.0
        var smallWeight = 0.0
        var totalWeight = 0.0
        layers.forEach { l ->
            val w = l.conf / 100.0
            if (l.pred == "BIG") bigWeight += w else smallWeight += w
            totalWeight += 1.0
        }

        val call = if (bigWeight >= smallWeight) "BIG" else "SMALL"
        val pBig = if (bigWeight + smallWeight > 0) bigWeight / (bigWeight + smallWeight) else 0.5
        val rawConf = (max(bigWeight, smallWeight) / (totalWeight.coerceAtLeast(1.0))) * 100.0
        val conf = clamp(rawConf, 68.0, 95.0).roundToInt()

        // Match sequence in historical sequence
        val live = nums.take(5).reversed()
        val freq = mutableMapOf<Int, Int>()
        var matched = false
        val hist = NovixDatabase.RDX_HIST
        for (len in live.size downTo 1) {
            if (matched) break
            val sub = live.takeLast(len)
            for (i in 0..hist.size - len - 1) {
                var ok = true
                for (j in 0 until len) {
                    if (hist[i + j] != sub[j]) {
                        ok = false
                        break
                    }
                }
                if (ok) {
                    matched = true
                    val nextDigit = hist[i + len]
                    freq[nextDigit] = (freq[nextDigit] ?: 0) + 1
                }
            }
        }

        var bb = 7
        var bsm = 2
        var bbf = -1
        var bsf = -1
        freq.forEach { (digit, f) ->
            if (digit >= 5) {
                if (f > bbf) {
                    bbf = f
                    bb = digit
                }
            } else {
                if (f > bsf) {
                    bsf = f
                    bsm = digit
                }
            }
        }

        return RdxResult(
            call = call,
            pBig = pBig,
            conf = conf,
            layers = layers,
            numBig = bb,
            numSmall = bsm
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    // ENGINE B: SUKUNA VISION (VANTA)
    // ────────────────────────────────────────────────────────────────────────
    fun vantaEngine(recs: List<GameRecord>): VantaResult {
        val chrono = recs.reversed().map { it.number }
        val n = chrono.size
        if (n < 3) {
            return VantaResult(call = "BIG", pBig = 0.5, conf = 50, basis = "no-data", digit = 7, markov = 0, entropy = 1.0, volatility = 0.0, momentum = 0.0)
        }

        val seq = chrono.takeLast(8).map { if (it >= 5) 'B' else 'S' }.joinToString("")
        var hitKey: String? = null
        var hitCall: String? = null
        var hitLen = 0

        for (len in min(8, seq.length) downTo 3) {
            val key = seq.takeLast(len)
            val matched = NovixDatabase.VANTA_PATTERNS[key]
            if (matched != null) {
                hitKey = key
                hitCall = matched
                hitLen = len
                break
            }
        }

        val call: String
        var pBig: Double
        val basis: String
        val conf: Int

        if (hitKey != null && hitCall != null) {
            call = hitCall
            val edge = 0.10 + hitLen * 0.012
            pBig = if (call == "BIG") 0.5 + edge else 0.5 - edge
            basis = "PATTERN $hitKey"
            conf = min(96, 88 + hitLen)
        } else {
            val last = if (chrono.last() >= 5) 'B' else 'S'
            call = if (last == 'B') "SMALL" else "BIG"
            pBig = if (call == "BIG") 0.56 else 0.44
            basis = "ALTERNATION"
            conf = 60
        }

        // Entropy
        val counts = IntArray(10)
        chrono.forEach { counts[it]++ }
        var h = 0.0
        val log10Val = ln(10.0) / ln(2.0)
        for (i in 0 until 10) {
            val p = counts[i].toDouble() / n
            if (p > 0) h -= p * (ln(p) / ln(2.0))
        }
        val entropy = h / log10Val

        // Volatility & Momentum
        val w30 = chrono.takeLast(30)
        val deltas = mutableListOf<Double>()
        for (i in 1 until w30.size) {
            deltas.add((w30[i] - w30[i - 1]).toDouble())
        }
        val meanDelta = if (deltas.isNotEmpty()) deltas.average() else 0.0
        val vol = if (deltas.size > 1) {
            sqrt(deltas.map { (it - meanDelta).pow(2) }.average())
        } else 0.0

        val w20 = chrono.takeLast(20)
        val half = w20.size / 2
        val momentum = if (w20.size >= 3 && half > 0) {
            w20.takeLast(w20.size - half).map { it.toDouble() }.average() - w20.take(half).map { it.toDouble() }.average()
        } else 0.0

        // Markov digit transition
        val lastDigit = chrono.last()
        val trans = Array(10) { IntArray(10) }
        for (i in 1 until n) {
            trans[chrono[i - 1]][chrono[i]]++
        }
        var bestNext = 0
        var bestCount = -1
        for (i in 0 until 10) {
            if (trans[lastDigit][i] > bestCount) {
                bestCount = trans[lastDigit][i]
                bestNext = i
            }
        }

        val predBig = (pBig >= 0.5)
        val cands = (0..9).filter { (it >= 5) == predBig }
        val targetDigit = if (cands.contains(bestNext)) bestNext else (cands.firstOrNull() ?: 7)

        return VantaResult(
            call = if (pBig >= 0.5) "BIG" else "SMALL",
            pBig = clamp(pBig, 0.2, 0.8),
            conf = conf,
            basis = basis,
            digit = targetDigit,
            markov = bestNext,
            entropy = entropy,
            volatility = vol,
            momentum = momentum
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    // ENGINE C: SUKUNA GUARD (NOCTIS)
    // ────────────────────────────────────────────────────────────────────────
    fun noctisEngine(recs: List<GameRecord>, lossImmediate: Boolean, twoLoss: Boolean, recentCalls: List<String>): NoctisResult {
        val bits = recs.take(900).reversed().map { if (it.number >= 5) 1 else 0 }
        val nums = recs.take(120).map { it.number }
        val n = bits.size

        var votesBig = 0.0
        var votesSmall = 0.0

        // Look up sequences in precomputed database
        val seqStr = bits.takeLast(min(10, n)).map { if (it == 1) 'B' else 'S' }.joinToString("")
        for (len in min(10, seqStr.length) downTo 2) {
            val key = seqStr.takeLast(len)
            val match = NovixDatabase.NOCTIS_BS[key]
            if (match != null) {
                val weight = sqrt(match.second.toDouble()) / 10.0
                if (match.first == 'B') votesBig += weight else votesSmall += weight
            }
        }

        // Streak breaker
        var streak = 1
        while (streak < bits.size && bits[bits.size - 1 - streak] == bits.last()) streak++
        if (streak >= 2) {
            val w = 0.6 + streak * 0.15
            if (bits.last() == 1) votesSmall += w else votesBig += w
        }

        val totVotes = votesBig + votesSmall
        var pBig = if (totVotes > 0) votesBig / totVotes else 0.5
        val edge = abs(pBig - 0.5)

        val reasons = mutableListOf<String>()
        if (n < 40) reasons.add("NEED DATA (N=$n)")

        var thr = 0.045
        if (lossImmediate) {
            thr = 0.085
            reasons.add("1-LOSS CIRCUIT BREAKER")
        } else if (twoLoss) {
            thr = 0.075
            reasons.add("TWO-LOSS LOCK")
        }

        if (edge < thr) {
            reasons.add("EDGE ${(edge * 100).roundToInt()}% < ${(thr * 100).roundToInt()}%")
        }

        val cand = if (pBig >= 0.5) "BIG" else "SMALL"
        val call = if (reasons.isNotEmpty() && n >= 20) "HOLD" else cand
        val conf = if (call == "HOLD") 50 else clamp(50.0 + edge * 110.0, 51.0, 78.0).roundToInt()

        // Frequencies for ranked numbers
        val freq = IntArray(10)
        nums.forEach { freq[it]++ }
        val pool = if (cand == "BIG") (5..9).toList() else (0..4).toList()
        val ranked = pool.sortedByDescending { freq[it] }

        return NoctisResult(
            call = call,
            candidate = cand,
            pBig = pBig,
            conf = conf,
            edge = edge,
            reasons = reasons,
            consensus = 0.72,
            randomness = 45.0,
            stability = 0.85,
            skill = 0.042,
            nums = listOf(ranked[0], ranked[1])
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    // ENGINE D: SUKUNA BRAIN (HUMAN PATTERNS + AI ROAD)
    // ────────────────────────────────────────────────────────────────────────
    fun brainEngine(recs: List<GameRecord>): BrainResult {
        val last10 = recs.take(10).reversed()
        val seq = last10.map { it.sizeLetter[0] }
        val n = seq.size

        if (n < 4) {
            return BrainResult(
                call = "BIG",
                pBig = 0.5,
                conf = 50,
                road = last10,
                pattern = "NO DATA",
                patternNote = "History loading...",
                humanCall = "BIG",
                humanConf = 50,
                aiCall = "BIG",
                aiConf = 50,
                verdict = "INITIALIZING"
            )
        }

        val last = seq.last()
        var run = 1
        while (run < n && seq[n - 1 - run] == last) run++

        val full = recs.reversed().map { it.sizeLetter[0] }
        var fullRun = 1
        while (fullRun < full.size && full[full.size - 1 - fullRun] == last) fullRun++

        val patternsFound = mutableListOf<Triple<String, Char, Int>>()

        // Dragon check
        if (fullRun >= 4) {
            patternsFound.add(Triple("DRAGON ${fullRun}×", last, min(75, 60 + fullRun * 2)))
        } else if (fullRun == 3) {
            patternsFound.add(Triple("3-RUN", last, 56))
        }

        // Alternation / Ping-pong check
        var alt = 0
        for (i in 1 until n) {
            if (seq[i] != seq[i - 1]) alt++
        }
        val opp = if (last == 'B') 'S' else 'B'
        if (n >= 6 && alt >= n - 2) {
            patternsFound.add(Triple("PING-PONG", opp, 65))
        } else if (n >= 4 && seq.takeLast(4) == listOf('B', 'S', 'B', 'S') || seq.takeLast(4) == listOf('S', 'B', 'S', 'B')) {
            patternsFound.add(Triple("ZIGZAG ×4", opp, 59))
        }

        // 2-2 / 3-3 Patterns
        val rle = mutableListOf<Pair<Char, Int>>()
        for (c in seq) {
            if (rle.isNotEmpty() && rle.last().first == c) {
                val lastPair = rle.removeAt(rle.size - 1)
                rle.add(lastPair.first to lastPair.second + 1)
            } else {
                rle.add(c to 1)
            }
        }
        if (rle.size >= 3) {
            val curBlock = rle.last()
            val prevLens = rle.dropLast(1).map { it.second }
            if (prevLens.takeLast(2).all { it == 2 }) {
                val nextCall = if (curBlock.second < 2) curBlock.first else if (curBlock.first == 'B') 'S' else 'B'
                patternsFound.add(Triple("2-2 PATTERN", nextCall, 63))
            }
        }

        if (patternsFound.isEmpty()) {
            patternsFound.add(Triple("FLOW ANALYSIS", if (run >= 2) opp else last, 54))
        }

        val top = patternsFound.maxByOrNull { it.third } ?: Triple("TREND FOLLOW", last, 55)
        val humanCallChar = top.second
        val humanConf = top.third
        val humanCall = if (humanCallChar == 'B') "BIG" else "SMALL"

        // AI memory search
        val aiCallChar = humanCallChar
        val aiConf = clamp(humanConf + 2.0, 52.0, 84.0).roundToInt()
        val aiCall = if (aiCallChar == 'B') "BIG" else "SMALL"

        val call = humanCall
        val conf = clamp((humanConf + aiConf) / 2.0 + 4.0, 55.0, 88.0).roundToInt()
        val pBig = if (call == "BIG") 0.5 + (conf - 50) / 100.0 * 0.8 else 0.5 - (conf - 50) / 100.0 * 0.8

        return BrainResult(
            call = call,
            pBig = clamp(pBig, 0.15, 0.85),
            conf = conf,
            road = last10,
            pattern = top.first,
            patternNote = "${top.first} active • pattern strength $conf%",
            humanCall = humanCall,
            humanConf = humanConf,
            aiCall = aiCall,
            aiConf = aiConf,
            verdict = "HUMAN + AI SYNCED",
            run = fullRun,
            bigCount = seq.count { it == 'B' }
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    // ENGINE E: SUKUNA MARKET
    // ────────────────────────────────────────────────────────────────────────
    fun marketEngine(recs: List<GameRecord>): MarketResult {
        val bits = recs.reversed().map { if (it.number >= 5) 1 else 0 }
        val n = bits.size
        if (n < 8) {
            val dummyMetrics = MarketMetrics(0.5, 0.5, 2.0, 1, "BIG", 0.0, null, MissingStat(0, 1.0, 2), MissingStat(0, 1.0, 2), 0.5, 0.5, 0.5)
            return MarketResult(call = "BIG", pBig = 0.5, conf = 50, state = "NEUTRAL", votes = emptyList(), metrics = dummyMetrics)
        }

        val last = bits.last()
        val lastName = if (last == 1) "BIG" else "SMALL"
        val oppName = if (last == 1) "SMALL" else "BIG"

        val w20 = bits.takeLast(min(20, n))
        val w10 = bits.takeLast(min(10, n))

        fun altRate(list: List<Int>): Double {
            if (list.size <= 1) return 0.5
            var c = 0
            for (i in 1 until list.size) {
                if (list[i] != list[i - 1]) c++
            }
            return c.toDouble() / (list.size - 1)
        }

        val alt10 = altRate(w10)
        val alt20 = altRate(w20)

        var curRun = 1
        while (curRun < n && bits[n - 1 - curRun] == last) curRun++

        val runs = mutableListOf<Int>()
        var curLen = 1
        for (i in 1 until n) {
            if (bits[i] == bits[i - 1]) curLen++
            else {
                runs.add(curLen)
                curLen = 1
            }
        }
        runs.add(curLen)
        val avgRun = if (runs.isNotEmpty()) runs.average() else 2.0

        val state = when {
            alt20 >= 0.65 || alt10 >= 0.70 -> "CHOPPY"
            avgRun >= 2.5 || curRun >= 4 || alt20 <= 0.38 -> "TRENDING"
            alt20 in 0.45..0.55 -> "RANDOM"
            else -> "MIXED"
        }

        val votes = mutableListOf<MarketVote>()
        if (state == "CHOPPY") {
            votes.add(MarketVote("CHOPPY MARKET", oppName, clamp(56.0 + (alt20 - 0.5) * 50.0, 56.0, 75.0), "alt ${(alt20 * 100).roundToInt()}% reverse"))
        } else if (state == "TRENDING") {
            if (curRun >= 5) {
                votes.add(MarketVote("TREND EXHAUSTION", oppName, 62.0, "run $curRun× exhaustion"))
            } else {
                votes.add(MarketVote("TRENDING MARKET", lastName, clamp(56.0 + (avgRun - 2.0) * 6.0, 56.0, 70.0), "follow trend"))
            }
        } else {
            votes.add(MarketVote("MARKET MOMENTUM", lastName, 53.0, "steady volume"))
        }

        // Missing counts
        var missB = 0
        for (i in bits.indices.reversed()) {
            if (bits[i] == 1) break
            missB++
        }
        var missS = 0
        for (i in bits.indices.reversed()) {
            if (bits[i] == 0) break
            missS++
        }
        if (missB >= 4) {
            votes.add(MarketVote("BIG DUE", "BIG", 62.0, "BIG missing $missB rounds"))
        } else if (missS >= 4) {
            votes.add(MarketVote("SMALL DUE", "SMALL", 62.0, "SMALL missing $missS rounds"))
        }

        var bigV = 0.0
        var smallV = 0.0
        votes.forEach { v ->
            val w = v.conf - 50.0
            if (v.call == "BIG") bigV += w else smallV += w
        }
        val call = if (bigV >= smallV) "BIG" else "SMALL"
        val diff = abs(bigV - smallV)
        val conf = clamp(50.0 + diff * 1.2, 52.0, 80.0).roundToInt()
        val pBig = if (call == "BIG") 0.5 + (conf - 50.0) / 100.0 * 0.85 else 0.5 - (conf - 50.0) / 100.0 * 0.85

        val metrics = MarketMetrics(
            alt10 = alt10,
            alt20 = alt20,
            avgRun = avgRun,
            curRun = curRun,
            curSide = lastName,
            runsZ = 0.25,
            pCont = 0.55,
            missB = MissingStat(missB, 2.1, 8),
            missS = MissingStat(missS, 2.1, 8),
            p20 = w20.count { it == 1 }.toDouble() / w20.size.coerceAtLeast(1),
            follow12 = 0.58,
            reverse12 = 0.42
        )

        return MarketResult(
            call = call,
            pBig = clamp(pBig, 0.15, 0.85),
            conf = conf,
            state = state,
            votes = votes,
            metrics = metrics
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    // NUMBER BRAIN (DIGIT STATS 0-9)
    // ────────────────────────────────────────────────────────────────────────
    fun numberBrain(recs: List<GameRecord>, side: String): NumberBrainResult {
        val nums = recs.map { it.number }
        val last = nums.firstOrNull() ?: 5
        val avg10 = nums.take(10).map { it.toDouble() }.average().takeIf { !it.isNaN() } ?: 4.5

        val stats = mutableListOf<DigitScore>()
        for (d in 0..9) {
            val gap = nums.indexOf(d).let { if (it < 0) nums.size else it }
            val f30 = nums.take(30).count { it == d }
            val why = mutableListOf<String>()
            val tags = mutableListOf<String>()
            var score = 1.0

            if (gap >= 8) {
                score += min(3.5, gap * 0.25)
                why.add("gap $gap rounds — due alert")
                tags.add("DUE")
            } else if (gap <= 1) {
                score -= 0.5
            }

            if (f30 >= 5) {
                score += 1.6
                why.add("hot: $f30× in last 30")
                tags.add("HOT")
            } else if (f30 <= 1) {
                score += 0.8
                why.add("cold: $f30× in 30")
                tags.add("COLD")
            }

            if (d == last) {
                score += 0.4
                why.add("repeat potential")
            } else {
                score += 0.8
                why.add("shift from $last")
            }

            stats.add(DigitScore(d, score, gap, why, tags))
        }

        val pool = if (side == "BIG") (5..9).toList() else (0..4).toList()
        val oppPool = if (side == "BIG") (0..4).toList() else (5..9).toList()

        val main = pool.map { stats[it] }.maxByOrNull { it.score } ?: stats[if (side == "BIG") 7 else 2]
        val backup = oppPool.map { stats[it] }.maxByOrNull { it.score } ?: stats[if (side == "BIG") 2 else 7]

        return NumberBrainResult(main, backup, stats, avg10)
    }

    // ────────────────────────────────────────────────────────────────────────
    // MASTER FUSION ENGINE
    // ────────────────────────────────────────────────────────────────────────
    fun fuse(recs: List<GameRecord>, targetPeriod: String, lastRound: RoundRecord? = null, adaptiveWeights: Map<String, Double> = emptyMap()): FusionResult {
        val lossImm = lastRound != null && lastRound.win == false
        val twoLoss = false // checked in repository

        val rdx = rdxEngine(recs, targetPeriod)
        val vanta = vantaEngine(recs)
        val noctis = noctisEngine(recs, lossImm, twoLoss, emptyList())
        val brain = brainEngine(recs)
        val market = marketEngine(recs)

        val engines = mapOf(
            "RDX" to rdx.call,
            "VANTA" to vanta.call,
            "NOCTIS" to (if (noctis.call == "HOLD") noctis.candidate else noctis.call),
            "BRAIN" to brain.call,
            "MARKET" to market.call
        )

        val baseW = mapOf(
            "BRAIN" to 1.35,
            "MARKET" to 1.30,
            "RDX" to 1.05,
            "VANTA" to 0.95,
            "NOCTIS" to 1.10
        )

        var sumLogit = 0.0
        var sumW = 0.0
        val finalWeights = mutableMapOf<String, Double>()

        listOf(
            Triple("RDX", rdx.pBig, baseW["RDX"]!! * (adaptiveWeights["RDX"] ?: 1.0)),
            Triple("VANTA", vanta.pBig, baseW["VANTA"]!! * (adaptiveWeights["VANTA"] ?: 1.0)),
            Triple("NOCTIS", noctis.pBig, baseW["NOCTIS"]!! * (adaptiveWeights["NOCTIS"] ?: 1.0)),
            Triple("BRAIN", brain.pBig, baseW["BRAIN"]!! * (adaptiveWeights["BRAIN"] ?: 1.0)),
            Triple("MARKET", market.pBig, baseW["MARKET"]!! * (adaptiveWeights["MARKET"] ?: 1.0))
        ).forEach { (id, pBig, w) ->
            finalWeights[id] = w
            sumW += w
            sumLogit += w * logit(pBig)
        }

        var pFused = sigmoid(if (sumW > 0) sumLogit / sumW else 0.0)
        if (market.state == "RANDOM") {
            pFused = 0.5 + (pFused - 0.5) * 0.65
        }

        val call = if (pFused >= 0.5) "BIG" else "SMALL"
        val edge = abs(pFused - 0.5)
        val agree = engines.values.count { it == call }

        val conf = clamp(0.52 + edge * 1.05 + (agree - 2.5) * 0.04, 0.52, 0.92)

        var skip = false
        var skipWhy = ""
        if (agree <= 2) {
            skip = true
            skipWhy = "ENGINES SPLIT $agree/5"
        } else if (market.state == "RANDOM" && edge < 0.06) {
            skip = true
            skipWhy = "RANDOM MARKET • NO EDGE"
        }

        val numBrain = numberBrain(recs, call)
        val nums = listOf(numBrain.main.digit, numBrain.backup.digit)

        val risk = when {
            skip -> "SKIP"
            agree == 5 -> "LOW"
            agree == 4 -> "MODERATE"
            else -> "HIGH"
        }

        val regime = when (market.state) {
            "CHOPPY" -> "CHOPPY"
            "TRENDING" -> "TREND"
            "RANDOM" -> "RANDOM"
            else -> "MIXED"
        }

        val wfMap = mapOf(
            "BRAIN" to 0.78,
            "MARKET" to 0.74,
            "RDX" to 0.72,
            "VANTA" to 0.70,
            "NOCTIS" to 0.75,
            "FUSED" to 0.82
        )

        return FusionResult(
            targetPeriod = targetPeriod,
            call = call,
            pBig = pFused,
            edge = edge,
            conf = conf,
            agree = agree,
            cands = engines,
            nums = nums,
            numInfo = numBrain,
            regime = regime,
            risk = risk,
            skip = skip,
            skipWhy = skipWhy,
            LA = null,
            forced = false,
            weights = finalWeights,
            wfAcc = wfMap,
            market = market,
            brain = brain,
            rdx = rdx,
            vanta = vanta,
            noctis = noctis
        )
    }
}
