package app.deliversmart.service

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

data class OfferData(
    val payout: Int,
    val distanceKm: Double,
    val ratePerKm: Double,
    val netEarnings: Double,
    val rating: OfferRating
)

enum class OfferRating { GOOD, AVERAGE, BAD }

object OfferExtractor {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun extractOffer(
        bitmap: Bitmap,
        fuelCostPerKm: Double = 2.5,
        goodThreshold: Double = 12.0,
        onResult: (OfferData?) -> Unit
    ) {
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val fullText = visionText.text
                android.util.Log.d("OfferExtractor", "OCR Text: $fullText")

                val payout = extractPayout(fullText)
                val distance = extractDistance(fullText)

                if (payout != null && distance != null && distance > 0) {
                    val fuelCost = distance * fuelCostPerKm
                    val net = payout - fuelCost
                    val rate = net / distance

                    val rating = when {
                        rate >= goodThreshold -> OfferRating.GOOD
                        rate >= goodThreshold * 0.6 -> OfferRating.AVERAGE
                        else -> OfferRating.BAD
                    }

                    onResult(
                        OfferData(
                            payout = payout,
                            distanceKm = distance,
                            ratePerKm = rate,
                            netEarnings = net,
                            rating = rating
                        )
                    )
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                android.util.Log.e("OfferExtractor", "OCR failed: ${it.message}")
                onResult(null)
            }
    }

    private fun extractPayout(text: String): Int? {
        /* Matches patterns like ₹280, Rs 280, 280.00 */
        val patterns = listOf(
            Regex("[₹Rs\\.]+\\s*(\\d+)(?:\\.\\d+)?"),
            Regex("(\\d+)(?:\\.\\d+)?\\s*(?:rupees|rs)", RegexOption.IGNORE_CASE),
            Regex("earning[s]?[:\\s]+(\\d+)", RegexOption.IGNORE_CASE),
            Regex("payout[:\\s]+(\\d+)", RegexOption.IGNORE_CASE),
            Regex("fare[:\\s]+(\\d+)", RegexOption.IGNORE_CASE)
        )
        val amounts = mutableListOf<Int>()
        patterns.forEach { pattern ->
            pattern.findAll(text).forEach { match ->
                match.groupValues[1].toIntOrNull()?.let {
                    if (it in 50..2000) amounts.add(it)
                }
            }
        }
        /* Return the largest amount found — most likely the payout */
        return amounts.maxOrNull()
    }

    private fun extractDistance(text: String): Double? {
        /* Matches patterns like 6.2 km, 6.2km, 6 km */
        val patterns = listOf(
            Regex("(\\d+\\.?\\d*)\\s*km", RegexOption.IGNORE_CASE),
            Regex("(\\d+\\.?\\d*)\\s*kilometers", RegexOption.IGNORE_CASE),
            Regex("distance[:\\s]+(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE)
        )
        patterns.forEach { pattern ->
            pattern.find(text)?.groupValues?.get(1)?.toDoubleOrNull()?.let {
                if (it in 0.5..50.0) return it
            }
        }
        return null
    }
}