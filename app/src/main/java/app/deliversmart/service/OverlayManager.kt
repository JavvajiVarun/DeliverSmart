package app.deliversmart.service

import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue

class OverlayManager(private val context: Context) {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())

    init {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private fun dp(value: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, value.toFloat(),
            context.resources.displayMetrics
        ).toInt()

    fun showOverlay(offer: OfferData) {
        hideOverlay()

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = dp(8)
        }

        val ratingColor = when (offer.rating) {
            OfferRating.GOOD    -> Color.parseColor("#0D9488")
            OfferRating.AVERAGE -> Color.parseColor("#F59E0B")
            OfferRating.BAD     -> Color.parseColor("#EF4444")
        }
        val ratingBg = when (offer.rating) {
            OfferRating.GOOD    -> Color.parseColor("#0D1412")
            OfferRating.AVERAGE -> Color.parseColor("#1A1200")
            OfferRating.BAD     -> Color.parseColor("#1A0808")
        }
        val ratingLabel = when (offer.rating) {
            OfferRating.GOOD    -> "✓  Good offer"
            OfferRating.AVERAGE -> "~  Average offer"
            OfferRating.BAD     -> "✕  Bad offer"
        }

        /* Outer container */
        val outer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(8), dp(12), dp(0))
        }

        /* Card */
        val card = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = GradientDrawable().apply {
                setColor(ratingBg)
                cornerRadius = dp(16).toFloat()
                setStroke(dp(1), ratingColor)
            }
            elevation = dp(8).toFloat()
        }

        /* Top row: rating label + dismiss */
        val topRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val ratingText = TextView(context).apply {
            text = ratingLabel
            textSize = 17f
            setTypeface(null, Typeface.BOLD)
            setTextColor(ratingColor)
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
        }

        val dismissBtn = TextView(context).apply {
            text = "✕"
            textSize = 16f
            setTextColor(Color.parseColor("#3A3A3C"))
            setPadding(dp(8), 0, 0, 0)
        }

        topRow.addView(ratingText)
        topRow.addView(dismissBtn)

        /* Divider */
        val divider = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1)
            ).apply { setMargins(0, dp(10), 0, dp(10)) }
            setBackgroundColor(Color.parseColor("#1E1E1E"))
        }

        /* Stats row — Payout | Net earn | Est time | Rate */
        val statsRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        fun statBox(value: String, label: String, color: Int): LinearLayout {
            return LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
                addView(TextView(context).apply {
                    text = value
                    textSize = 17f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(color)
                    gravity = Gravity.CENTER
                })
                addView(TextView(context).apply {
                    text = label
                    textSize = 10f
                    setTextColor(Color.parseColor("#636366"))
                    gravity = Gravity.CENTER
                    setPadding(0, dp(2), 0, 0)
                })
            }
        }

        fun separator(): View = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(dp(1), dp(36)).apply {
                setMargins(dp(2), 0, dp(2), 0)
            }
            setBackgroundColor(Color.parseColor("#1E1E1E"))
        }

        /* Est time label */
        val estTimeLabel = "${offer.estTimeMinutes} min"

        statsRow.addView(statBox("₹${offer.payout}", "Payout", Color.parseColor("#DDDDDD")))
        statsRow.addView(separator())
        statsRow.addView(statBox("₹${"%.0f".format(offer.netEarnings)}", "Net earn", ratingColor))
        statsRow.addView(separator())
        statsRow.addView(statBox(estTimeLabel, "Est. time", Color.parseColor("#DDDDDD")))
        statsRow.addView(separator())
        statsRow.addView(statBox("₹${"%.0f".format(offer.ratePerKm)}/km", "Rate", ratingColor))

        /* Hint */
        val tapHint = TextView(context).apply {
            text = "Tap anywhere to dismiss  ·  Auto closes in 10s"
            textSize = 10f
            setTextColor(Color.parseColor("#3A3A3C"))
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, 0)
        }

        card.addView(topRow)
        card.addView(divider)
        card.addView(statsRow)
        card.addView(tapHint)
        outer.addView(card)

        card.setOnClickListener { hideOverlay() }
        dismissBtn.setOnClickListener { hideOverlay() }

        overlayView = outer

        try {
            windowManager?.addView(outer, params)
            handler.postDelayed({ hideOverlay() }, 10000)
        } catch (e: Exception) {
            android.util.Log.e("OverlayManager", "Failed to show overlay: ${e.message}")
        }
    }

    fun hideOverlay() {
        handler.removeCallbacksAndMessages(null)
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                android.util.Log.e("OverlayManager", "Failed to hide: ${e.message}")
            }
            overlayView = null
        }
    }
}