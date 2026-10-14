package app.deliversmart.ui.onboarding

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import app.deliversmart.ui.theme.*

@Composable
fun PermissionsScreen(onDone: () -> Unit) {
    val context = LocalContext.current

    var overlayGranted  by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var storageGranted  by remember { mutableStateOf(false) }
    var notifGranted    by remember { mutableStateOf(false) }

    val storageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> storageGranted = granted }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> notifGranted = granted }

    val overlayLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { overlayGranted = Settings.canDrawOverlays(context) }

    fun requestAll() {
        if (!Settings.canDrawOverlays(context)) {
            overlayLauncher.launch(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
            )
        } else {
            overlayGranted = true
        }
        val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
        storageLauncher.launch(storagePermission)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            notifGranted = true
        }
    }

    Column(
        Modifier.fillMaxSize().background(Background)
    ) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            Text("ONE TIME SETUP", color = Teal, fontSize = 9.sp,
                letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("Two quick\npermissions.", color = TextPrimary, fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp, lineHeight = 30.sp)
            Spacer(Modifier.height(8.dp))
            Text("DeliverSmart needs these to detect your screenshots and show the rating on top of your screen.",
                color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(20.dp))

            /* Permission cards */
            PermCard(
                icon = "🖥",
                iconBg = Teal.copy(0.1f),
                name = "Display over other apps",
                why = "So the rating card can appear on top of Zomato or Swiggy when you take a screenshot.",
                badge = if (overlayGranted) "Granted" else "Required",
                badgeBg = if (overlayGranted) Teal.copy(0.12f) else Teal.copy(0.12f),
                badgeColor = Teal,
                granted = overlayGranted
            )
            Spacer(Modifier.height(10.dp))
            PermCard(
                icon = "🖼",
                iconBg = Color(0xFF818CF8).copy(0.1f),
                name = "Read screenshots",
                why = "So we can read offer details the moment you screenshot. We only access the latest image, nothing else.",
                badge = if (storageGranted) "Granted" else "Required",
                badgeBg = Color(0xFF818CF8).copy(0.12f),
                badgeColor = Color(0xFF818CF8),
                granted = storageGranted
            )
            Spacer(Modifier.height(10.dp))
            PermCard(
                icon = "🔔",
                iconBg = Amber.copy(0.1f),
                name = "Notifications",
                why = "Optional. If the popup cannot appear, we send a quick notification with the rating instead.",
                badge = if (notifGranted) "Granted" else "Optional",
                badgeBg = Amber.copy(0.1f),
                badgeColor = Amber,
                granted = notifGranted
            )

            Spacer(Modifier.height(14.dp))

            /* Note */
            Row(
                Modifier.fillMaxWidth()
                    .background(TealDim, RoundedCornerShape(12.dp))
                    .border(1.dp, Teal.copy(0.15f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text("🛡", fontSize = 15.sp)
                Spacer(Modifier.width(9.dp))
                Text(
                    "We only use these to show your rating. We never read your gallery or access your contacts.",
                    color = Color(0xFF3A4A44), fontSize = 11.sp, lineHeight = 16.sp
                )
            }
        }

        /* Bottom */
        Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            Button(
                onClick = {
                    requestAll()
                    if (overlayGranted && storageGranted) onDone()
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) {
                Text(
                    if (overlayGranted && storageGranted) "Get started →"
                    else "Grant permissions and get started",
                    fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White
                )
            }
            OnboardingDots(total = 3, current = 2)
        }
    }
}

@Composable
fun PermCard(
    icon: String, iconBg: Color, name: String, why: String,
    badge: String, badgeBg: Color, badgeColor: Color, granted: Boolean
) {
    Row(
        Modifier.fillMaxWidth()
            .background(Color(0xFF111111), RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (granted) Teal.copy(0.3f) else Border,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier.size(40.dp).background(iconBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 20.sp) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, color = Color(0xFFDDDDDD),
                    fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (granted) Text("✓", color = Teal,
                    fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text(why, color = Color(0xFF555555), fontSize = 11.sp, lineHeight = 16.sp)
            Spacer(Modifier.height(7.dp))
            Text(
                badge,
                color = badgeColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(badgeBg, RoundedCornerShape(6.dp))
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            )
        }
    }
}