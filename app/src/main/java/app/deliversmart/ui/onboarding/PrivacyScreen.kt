package app.deliversmart.ui.onboarding

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import app.deliversmart.ui.theme.*

@Composable
fun PrivacyScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Background)
    ) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onSkip) {
                    Text("Skip", color = TextMuted, fontSize = 12.sp)
                }
            }

            Text("YOUR PRIVACY", color = Teal, fontSize = 9.sp,
                letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("Your data never\nleaves your phone.", color = TextPrimary, fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp, lineHeight = 30.sp)
            Spacer(Modifier.height(8.dp))
            Text("Everything runs locally. Always. No exceptions.",
                color = TextSecondary, fontSize = 13.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(20.dp))

            /* Hero card */
            Column(
                Modifier.fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF0D1412), Color(0xFF091812))),
                        RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, Teal.copy(0.25f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier.size(52.dp)
                        .background(Teal.copy(0.12f), RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("🔒", fontSize = 26.sp) }
                Spacer(Modifier.height(12.dp))
                Text("100% On-Device", color = TextPrimary, fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.4).sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Screenshots are read on your phone and never sent anywhere. Not to us, not to anyone.",
                    color = Color(0xFF3A4A44), fontSize = 12.sp,
                    lineHeight = 17.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(Modifier.height(14.dp))

            /* Privacy list */
            Column(
                Modifier.fillMaxWidth()
                    .background(Color(0xFF111111), RoundedCornerShape(14.dp))
                    .border(1.dp, Border, RoundedCornerShape(14.dp))
            ) {
                val items = listOf(
                    Triple("📵", Teal.copy(0.1f), Pair("No uploads. Ever.",
                        "Your screenshots are read on your phone and never sent to any server.")),
                    Triple("👤", Color(0xFF1C1C1E), Pair("No account required",
                        "Works completely offline. No sign-up, no login, ever.")),
                    Triple("👁", Color(0xFF1C1C1E), Pair("No background tracking",
                        "Only activates when you screenshot. Nothing else.")),
                    Triple("📢", Color(0xFF1C1C1E), Pair("No ads. No data selling.",
                        "We never have and never will.")),
                )
                items.forEachIndexed { index, (icon, iconBg, text) ->
                    Row(
                        Modifier.fillMaxWidth()
                            .then(if (index < items.size - 1)
                                Modifier.border(BorderStroke(1.dp, Color(0xFF161616)))
                            else Modifier)
                            .padding(13.dp, 13.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            Modifier.size(30.dp).background(iconBg, RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text(icon, fontSize = 15.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(text.first, color = Color(0xFFE5E5EA),
                                fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(3.dp))
                            Text(text.second, color = Color(0xFF48484A),
                                fontSize = 11.sp, lineHeight = 15.sp)
                        }
                    }
                }
            }
        }

        /* Bottom */
        Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            OnboardingButton("Next — permissions", onNext)
            OnboardingDots(total = 3, current = 1)
        }
    }
}