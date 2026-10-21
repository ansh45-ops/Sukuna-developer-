package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SukunaViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.MetaRow
import com.example.ui.theme.*

@Composable
fun EngineScreen(
    viewModel: SukunaViewModel,
    modifier: Modifier = Modifier
) {
    val fusion by viewModel.currentFusion.collectAsState()
    val scrollState = rememberScrollState()
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .padding(bottom = 90.dp)
            .testTag("engine_analysis_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Subhead
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ENGINE ANALYSIS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.6.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "5-LOGIC MULTI-BRAIN CONSENSUS • LIVE BREAKDOWN",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
            }
        }

        if (fusion == null) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NO ANALYSIS YET", fontSize = 13.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Start the Fusion Engine or Demo from Home tab.", fontSize = 9.5.sp, color = Muted)
                    }
                }
            }
            return
        }

        val f = fusion!!
        val isBig = f.call == "BIG"
        val callColor = if (f.skip) SukunaSkip else if (isBig) SukunaGold else SukunaCyan

        // Fusion Verdict Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("FUSION VERDICT", fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurface)
                Text("#${f.targetPeriod.takeLast(5)}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = primary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            MetaRow(
                k1 = "CALL", v1 = if (f.skip) "SKIP" else f.call,
                k2 = "P(BIG)", v2 = "${(f.pBig * 100).toInt()}%",
                k3 = "CONFIDENCE", v3 = "${(f.conf * 100).toInt()}%",
                color3 = callColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            MetaRow(
                k1 = "AGREEMENT", v1 = "${f.agree}/5",
                k2 = "RISK", v2 = f.risk,
                k3 = "REGIME", v3 = f.regime,
                color3 = primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Main / Backup numbers box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(primary.copy(alpha = 0.1f))
                    .border(1.dp, primary.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = "TARGET DIGITS: MAIN [${f.nums[0]}] • BACKUP [${f.nums[1]}]${if (f.skip) " • " + f.skipWhy else ""}",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary
                )
            }
        }

        // Engine Weights Breakdown
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("ADAPTIVE WEIGHTS & ACCURACY", fontSize = 10.5.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))

            val engineNames = listOf(
                "BRAIN" to "SUKUNA BRAIN",
                "MARKET" to "SUKUNA MARKET",
                "RDX" to "SUKUNA CORE",
                "VANTA" to "SUKUNA VISION",
                "NOCTIS" to "SUKUNA GUARD"
            )

            engineNames.forEach { (id, label) ->
                val cand = f.cands[id] ?: "—"
                val candColor = if (cand == "BIG") SukunaGold else if (cand == "SMALL") SukunaCyan else Muted
                val weight = f.weights[id] ?: 1.0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkCard2)
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = InkLight)
                        Text("Weight factor: ${"%.2f".format(weight)}", fontSize = 7.5.sp, color = Muted)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(candColor.copy(alpha = 0.15f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(cand, fontSize = 9.5.sp, fontWeight = FontWeight.Black, color = candColor)
                    }
                }
            }
        }

        // Number Brain Grid
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("NUMBER BRAIN (DIGITS 0-9)", fontSize = 10.5.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                f.numInfo.stats.take(5).forEach { stat ->
                    DigitStatBox(stat = stat, isPicked = stat.digit in f.nums, modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                f.numInfo.stats.drop(5).take(5).forEach { stat ->
                    DigitStatBox(stat = stat, isPicked = stat.digit in f.nums, modifier = Modifier.weight(1f))
                }
            }
        }

        // Sukuna Market Detail
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("SUKUNA MARKET — ${f.market.state}", fontSize = 10.5.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            val m = f.market.metrics
            MetaRow(
                k1 = "ALT 20", v1 = "${(m.alt20 * 100).toInt()}%",
                k2 = "AVG RUN", v2 = "%.1f".format(m.avgRun),
                k3 = "CUR RUN", v3 = "${m.curRun}× ${m.curSide}",
                color3 = SukunaCyan
            )
            Spacer(modifier = Modifier.height(6.dp))
            MetaRow(
                k1 = "BIG MISS", v1 = "${m.missB.cur}",
                k2 = "SMALL MISS", v2 = "${m.missS.cur}",
                k3 = "P(CONT)", v3 = "${((m.pCont ?: 0.5) * 100).toInt()}%",
                color3 = SukunaGold
            )
        }

        // Sukuna Brain Detail
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("SUKUNA BRAIN — ${f.brain.pattern}", fontSize = 10.5.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = f.brain.patternNote,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = InkSoft
            )
            Spacer(modifier = Modifier.height(6.dp))
            MetaRow(
                k1 = "HUMAN READ", v1 = "${f.brain.humanCall} ${f.brain.humanConf}%",
                k2 = "AI MEMORY", v2 = "${f.brain.aiCall} ${f.brain.aiConf}%",
                k3 = "VERDICT", v3 = f.brain.verdict,
                color3 = SukunaWin
            )
        }
    }
}

@Composable
fun DigitStatBox(
    stat: com.example.data.DigitScore,
    isPicked: Boolean,
    modifier: Modifier = Modifier
) {
    val isBig = stat.digit >= 5
    val numColor = if (isBig) SukunaGold else SukunaCyan

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isPicked) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else DarkCard2)
            .border(
                if (isPicked) 1.5.dp else 1.dp,
                if (isPicked) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(8.dp)
            )
            .padding(vertical = 5.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${stat.digit}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = numColor)
            Text(text = "g:${stat.gap}", fontSize = 7.5.sp, color = Muted)
            Text(text = "%.1f".format(stat.score), fontSize = 7.sp, fontWeight = FontWeight.Bold, color = InkSoft)
        }
    }
}
