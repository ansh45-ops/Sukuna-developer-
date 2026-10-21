package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.data.ThemePreset
import com.example.ui.SukunaViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: SukunaViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val winCount by viewModel.winCount.collectAsState()
    val lossCount by viewModel.lossCount.collectAsState()
    val total = winCount + lossCount
    val accStr = if (total > 0) "${(winCount.toDouble() / total * 100).toInt()}%" else "0%"
    val primary = MaterialTheme.colorScheme.primary
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .padding(bottom = 90.dp)
            .testTag("profile_screen"),
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
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "PROFILE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.6.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ACCOUNT • THEME CUSTOMIZATION",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Muted
                )
            }
        }

        // Profile Hero
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(primary, SukunaCrimsonDark)))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("SU", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = viewModel.userName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(primary.copy(alpha = 0.15f))
                            .border(1.dp, primary.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = primary, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("NOVIX V1 OWNER", fontSize = 8.sp, fontWeight = FontWeight.Black, color = primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Stats 3-Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfileStatBox(label = "WINS", value = "$winCount", valColor = SukunaWin, modifier = Modifier.weight(1f))
                ProfileStatBox(label = "LOSSES", value = "$lossCount", valColor = SukunaLoss, modifier = Modifier.weight(1f))
                ProfileStatBox(label = "ACCURACY", value = accStr, valColor = primary, modifier = Modifier.weight(1f))
            }
        }

        // Theme Customization
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "THEME CUSTOMIZATION",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))

            val presets = ThemePreset.values()
            // 4 columns grid
            for (row in 0 until (presets.size + 3) / 4) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until 4) {
                        val index = row * 4 + col
                        if (index < presets.size) {
                            val preset = presets[index]
                            val isSelected = currentTheme == preset
                            ThemeSwatchItem(
                                preset = preset,
                                isSelected = isSelected,
                                onClick = { viewModel.setTheme(preset) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Settings & Options Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "SETTINGS & SYSTEM OPTIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            ProfileMenuItem(
                icon = Icons.Default.Laptop,
                title = "Device Information",
                subtitle = "ID: YNX-2026",
                onClick = {}
            )
            ProfileMenuItem(
                icon = Icons.Default.PlayCircle,
                title = "Demo Mode Simulation",
                subtitle = "TEST",
                onClick = { viewModel.startDemo() }
            )
            ProfileMenuItem(
                icon = Icons.Default.CleaningServices,
                title = "Clear Cache & Repair Seeds",
                subtitle = "REPAIR",
                onClick = { viewModel.clearCache() }
            )
            ProfileMenuItem(
                icon = Icons.Default.Refresh,
                title = "Reset Session Statistics",
                subtitle = "RESET",
                onClick = { viewModel.resetStats() }
            )
        }
    }
}

@Composable
fun ProfileStatBox(
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
            Text(label, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Muted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = valColor)
        }
    }
}

@Composable
fun ThemeSwatchItem(
    preset: ThemePreset,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(preset.primaryHex)
    val secondaryColor = Color(preset.secondaryHex)

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(listOf(primaryColor, secondaryColor)))
            .border(
                if (isSelected) 2.5.dp else 1.dp,
                if (isSelected) Color.White else Color.Transparent,
                RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(5.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(10.dp))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = preset.title.takeWhile { it != ' ' },
                fontSize = 7.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard2)
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = InkLight, modifier = Modifier.weight(1f))
        Text(subtitle, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Muted)
    }
}
