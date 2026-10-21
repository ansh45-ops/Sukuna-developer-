package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
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
import com.example.data.RoundRecord
import com.example.ui.SukunaViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun HistoryScreen(
    viewModel: SukunaViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.historyList.collectAsState()
    val winCount by viewModel.winCount.collectAsState()
    val lossCount by viewModel.lossCount.collectAsState()
    val bestStreak by viewModel.bestStreak.collectAsState()
    val primary = MaterialTheme.colorScheme.primary

    val total = winCount + lossCount
    val accStr = if (total > 0) "${(winCount.toDouble() / total * 100).toInt()}%" else "0%"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .padding(bottom = 85.dp)
            .testTag("history_screen")
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
                Icon(imageVector = Icons.Default.History, contentDescription = null, tint = primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "HISTORY",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.6.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "WIN & LOSS PREDICTION RECORDS",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "${historyList.size} ROUNDS",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Summary 4-Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SummaryBox("WINS", "$winCount", SukunaWin, Modifier.weight(1f))
            SummaryBox("LOSSES", "$lossCount", SukunaLoss, Modifier.weight(1f))
            SummaryBox("ACCURACY", accStr, primary, Modifier.weight(1f))
            SummaryBox("BEST", "$bestStreak", SukunaGold, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Last 20 outcome trail
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("LAST 20", fontSize = 8.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, color = Muted)
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    val last20 = historyList.take(20).reversed()
                    for (i in 0 until 20) {
                        val item = last20.getOrNull(i)
                        val dotColor = when {
                            item == null -> Color.White.copy(alpha = 0.08f)
                            item.skip -> SukunaSkip
                            item.jackpot -> Color(0xFFB99AFF)
                            item.win == true -> SukunaWin
                            else -> SukunaLoss
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(dotColor)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List of Rounds
        if (historyList.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NO ROUNDS YET", fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Start the engine from Home to generate live verified records.", fontSize = 9.sp, color = Muted)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(historyList, key = { it.id }) { item ->
                    RoundHistoryCard(item)
                }
            }
        }
    }
}

@Composable
fun SummaryBox(
    label: String,
    value: String,
    valColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard2)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = Muted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = valColor)
        }
    }
}

@Composable
fun RoundHistoryCard(
    record: RoundRecord,
    modifier: Modifier = Modifier
) {
    val isWin = record.win == true
    val isSkip = record.skip
    val isJackpot = record.jackpot

    val statusColor = when {
        isSkip -> SukunaSkip
        isJackpot -> Color(0xFFB99AFF)
        isWin -> SukunaWin
        else -> SukunaLoss
    }

    val statusText = when {
        isSkip -> "SKIP"
        isJackpot -> "JACKPOT"
        isWin -> "WIN"
        else -> "LOSS"
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        borderColor = statusColor.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon Box
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (isJackpot) Icons.Default.Star else if (isWin) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = statusText,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("#${record.period.takeLast(5)}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(record.mode, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Muted)
                        Text("•", fontSize = 8.sp, color = Muted)
                        Text("STEP ${record.step}", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PRED: ${record.prediction} [${record.predNums.joinToString("&")}]",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkSoft
                    )
                    Text(
                        text = "ACTUAL: ${record.actual} (${record.actualNum})",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isWin) SukunaWin else SukunaLoss
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "SUKUNA NOVIX V1 • CONF ${record.conf}%",
                    fontSize = 7.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Muted
                )
            }
        }
    }
}
