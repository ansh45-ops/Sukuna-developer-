package com.example.ui

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.WinCelebrationDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun SukunaApp(
    viewModel: SukunaViewModel = viewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val winCelebration by viewModel.winCelebration.collectAsState()

    val winCount by viewModel.winCount.collectAsState()
    val lossCount by viewModel.lossCount.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val bestStreak by viewModel.bestStreak.collectAsState()
    val total = winCount + lossCount
    val accStr = if (total > 0) "${(winCount.toDouble() / total * 100).toInt()}%" else "0%"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    SukunaTheme(preset = currentTheme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = DarkCard2,
                    drawerContentColor = InkLight,
                    modifier = Modifier.width(310.dp)
                ) {
                    // Drawer Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.Black.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "SUKUNA TOOL",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = Color.White
                            )
                            Text(
                                text = "LIQUID GLASS CONTROL CENTRE",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Live Stats Grid inside Drawer
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DrawerStatCard("WINS", "$winCount", SukunaWin, Modifier.weight(1f))
                            DrawerStatCard("LOSSES", "$lossCount", SukunaLoss, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DrawerStatCard("BEST STREAK", "$bestStreak", SukunaGold, Modifier.weight(1f))
                            DrawerStatCard("STREAK", "$currentStreak", SukunaCyan, Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        DrawerStatCard("ACCURACY", accStr, MaterialTheme.colorScheme.primary, Modifier.fillMaxWidth())
                    }

                    Divider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(horizontal = 14.dp))

                    // Menu Items
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(10.dp)
                    ) {
                        DrawerMenuItem(
                            icon = Icons.Default.Home,
                            label = "HOME",
                            onClick = {
                                viewModel.switchTab("home")
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerMenuItem(
                            icon = Icons.Default.Psychology,
                            label = "ENGINE ANALYSIS",
                            onClick = {
                                viewModel.switchTab("engine")
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerMenuItem(
                            icon = Icons.Default.SportsEsports,
                            label = "GAME CONNECT",
                            onClick = {
                                viewModel.switchTab("connect")
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerMenuItem(
                            icon = Icons.Default.History,
                            label = "HISTORY",
                            onClick = {
                                viewModel.switchTab("history")
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerMenuItem(
                            icon = Icons.Default.Person,
                            label = "PROFILE",
                            onClick = {
                                viewModel.switchTab("profile")
                                scope.launch { drawerState.close() }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "SYSTEM ACTIONS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            color = Muted,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        DrawerMenuItem(
                            icon = Icons.Default.Refresh,
                            label = "RESET STATS",
                            onClick = {
                                viewModel.resetStats()
                                scope.launch { drawerState.close() }
                            }
                        )
                        DrawerMenuItem(
                            icon = Icons.Default.CleaningServices,
                            label = "CLEAR CACHE",
                            onClick = {
                                viewModel.clearCache()
                                scope.launch { drawerState.close() }
                            }
                        )
                    }

                    // Drawer Footer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SUKUNA TOOL © 2026 • DOM OF HACK",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Muted
                        )
                    }
                }
            }
        ) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    SukunaTopBar(
                        isRunning = isRunning,
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                },
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Active Tab Screen
                    when (activeTab) {
                        "home" -> HomeScreen(viewModel = viewModel)
                        "engine" -> EngineScreen(viewModel = viewModel)
                        "connect" -> ConnectScreen()
                        "history" -> HistoryScreen(viewModel = viewModel)
                        "profile" -> ProfileScreen(viewModel = viewModel)
                    }

                    // Bottom Floating iOS-Style Liquid Glass Dock
                    LiquidGlassDock(
                        activeTab = activeTab,
                        onTabSelect = { viewModel.switchTab(it) },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    )

                    // Win celebration modal popup
                    winCelebration?.let { record ->
                        WinCelebrationDialog(
                            record = record,
                            onDismiss = { viewModel.closeWinCelebration() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SukunaTopBar(
    isRunning: Boolean,
    onMenuClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hamburger Menu Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(DarkCard2)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(13.dp))
                .clickable(onClick = onMenuClick)
                .testTag("topbar_menu_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(modifier = Modifier.width(16.dp).height(2.dp).background(primary, CircleShape))
                Box(modifier = Modifier.width(11.dp).height(2.dp).background(primary, CircleShape))
                Box(modifier = Modifier.width(16.dp).height(2.dp).background(primary, CircleShape))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "SUKUNA TOOL",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "LIQUID GLASS · PREDICTION LAB",
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Muted
            )
        }

        // Live status badge
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(primary.copy(alpha = 0.12f))
                .border(1.dp, primary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isRunning) SukunaWin else SukunaGold)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (isRunning) "ONLINE" else "READY",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Black,
                color = primary
            )
        }
    }
}

@Composable
fun DrawerStatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = label, fontSize = 7.5.sp, fontWeight = FontWeight.Bold, color = Muted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = valueColor)
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Black, color = InkLight)
    }
}

@Composable
fun LiquidGlassDock(
    activeTab: String,
    onTabSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(16.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(DarkCard.copy(alpha = 0.95f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(24.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockItem(id = "home", label = "Home", icon = Icons.Default.Home, isSelected = activeTab == "home", onClick = { onTabSelect("home") })
            DockItem(id = "engine", label = "Engine", icon = Icons.Default.Psychology, isSelected = activeTab == "engine", onClick = { onTabSelect("engine") })
            DockItem(id = "connect", label = "Connect", icon = Icons.Default.SportsEsports, isSelected = activeTab == "connect", onClick = { onTabSelect("connect") })
            DockItem(id = "history", label = "History", icon = Icons.Default.History, isSelected = activeTab == "history", onClick = { onTabSelect("history") })
            DockItem(id = "profile", label = "Profile", icon = Icons.Default.Person, isSelected = activeTab == "profile", onClick = { onTabSelect("profile") })
        }
    }
}

@Composable
fun DockItem(
    id: String,
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isSelected) Modifier.background(Brush.linearGradient(listOf(primary, SukunaCrimsonDark)))
                else Modifier.background(Color.Transparent)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("dock_item_$id"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Muted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                color = if (isSelected) Color.White else Muted
            )
        }
    }
}
