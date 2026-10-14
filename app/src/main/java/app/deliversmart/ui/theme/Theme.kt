package app.deliversmart.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Teal       = Color(0xFF0D9488)
val TealDim    = Color(0xFF0D1412)
val Background = Color(0xFF08090A)
val Surface    = Color(0xFF111111)
val Surface2   = Color(0xFF131314)
val Border     = Color(0xFF1A1A1A)
val Border2    = Color(0xFF1C1C1E)
val TextPrimary   = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF636366)
val TextMuted     = Color(0xFF3A3A3C)
val Red    = Color(0xFFEF4444)
val Amber  = Color(0xFFF59E0B)
val Indigo = Color(0xFF818CF8)

private val ColorScheme = darkColorScheme(
    primary   = Teal,
    background = Background,
    surface   = Surface,
    onPrimary  = Color.White,
    onBackground = TextPrimary,
    onSurface  = TextPrimary,
)

@Composable
fun DeliverSmartTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, content = content)
}
