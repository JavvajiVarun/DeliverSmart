package app.deliversmart

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import app.deliversmart.service.ScreenshotService
import app.deliversmart.ui.DeliverSmartApp
import app.deliversmart.ui.theme.DeliverSmartTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("MainActivity", "=== APP STARTED ===")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        startService()
        setContent {
            DeliverSmartTheme {
                DeliverSmartApp(
                    onStartService = { fuelCost, threshold ->
                        ScreenshotService.fuelCostPerKm = fuelCost
                        ScreenshotService.goodThreshold = threshold
                        startService()
                    }
                )
            }
        }
    }

    private fun startService() {
        android.util.Log.d("MainActivity", "Starting service...")
        try {
            val intent = Intent(this, ScreenshotService::class.java).apply {
                putExtra("fuelCost", ScreenshotService.fuelCostPerKm)
                putExtra("threshold", ScreenshotService.goodThreshold)
            }
            startForegroundService(intent)
            android.util.Log.d("MainActivity", "Service started")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Failed: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        startService()
    }
}