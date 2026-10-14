package app.deliversmart.service

import android.app.*
import android.content.*
import android.database.ContentObserver
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import app.deliversmart.HistoryOffer
import app.deliversmart.Prefs

class ScreenshotService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var overlayManager: OverlayManager? = null
    private var lastProcessedId = 0L
    private var isPolling = false
    private var contentObserver: ContentObserver? = null

    companion object {
        const val CHANNEL_ID = "deliversmart_channel"
        const val NOTIF_ID = 1
        var fuelCostPerKm: Double = 2.5
        var goodThreshold: Double = 12.0
    }

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("ScreenshotService", "=== SERVICE CREATED ===")
        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification())
        overlayManager = OverlayManager(this)

        /* Load settings from Prefs */
        fuelCostPerKm = Prefs.getFuelCost(this)
        goodThreshold = Prefs.getThreshold(this)
        android.util.Log.d("ScreenshotService", "Settings: fuel=$fuelCostPerKm threshold=$goodThreshold")

        lastProcessedId = getLatestImageId()
        android.util.Log.d("ScreenshotService", "lastId=$lastProcessedId")

        registerObserver()
        startPolling()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        android.util.Log.d("ScreenshotService", "onStartCommand")
        /* Reload settings every time service starts */
        fuelCostPerKm = Prefs.getFuelCost(this)
        goodThreshold = Prefs.getThreshold(this)
        intent?.let {
            if (it.hasExtra("fuelCost")) fuelCostPerKm = it.getDoubleExtra("fuelCost", 2.5)
            if (it.hasExtra("threshold")) goodThreshold = it.getDoubleExtra("threshold", 12.0)
        }
        if (!isPolling) startPolling()
        return START_STICKY
    }

    private fun registerObserver() {
        contentObserver = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                android.util.Log.d("ScreenshotService", "ContentObserver triggered: $uri")
                uri?.let {
                    handler.postDelayed({ checkSpecificUri(it) }, 500)
                }
            }
        }
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver!!
        )
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.INTERNAL_CONTENT_URI, true, contentObserver!!
        )
    }

    private fun startPolling() {
        isPolling = true
        android.util.Log.d("ScreenshotService", "Polling started")
        pollForScreenshot()
    }

    private fun pollForScreenshot() {
        if (!isPolling) return
        checkForNewScreenshot()
        handler.postDelayed({ pollForScreenshot() }, 500)
    }

    private fun checkSpecificUri(uri: Uri) {
        try {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA
            )
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)) ?: ""
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)) ?: ""
                    if (id > lastProcessedId && isScreenshotPath(name, path)) {
                        lastProcessedId = id
                        handler.postDelayed({ processScreenshot(uri) }, 500)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ScreenshotService", "checkSpecificUri error: ${e.message}")
        }
    }

    private fun checkForNewScreenshot() {
        try {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED
            )
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, sortOrder
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)) ?: ""
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)) ?: ""

                    if (id > lastProcessedId) {
                        android.util.Log.d("ScreenshotService", "Poll found new: name=$name")
                        val isScreenshot = isScreenshotPath(name, path)
                        lastProcessedId = id
                        if (isScreenshot) {
                            val uri = Uri.withAppendedPath(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString()
                            )
                            handler.postDelayed({ processScreenshot(uri) }, 500)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ScreenshotService", "Poll error: ${e.message}")
        }
    }

    private fun isScreenshotPath(name: String, path: String): Boolean {
        return name.contains("screenshot", ignoreCase = true) ||
                path.contains("screenshot", ignoreCase = true) ||
                path.contains("Screenshots", ignoreCase = true) ||
                path.contains("DCIM", ignoreCase = true)
    }

    private fun getLatestImageId(): Long {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, sortOrder
        )?.use {
            if (it.moveToFirst()) {
                return it.getLong(it.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
            }
        }
        return 0L
    }

    private fun processScreenshot(uri: Uri) {
        try {
            android.util.Log.d("ScreenshotService", "Processing: $uri")
            val bitmap = contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            } ?: return

            /* Reload latest settings before processing */
            fuelCostPerKm = Prefs.getFuelCost(this)
            goodThreshold = Prefs.getThreshold(this)

            OfferExtractor.extractOffer(
                bitmap = bitmap,
                fuelCostPerKm = fuelCostPerKm,
                goodThreshold = goodThreshold
            ) { offerData ->
                offerData?.let { offer ->
                    /* Save to history */
                    val historyEntry = HistoryOffer(
                        payout = offer.payout,
                        netEarnings = offer.netEarnings,
                        distanceKm = offer.distanceKm,
                        ratePerKm = offer.ratePerKm,
                        estTimeMinutes = offer.estTimeMinutes,
                        rating = offer.rating.name,
                        timestamp = System.currentTimeMillis()
                    )
                    Prefs.addToHistory(this, historyEntry)
                    android.util.Log.d("ScreenshotService", "Saved to history")

                    handler.post {
                        overlayManager?.showOverlay(offer)
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ScreenshotService", "Process error: ${e.message}")
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID, "DeliverSmart", NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Watching for offer screenshots" }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("DeliverSmart is active")
            .setContentText("Watching for offer screenshots")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        android.util.Log.d("ScreenshotService", "=== DESTROYED ===")
        isPolling = false
        handler.removeCallbacksAndMessages(null)
        contentObserver?.let { contentResolver.unregisterContentObserver(it) }
        overlayManager?.hideOverlay()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}