package app.deliversmart.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.deliversmart.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var started by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (started) 1f else 0.5f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (started) 1f else 0f,
        animationSpec = tween(520, easing = FastOutSlowInEasing),
        label = "alpha"
    )
    val tagAlpha by animateFloatAsState(
        targetValue = if (started) 0.55f else 0f,
        animationSpec = tween(420, delayMillis = 520, easing = LinearOutSlowInEasing),
        label = "tagAlpha"
    )
    val tagSpacing by animateFloatAsState(
        targetValue = if (started) 4f else 10f,
        animationSpec = tween(420, delayMillis = 520, easing = LinearOutSlowInEasing),
        label = "tagSpacing"
    )

    LaunchedEffect(Unit) {
        delay(300)
        started = true
        delay(2400)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .alpha(alpha)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) { append("Deliver") }
                    withStyle(SpanStyle(color = Teal))        { append("Smart") }
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1.5).sp
            )
            Text(
                text = "KNOW BEFORE YOU ACCEPT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = tagSpacing.sp,
                color = Teal.copy(alpha = tagAlpha)
            )
        }
    }
}
