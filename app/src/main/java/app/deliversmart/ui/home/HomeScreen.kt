package app.deliversmart.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import app.deliversmart.Prefs
import app.deliversmart.ui.AppTab
import app.deliversmart.ui.BottomNav
import app.deliversmart.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class Rating { GOOD, BAD, AVERAGE }

@Composable
fun HomeScreen(onHistory: () -> Unit, onSettings: () -> Unit) {
    val context = LocalContext.current
    val today = SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date())
    var history by remember { mutableStateOf(Prefs.getHistory(context)) }
    LaunchedEffect(Unit) { history = Prefs.getHistory(context) }

    val goodCount = history.count { it.rating == "GOOD" }
    val avgCount  = history.count { it.rating == "AVERAGE" }
    val badCount  = history.count { it.rating == "BAD" }

    Scaffold(
        containerColor = Background,
        bottomBar = { BottomNav(AppTab.HOME, onHome = {}, onHistory = onHistory, onSettings = onSettings) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
        ) {
            /* Header */
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row {
                        Text("Deliver", color = Color.White, fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
                        Text("Smart", color = Teal, fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
                    }
                    Text(today, color = TextMuted, fontSize = 11.sp)
                }
            }

            /* Hint card */
            Row(
                Modifier.padding(horizontal = 14.dp).fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF0D1412), Color(0xFF091812))),
                        RoundedCornerShape(14.dp)
                    )
                    .border(1.dp, Teal.copy(0.25f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(36.dp).background(Teal.copy(0.15f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("📷", fontSize = 18.sp) }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Screenshot any offer", color = Teal,
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("We read it instantly and show your rating.",
                        color = Color(0xFF3A4A44), fontSize = 10.sp, lineHeight = 15.sp)
                }
            }

            Spacer(Modifier.height(12.dp))

            /* Stats card */
            Row(
                Modifier.padding(horizontal = 14.dp).fillMaxWidth()
                    .background(Surface, RoundedCornerShape(14.dp))
                    .border(1.dp, Border, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("${history.size}", color = Color.White, fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold, letterSpacing = (-1.5).sp)
                    Text("OFFERS ANALYSED", color = Color(0xFF555555),
                        fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Medium)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VerticalDivider()
                    MiniStat("$goodCount", "Good", Teal)
                    VerticalDivider()
                    MiniStat("$avgCount", "Avg", Amber)
                    VerticalDivider()
                    MiniStat("$badCount", "Bad", Red)
                }
            }

            Spacer(Modifier.height(12.dp))

            /* How to get started */
            Column(
                Modifier.padding(horizontal = 14.dp).fillMaxWidth()
                    .background(Surface, RoundedCornerShape(14.dp))
                    .border(1.dp, Border, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Text(
                    if (history.isEmpty()) "HOW TO GET STARTED" else "QUICK REMINDER",
                    color = Teal, fontSize = 9.sp,
                    letterSpacing = 2.sp, fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(13.dp))
                StartStep("1", "Open Zomato or Swiggy",
                    "Wait for a delivery offer to appear on your screen.")
                Spacer(Modifier.height(10.dp))
                StartStep("2", "Take a screenshot",
                    "Press Volume Down and Power button together.")
                Spacer(Modifier.height(10.dp))
                StartStep("3", "See your rating instantly",
                    "A popup shows Good, Average or Bad with your net earnings.")
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun VerticalDivider() {
    Box(Modifier.width(1.dp).height(32.dp).background(Color(0xFF1A1A1A)))
}

@Composable
fun MiniStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
        Text(label, color = Color(0xFF555555), fontSize = 9.sp)
    }
}

@Composable
fun StartStep(number: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            Modifier.size(22.dp)
                .background(TealDim, RoundedCornerShape(7.dp))
                .border(1.dp, Teal.copy(0.2f), RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center
        ) { Text(number, color = Teal, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = Color(0xFFDDDDDD),
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(desc, color = Color(0xFF555555), fontSize = 10.sp, lineHeight = 15.sp)
        }
    }
}