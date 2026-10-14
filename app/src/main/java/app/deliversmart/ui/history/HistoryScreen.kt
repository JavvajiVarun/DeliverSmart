package app.deliversmart.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import app.deliversmart.HistoryOffer
import app.deliversmart.Prefs
import app.deliversmart.ui.AppTab
import app.deliversmart.ui.BottomNav
import app.deliversmart.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(onHome: () -> Unit, onSettings: () -> Unit) {
    val context = LocalContext.current
    var history by remember { mutableStateOf(Prefs.getHistory(context)) }

    LaunchedEffect(Unit) { history = Prefs.getHistory(context) }

    val grouped = remember(history) {
        history.groupBy { offer ->
            val cal = Calendar.getInstance().apply { timeInMillis = offer.timestamp }
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
            when {
                cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> "TODAY"
                cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) &&
                        cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) -> "YESTERDAY"
                else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(offer.timestamp))
            }
        }
    }

    Scaffold(
        containerColor = Background,
        bottomBar = { BottomNav(AppTab.HISTORY, onHome = onHome, onHistory = {}, onSettings = onSettings) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
        ) {
            /* Header */
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text("Deliver", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
                    Text("Smart", color = Teal, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
                }
                if (history.isNotEmpty()) {
                    Box(
                        Modifier.background(Surface, RoundedCornerShape(8.dp))
                            .border(1.dp, Border, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("${history.size} scans", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }

            if (history.isEmpty()) {
                /* Empty state */
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(48.dp))
                    Box(
                        Modifier.size(80.dp)
                            .background(TealDim, RoundedCornerShape(24.dp))
                            .border(1.dp, Teal.copy(0.2f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) { Text("📋", fontSize = 36.sp) }
                    Spacer(Modifier.height(18.dp))
                    Text("No history yet", color = TextPrimary, fontSize = 18.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = (-0.4).sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Every offer you screenshot will appear here with its rating and your net earnings.",
                        color = TextSecondary, fontSize = 11.sp,
                        lineHeight = 17.sp, textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Row(
                        Modifier.fillMaxWidth()
                            .background(Surface, RoundedCornerShape(12.dp))
                            .border(1.dp, Border, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("💡", fontSize = 18.sp)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Go to Home and take your first screenshot to see your offer history here.",
                            color = Color(0xFF555555), fontSize = 11.sp, lineHeight = 16.sp
                        )
                    }
                }
            } else {
                grouped.forEach { (dateLabel, offers) ->
                    Text(
                        "$dateLabel · ${offers.size} SCANS",
                        color = Color(0xFF3A3A3C), fontSize = 9.sp, letterSpacing = 3.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                    )
                    offers.forEach { offer ->
                        PremiumHistoryCard(offer)
                        Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun PremiumHistoryCard(offer: HistoryOffer) {
    val ratingColor = when (offer.rating) { "GOOD" -> Teal; "BAD" -> Red; else -> Amber }
    val cardBg = when (offer.rating) {
        "GOOD" -> Brush.linearGradient(listOf(Color(0xFF0D1412), Color(0xFF091812)))
        "BAD"  -> Brush.linearGradient(listOf(Color(0xFF1A0808), Color(0xFF120505)))
        else   -> Brush.linearGradient(listOf(Color(0xFF1A1200), Color(0xFF120E00)))
    }
    val borderColor = when (offer.rating) {
        "GOOD" -> Teal.copy(0.2f); "BAD" -> Red.copy(0.2f); else -> Amber.copy(0.2f)
    }
    val badgeText = when (offer.rating) { "GOOD" -> "✓ Good"; "BAD" -> "✕ Bad"; else -> "~ Average" }
    val timeStr = SimpleDateFormat("h:mma", Locale.getDefault()).format(Date(offer.timestamp))

    Column(
        Modifier.padding(horizontal = 14.dp).fillMaxWidth()
            .background(cardBg, RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .padding(13.dp)
    ) {
        /* Top row */
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("₹${offer.payout}", color = Color.White,
                fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.8).sp)
            Text(badgeText, color = ratingColor, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(ratingColor.copy(0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp))
        }

        Spacer(Modifier.height(10.dp))

        /* Chips */
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            HistChip("₹${"%.0f".format(offer.netEarnings)} net", ratingColor)
            HistChip("${"%.1f".format(offer.distanceKm)} km", Color(0xFF636366))
            HistChip("${offer.estTimeMinutes} min", Color(0xFF636366))
            HistChip("₹${"%.0f".format(offer.ratePerKm)}/km", ratingColor)
        }

        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = Color.White.copy(0.04f), thickness = 1.dp)
        Spacer(Modifier.height(8.dp))

        Text("🕐 $timeStr · Scanned from screenshot",
            color = Color(0xFF2A2A2A), fontSize = 9.sp)
    }
}

@Composable
fun HistChip(text: String, color: Color) {
    Text(text, color = color, fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .background(color.copy(0.1f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp))
}