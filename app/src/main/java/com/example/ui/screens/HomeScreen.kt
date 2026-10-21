package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameMode
import com.example.ui.SukunaViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: SukunaViewModel,
    modifier: Modifier = Modifier
) {
    val selectedMode by viewModel.selectedMode.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val isDemo by viewModel.isDemo.collectAsState()
    val isAnalysing by viewModel.isAnalysing.collectAsState()
    val fusion by viewModel.currentFusion.collectAsState()
    val step by viewModel.step.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val winCount by viewModel.winCount.collectAsState()
    val lossCount by viewModel.lossCount.collectAsState()
    val secondsRemaining by viewModel.secondsRemaining.collectAsState()

    val total = winCount + lossCount
    val accStr = if (total > 0) "${(winCount.toDouble() / total * 100).toInt()}%" else "0%"
    val timerStr = "00:${secondsRemaining.toString().padStart(2, '0')}"

    val primary = MaterialTheme.colorScheme.primary
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .padding(bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Brand Strip
        SukunaBrandStrip(isReady = isRunning)

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
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SUKUNA LIVE PREDICTION",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "SUKUNA BRAIN • MARKET • CORE • VISION • GUARD",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(SukunaWin.copy(alpha = 0.12f))
                    .border(1.dp, SukunaWin.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isRunning) "● LIVE" else "READY",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRunning) SukunaWin else SukunaCyan
                )
            }
        }

        // Main Hero Prediction Card
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_prediction_card")
        ) {
            // Status bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard2)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isAnalysing) SukunaGold else if (isRunning) SukunaWin else Muted)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = if (isAnalysing) "ANALYSING..." else if (isRunning) "ENGINE RUNNING" else "READY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = InkSoft
                )
                Spacer(modifier = Modifier.weight(1f))
                val levelText = when {
                    !isRunning -> "LV 0 ● IDLE"
                    step >= 3 -> "LV 3 ● ELITE LOCK"
                    step == 2 -> "LV 2 ● RECOVERY"
                    else -> "LV 1 ● ACTIVE"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(primary.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(text = levelText, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = primary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Streak ribbon if streak >= 2
            if (currentStreak >= 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SukunaWin)
                        .padding(vertical = 4.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+$currentStreak WIN STREAK",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF04130B),
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            } else if (step > 1 && isRunning) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SukunaLoss)
                        .padding(vertical = 4.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RECOVERY MODE • STEP $step",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Dial and Side Prediction Box
            CoreDialWithPrediction(
                prediction = fusion?.let { if (it.skip) "SKIP" else it.call } ?: if (isRunning) "READY" else "READY",
                confidence = fusion?.conf ?: 0.65,
                step = step,
                agreement = "${fusion?.agree ?: 4}/5",
                isAnalysing = isAnalysing,
                mainNum = fusion?.nums?.getOrNull(0),
                backupNum = fusion?.nums?.getOrNull(1)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 5 Engine Mini Chips
            val b = fusion?.brain
            val m = fusion?.market
            val r = fusion?.rdx
            val v = fusion?.vanta
            val n = fusion?.noctis

            EngineChipsRow(
                brainCall = b?.call ?: "—",
                brainConf = b?.conf ?: 0,
                marketCall = m?.call ?: "—",
                marketConf = m?.conf ?: 0,
                rdxCall = r?.call ?: "—",
                rdxConf = r?.conf ?: 0,
                vantaCall = v?.call ?: "—",
                vantaConf = v?.conf ?: 0,
                noctisCall = n?.call ?: "—",
                noctisConf = n?.conf ?: 0
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Roadbox: Last 10 Bead sequence
            if (b != null && b.road.isNotEmpty()) {
                RoadBeadsView(
                    road = b.road,
                    nextCall = fusion?.let { if (it.skip) "SKIP" else it.call } ?: "—"
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${b.pattern} • ${b.patternNote}",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = primary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Confidence meter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "FUSION CONFIDENCE", fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = Muted)
                Text(text = "${((fusion?.conf ?: 0.65) * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Black, color = primary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            ConfidenceBar(confidence = fusion?.conf ?: 0.65)

            Spacer(modifier = Modifier.height(12.dp))

            // Meta Rows
            MetaRow(
                k1 = "PERIOD", v1 = fusion?.targetPeriod?.takeLast(5) ?: "—",
                k2 = "MODE", v2 = selectedMode.displayName,
                k3 = "TIMER", v3 = timerStr,
                color3 = primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            MetaRow(
                k1 = "REGIME", v1 = fusion?.regime ?: "MIXED",
                k2 = "RISK", v2 = fusion?.risk ?: "LOW",
                k3 = "ACCURACY", v3 = accStr,
                color3 = SukunaWin
            )
        }

        // Controls Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "SELECT GAME MODE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                color = Muted
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Game Mode Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                GameMode.values().forEach { mode ->
                    val isSelected = selectedMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .background(
                                if (isSelected) Brush.linearGradient(listOf(primary, SukunaCrimsonDark))
                                else Brush.linearGradient(listOf(DarkCard2, DarkCard))
                            )
                            .border(
                                1.dp,
                                if (isSelected) primary else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(13.dp)
                            )
                            .clickable { viewModel.selectMode(mode) }
                            .testTag("mode_button_${mode.id}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp,
                            color = if (isSelected) Color.White else Muted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mode Type Chips (Big / Small active, Red / Green server down)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .background(primary.copy(alpha = 0.12f))
                        .border(1.dp, primary.copy(alpha = 0.5f), RoundedCornerShape(11.dp))
                        .padding(vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "● BIG / SMALL",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = primary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color(0xFFF0A34C).copy(alpha = 0.08f))
                        .border(1.dp, Color(0xFFF0A34C).copy(alpha = 0.4f), RoundedCornerShape(11.dp))
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "● RED / GREEN",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE8B77C)
                        )
                        Text(
                            text = "SERVER DOWN",
                            fontSize = 6.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp,
                            color = Color(0xFFF0A34C)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Start / Stop Engine Button
            Button(
                onClick = { viewModel.toggleEngine() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("start_engine_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) SukunaLoss else primary
                )
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isRunning) "■ STOP FUSION ENGINE" else "▶ START FUSION ENGINE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Demo Mode Button
            OutlinedButton(
                onClick = { viewModel.startDemo() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("demo_mode_button"),
                shape = RoundedCornerShape(13.dp),
                border = BorderStroke(1.dp, SukunaAccent.copy(alpha = 0.45f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SukunaAccent
                )
            ) {
                Text(
                    text = if (isDemo) "▷ RUNNING OFFLINE DEMO" else "▷ TRY OFFLINE DEMO",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
            }
        }
    }
}
