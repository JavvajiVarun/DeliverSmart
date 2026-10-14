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
    val rating: OfferRating,
    val estTimeMinutes: Int  // estimated time including 5 min wait
)

enum class OfferRating { GOOD, AVERAGE, BAD }

object OfferExtractor {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /* Average delivery speed in km/h */
    private const val AVG_SPEED_KMH = 20.0
    private const val RESTAURANT_WAIT_MINS = 5

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
                android.util.Log.d("OfferExtractor", "=== OCR TEXT START ===")
                android.util.Log.d("OfferExtractor", fullText)
                android.util.Log.d("OfferExtractor", "=== OCR TEXT END ===")

                val payout = extractPayout(fullText)
                val distance = extractDistance(fullText)

                android.util.Log.d("OfferExtractor", "Extracted payout=$payout distance=$distance")

                if (payout != null && distance != null && distance > 0) {
                    val fuelCost = distance * fuelCostPerKm
                    val net = payout - fuelCost
                    val rate = net / distance

                    /* Estimated time = travel time + 5 min restaurant wait */
                    val travelMins = (distance / AVG_SPEED_KMH * 60).toInt()
                    val estTime = travelMins + RESTAURANT_WAIT_MINS

                    val rating = when {
                        rate >= goodThreshold -> OfferRating.GOOD
                        rate >= goodThreshold * 0.6 -> OfferRating.AVERAGE
                        else -> OfferRating.BAD
                    }

                    onResult(OfferData(
                        payout = payout,
                        distanceKm = distance,
                        ratePerKm = rate,
                        netEarnings = net,
                        rating = rating,
                        estTimeMinutes = estTime
                    ))
                } else {
                    android.util.Log.d("OfferExtractor", "Could not extract payout or distance")
                    onResult(null)
                }
            }
            .addOnFailureListener {
                android.util.Log.e("OfferExtractor", "OCR failed: ${it.message}")
                onResult(null)
            }
    }

    private fun extractPayout(text: String): Int? {
        val lines = text.lines()

        if (text.contains("Net earn", ignoreCase = true) &&
            text.contains("Tap anywhere to dismiss", ignoreCase = true)) {
            android.util.Log.d("OfferExtractor", "Ignoring own overlay screenshot")
            return null
        }

        for (i in lines.indices) {
            val line = lines[i].trim().lowercase()
            if (line.contains("earning") || line.contains("estimated") ||
                line.contains("payout") || line.contains("fare")) {

                for (j in i..minOf(i + 3, lines.size - 1)) {
                    val raw = lines[j].trim()
                    val numMatch = Regex("(\\d+)").find(raw)
                    val num = numMatch?.groupValues?.get(1)?.toIntOrNull()

                    if (num != null) {
                        val corrected = when {
                            num in 720..799 && (num - 700) in 20..99 -> num - 700
                            num in 700..719 -> num - 700
                            num in 20..2000 -> num
                            else -> null
                        }
                        if (corrected != null && corrected in 20..2000) {
                            android.util.Log.d("OfferExtractor", "Corrected payout: $corrected (from $num)")
                            return corrected
                        }
                    }
                }
            }
        }

        val amounts = mutableListOf<Int>()
        Regex("(\\d+)").findAll(text).forEach { match ->
            match.groupValues[1].toIntOrNull()?.let { num ->
                val corrected = when {
                    num in 720..799 -> num - 700
                    num in 700..719 -> num - 700
                    num in 20..500 -> num
                    else -> null
                }
                corrected?.let { amounts.add(it) }
            }
        }
        return amounts.minOrNull()
    }

    private fun extractDistance(text: String): Double? {
        val lines = text.lines()

        for (line in lines) {
            if (line.contains("pickup", ignoreCase = true) &&
                line.contains("drop", ignoreCase = true)) {
                val kmPattern = Regex("(\\d+\\.?\\d*)\\s*km", RegexOption.IGNORE_CASE)
                val matches = kmPattern.findAll(line).toList()
                if (matches.size >= 2) {
                    val total = matches.sumOf { it.groupValues[1].toDoubleOrNull() ?: 0.0 }
                    if (total in 0.3..50.0) return total
                }
                if (matches.size == 1) {
                    matches[0].groupValues[1].toDoubleOrNull()?.let {
                        if (it in 0.3..50.0) return it
                    }
                }
            }
        }

        val distances = mutableListOf<Double>()
        val kmPatterns = listOf(
            Regex("(\\d+\\.?\\d*)\\s*kms?", RegexOption.IGNORE_CASE),
            Regex("(\\d+\\.?\\d*)\\s*kilometers?", RegexOption.IGNORE_CASE),
            Regex("distance[:\\s]+(\\d+\\.?\\d*)", RegexOption.IGNORE_CASE)
        )
        kmPatterns.forEach { pattern ->
            pattern.findAll(text).forEach { match ->
                match.groupValues[1].toDoubleOrNull()?.let {
                    if (it in 0.3..50.0) distances.add(it)
                }
            }
        }

        if (distances.size >= 2) {
            val total = distances.take(2).sum()
            if (total in 0.3..50.0) return total
        }
        return distances.minOrNull()
    }
}