package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GameRecord
import com.example.data.RoundRecord
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f),
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .shadow(12.dp, shape, ambientColor = Color.Black.copy(alpha = 0.4f))
            .border(BorderStroke(1.dp, borderColor), shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

@Composable
fun SukunaBrandStrip(
    modifier: Modifier = Modifier,
    isReady: Boolean = true
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                        primary.copy(alpha = 0.08f)
                    )
                )
            )
            .border(1.dp, primary.copy(alpha = 0.35f), RoundedCornerShape(22.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Kanji emblem
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(Brush.linearGradient(listOf(primary, SukunaCrimsonDark)))
                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "宿",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "DOM OF HACK · DIGITAL STUDIO",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
                color = SukunaCrimsonLight
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SUKUNA ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "TOOL",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = primary
                )
            }
            Text(
                text = "A GLASSY PREDICTION EXPERIENCE",
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp,
                color = Muted
            )
        }

        // Status badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SukunaWin.copy(alpha = 0.12f))
                .border(1.dp, SukunaWin.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(SukunaWin)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (isReady) "CORE READY" else "IDLE",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = SukunaWin
            )
        }
    }
}

@Composable
fun CoreDialWithPrediction(
    prediction: String,
    confidence: Double,
    step: Int,
    agreement: String,
    isAnalysing: Boolean,
    mainNum: Int?,
    backupNum: Int?,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val infiniteTransition = rememberInfiniteTransition(label = "dial_anim")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Side Glossy Prediction Result Box
        val isBig = prediction.equals("BIG", ignoreCase = true)
        val isSmall = prediction.equals("SMALL", ignoreCase = true)
        val isSkip = prediction.equals("SKIP", ignoreCase = true)

        val resultBg = when {
            isBig -> SukunaGold.copy(alpha = 0.18f)
            isSmall -> SukunaCyan.copy(alpha = 0.18f)
            isSkip -> SukunaSkip.copy(alpha = 0.15f)
            else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        }
        val resultBorder = when {
            isBig -> SukunaGold.copy(alpha = 0.7f)
            isSmall -> SukunaCyan.copy(alpha = 0.7f)
            isSkip -> SukunaSkip.copy(alpha = 0.7f)
            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        }
        val resultText = when {
            isBig -> "BIG"
            isSmall -> "SMALL"
            isSkip -> "SKIP"
            else -> "—"
        }
        val resultColor = when {
            isBig -> SukunaGold
            isSmall -> SukunaCyan
            isSkip -> SukunaSkip
            else -> Muted
        }

        Box(
            modifier = Modifier
                .width(115.dp)
                .height(130.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(resultBg)
                .border(BorderStroke(1.5.dp, resultBorder), RoundedCornerShape(22.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PREDICTION",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = Muted
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = resultText,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = resultColor
                )
                if (mainNum != null && backupNum != null && !isSkip) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(resultColor.copy(alpha = 0.25f))
                                .border(1.dp, resultColor.copy(alpha = 0.6f), RoundedCornerShape(7.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$mainNum", fontSize = 12.sp, fontWeight = FontWeight.Black, color = resultColor)
                        }
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(Color.White.copy(alpha = 0.08f))
                                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(7.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "$backupNum", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkSoft)
                        }
                    }
                }
            }
        }

        // Circular Core Dial
        Box(
            modifier = Modifier
                .size(160.dp)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Animated glowing circle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                listOf(primary.copy(alpha = 0.35f), Color.Transparent),
                                radius = size.minDimension / 1.8f
                            )
                        )
                        drawCircle(
                            color = primary,
                            style = Stroke(width = 3.dp.toPx())
                        )
                        drawCircle(
                            color = primary.copy(alpha = 0.4f),
                            radius = size.minDimension / 2.2f,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }
            )

            // Center Content
            Column(
                modifier = Modifier
                    .size(126.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(DarkCard2, DarkBg)
                        )
                    )
                    .border(1.dp, primary.copy(alpha = 0.5f), CircleShape)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "CREATED BY SUKUNA",
                    fontSize = 6.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.9.sp,
                    color = SukunaCrimsonLight,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isAnalysing) "ANALYSING" else if (prediction.isEmpty() || prediction == "READY") "NEXT BET" else prediction,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isSkip) SukunaSkip else if (isBig) SukunaGold else if (isSmall) SukunaCyan else Color.White
                )
                Text(
                    text = "${(confidence * 100).toInt()}% CONF",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
            }
        }
    }
}

@Composable
fun EngineChipsRow(
    brainCall: String,
    brainConf: Int,
    marketCall: String,
    marketConf: Int,
    rdxCall: String,
    rdxConf: Int,
    vantaCall: String,
    vantaConf: Int,
    noctisCall: String,
    noctisConf: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        EngineMiniChip(name = "S-BRAIN", call = brainCall, conf = brainConf, modifier = Modifier.weight(1f))
        EngineMiniChip(name = "S-MARKET", call = marketCall, conf = marketConf, modifier = Modifier.weight(1f))
        EngineMiniChip(name = "S-CORE", call = rdxCall, conf = rdxConf, modifier = Modifier.weight(1f))
        EngineMiniChip(name = "S-VISION", call = vantaCall, conf = vantaConf, modifier = Modifier.weight(1f))
        EngineMiniChip(name = "S-GUARD", call = noctisCall, conf = noctisConf, modifier = Modifier.weight(1f))
    }
}

@Composable
fun EngineMiniChip(
    name: String,
    call: String,
    conf: Int,
    modifier: Modifier = Modifier
) {
    val isB = call.equals("BIG", true)
    val isS = call.equals("SMALL", true)
    val color = when {
        isB -> SukunaGold
        isS -> SukunaCyan
        else -> Muted
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                fontSize = 6.5.sp,
                fontWeight = FontWeight.Bold,
                color = Muted,
                maxLines = 1
            )
            Text(
                text = if (call.isEmpty()) "—" else call.take(1),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Text(
                text = if (conf > 0) "$conf%" else "—",
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Bold,
                color = InkSoft
            )
        }
    }
}

@Composable
fun RoadBeadsView(
    road: List<GameRecord>,
    nextCall: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        road.takeLast(10).forEachIndexed { index, record ->
            val isBig = record.number >= 5
            val bg = if (isBig) SukunaGold else SukunaCyan
            val isLast = index == road.takeLast(10).size - 1

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(bg)
                    .border(if (isLast) 1.5.dp else 0.dp, Color.White, RoundedCornerShape(7.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${record.number}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF0B0B0F))
                    Text(text = if (isBig) "B" else "S", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0B0B0F))
                }
            }
        }

        // Predicted next bead
        val isNextB = nextCall.equals("BIG", true)
        val isNextS = nextCall.equals("SMALL", true)
        val nextColor = if (isNextB) SukunaGold else if (isNextS) SukunaCyan else SukunaSkip

        Box(
            modifier = Modifier
                .weight(1f)
                .height(34.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(nextColor.copy(alpha = 0.2f))
                .border(1.5.dp, nextColor, RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "?", fontSize = 11.sp, fontWeight = FontWeight.Black, color = nextColor)
                Text(text = nextCall.take(1).ifEmpty { "—" }, fontSize = 7.sp, fontWeight = FontWeight.Bold, color = nextColor)
            }
        }
    }
}

@Composable
fun ConfidenceBar(
    confidence: Double,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val totalSegments = 12
    val activeSegments = (confidence * totalSegments).toInt().coerceIn(0, totalSegments)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        for (i in 0 until totalSegments) {
            val isActive = i < activeSegments
            val color = if (isActive) {
                if (confidence >= 0.8) SukunaWin else primary
            } else {
                Color.White.copy(alpha = 0.12f)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun MetaRow(
    k1: String, v1: String,
    k2: String, v2: String,
    k3: String, v3: String,
    color3: Color = InkLight,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MetaBox(key = k1, value = v1, modifier = Modifier.weight(1f))
        MetaBox(key = k2, value = v2, modifier = Modifier.weight(1f))
        MetaBox(key = k3, value = v3, valueColor = color3, modifier = Modifier.weight(1f))
    }
}

@Composable
fun MetaBox(
    key: String,
    value: String,
    valueColor: Color = InkLight,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(11.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(11.dp))
            .padding(vertical = 7.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = key, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = Muted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 11.5.sp, fontWeight = FontWeight.Black, color = valueColor, maxLines = 1)
        }
    }
}

@Composable
fun WinCelebrationDialog(
    record: RoundRecord,
    onDismiss: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(DarkCard)
                .border(1.dp, primary.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(
                            Brush.linearGradient(listOf(primary, SukunaCrimsonDark, SukunaGold))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (record.jackpot) "JACKPOT HIT!" else "VICTORY!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp,
                            color = Color.White
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (record.jackpot) "NUMBER MATCH" else "PREDICTION WON",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SukunaWin
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCard2)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("PREDICTED", fontSize = 8.sp, color = Muted, fontWeight = FontWeight.Bold)
                                Text(record.prediction, fontSize = 14.sp, fontWeight = FontWeight.Black, color = primary)
                                Text("NUM: ${record.predNums.joinToString("&")}", fontSize = 9.sp, color = InkSoft)
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SukunaWin.copy(alpha = 0.15f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ACTUAL ✓", fontSize = 8.sp, color = SukunaWin, fontWeight = FontWeight.Bold)
                                Text(record.actual, fontSize = 14.sp, fontWeight = FontWeight.Black, color = SukunaWin)
                                Text("NUM: ${record.actualNum}", fontSize = 9.sp, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("win_dialog_continue_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primary)
                    ) {
                        Text("✓ CONTINUE", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
