package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.DarkCard2
import com.example.ui.theme.Muted
import com.example.ui.theme.SukunaWin

@Composable
fun ConnectScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gameUrl = "https://www.777bigwingame.cc/#/register?invitationCode=34423786327"
    val primary = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .padding(bottom = 85.dp)
            .testTag("game_connect_screen")
    ) {
        // Subhead
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = primary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GAME CONNECT",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.6.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "777 BIGWIN OFFICIAL • EMBEDDED CONSOLE",
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
                Text(text = "● LIVE", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = SukunaWin)
            }
        }

        // WebView Card container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, primary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            cacheMode = WebSettings.LOAD_DEFAULT
                        }
                        webViewClient = object : WebViewClient() {}
                        loadUrl(gameUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Floating Open in External Browser Button
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gameUrl))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp)
                    .testTag("open_game_browser_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primary)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = "New Tab",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "OPEN IN BROWSER",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    color = Color.White
                )
            }
        }
    }
}
