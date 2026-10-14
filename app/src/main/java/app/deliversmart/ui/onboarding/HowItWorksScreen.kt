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
fun HowItWorksScreen(onNext: () -> Unit, onSkip: () -> Unit) {
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
            Text("HOW IT WORKS", color = Teal, fontSize = 9.sp,
                letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text("Three steps,\nthree seconds.", color = TextPrimary, fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp, lineHeight = 30.sp)
            Spacer(Modifier.height(8.dp))
            Text("No typing. No opening the app.\nJust screenshot.", color = TextSecondary,
                fontSize = 13.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(20.dp))

            /* Step 1 */
            HiwStep(number = "1", title = "Screenshot the offer",
                desc = "When an offer appears on Zomato or Swiggy, take a screenshot. That is the only thing you do.") {
                Row(
                    Modifier.fillMaxWidth().background(Background, RoundedCornerShape(10.dp)).padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SvIcon("📱", "Offer\nappears", teal = true)
                    Text("→", color = Color(0xFF2C2C2E), fontSize = 16.sp)
                    SvIcon("📷", "You\nscreenshot", teal = false)
                    Text("→", color = Color(0xFF2C2C2E), fontSize = 16.sp)
                    SvIcon("⚡", "We detect\ninstantly", teal = true)
                }
            }

            Spacer(Modifier.height(10.dp))

            /* Step 2 */
            HiwStep(number = "2", title = "We read it automatically",
                desc = "Your app reads the payout and distance from the screenshot. All on your phone, in under a second.") {
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    listOf("₹280 payout", "6.2 km", "Koramangala → Whitefield").forEach {
                        Text(it, color = Teal, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .background(Background, RoundedCornerShape(6.dp))
                                .border(1.dp, Border2, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth()
                        .background(TealDim, RoundedCornerShape(9.dp))
                        .border(1.dp, Teal.copy(0.2f), RoundedCornerShape(9.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔒", fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Everything stays on your phone. Nothing is uploaded.",
                        color = Teal, fontSize = 11.sp, lineHeight = 16.sp)
                }
            }

            Spacer(Modifier.height(10.dp))

            /* Step 3 */
            HiwStep(number = "3", title = "Popup appears instantly",
                desc = "Good, Average, or Bad with your net earnings after fuel. Decide before the offer expires.") {
                Row(Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    VerdictBox("✓ Good", "₹264 net", Teal, Modifier.weight(1f))
                    VerdictBox("~ Avg", "₹147 net", Amber, Modifier.weight(1f))
                    VerdictBox("✕ Bad", "₹38 net", Red, Modifier.weight(1f))
                }
            }
        }

        /* Bottom */
        Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            OnboardingButton("Next — your privacy", onNext)
            OnboardingDots(total = 3, current = 0)
        }
    }
}

@Composable
fun HiwStep(number: String, title: String, desc: String, extra: @Composable () -> Unit) {
    Column(
        Modifier.fillMaxWidth()
            .background(Color(0xFF111111), RoundedCornerShape(14.dp))
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                Modifier.size(28.dp)
                    .background(Color(0xFF1E2E2B), RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(number, color = Teal, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(desc, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
        extra()
    }
}

@Composable
fun SvIcon(icon: String, label: String, teal: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            Modifier.size(34.dp)
                .background(if (teal) TealDim else Color(0xFF1C1C1E), RoundedCornerShape(10.dp))
                .then(if (teal) Modifier.border(1.dp, Teal.copy(0.15f), RoundedCornerShape(10.dp)) else Modifier),
            contentAlignment = Alignment.Center
        ) { Text(icon, fontSize = 16.sp) }
        Text(label, color = Color(0xFF48484A), fontSize = 9.sp,
            lineHeight = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun VerdictBox(label: String, sub: String, color: Color, modifier: Modifier) {
    Column(
        modifier
            .background(color.copy(0.08f), RoundedCornerShape(9.dp))
            .border(1.dp, color.copy(0.25f), RoundedCornerShape(9.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(3.dp))
        Text(sub, color = color.copy(0.7f), fontSize = 10.sp)
    }
}