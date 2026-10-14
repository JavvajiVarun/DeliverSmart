package app.deliversmart

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val NAME = "deliversmart_prefs"
    private const val KEY_FUEL = "fuel_cost"
    private const val KEY_THRESHOLD = "threshold"
    private const val KEY_VEHICLE = "vehicle_type"
    private const val KEY_HISTORY = "history"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun getFuelCost(context: Context): Double =
        prefs(context).getFloat(KEY_FUEL, 2.5f).toDouble()

    fun setFuelCost(context: Context, value: Double) =
        prefs(context).edit().putFloat(KEY_FUEL, value.toFloat()).apply()

    fun getThreshold(context: Context): Double =
        prefs(context).getFloat(KEY_THRESHOLD, 12.0f).toDouble()

    fun setThreshold(context: Context, value: Double) =
        prefs(context).edit().putFloat(KEY_THRESHOLD, value.toFloat()).apply()

    fun getVehicleType(context: Context): String =
        prefs(context).getString(KEY_VEHICLE, "Bike") ?: "Bike"

    fun setVehicleType(context: Context, value: String) =
        prefs(context).edit().putString(KEY_VEHICLE, value).apply()

    fun getHistory(context: Context): List<HistoryOffer> {
        val json = prefs(context).getString(KEY_HISTORY, "[]") ?: "[]"
        return try {
            val list = mutableListOf<HistoryOffer>()
            val arr = org.json.JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(HistoryOffer(
                    payout = obj.getInt("payout"),
                    netEarnings = obj.getDouble("netEarnings"),
                    distanceKm = obj.getDouble("distanceKm"),
                    ratePerKm = obj.getDouble("ratePerKm"),
                    estTimeMinutes = obj.getInt("estTimeMinutes"),
                    rating = obj.getString("rating"),
                    timestamp = obj.getLong("timestamp")
                ))
            }
            list
        } catch (e: Exception) { emptyList() }
    }

    fun addToHistory(context: Context, offer: HistoryOffer) {
        val current = getHistory(context).toMutableList()
        current.add(0, offer)
        /* Keep last 100 offers */
        val trimmed = current.take(100)
        val arr = org.json.JSONArray()
        trimmed.forEach { h ->
            val obj = org.json.JSONObject()
            obj.put("payout", h.payout)
            obj.put("netEarnings", h.netEarnings)
            obj.put("distanceKm", h.distanceKm)
            obj.put("ratePerKm", h.ratePerKm)
            obj.put("estTimeMinutes", h.estTimeMinutes)
            obj.put("rating", h.rating)
            obj.put("timestamp", h.timestamp)
            arr.put(obj)
        }
        prefs(context).edit().putString(KEY_HISTORY, arr.toString()).apply()
    }

    fun clearHistory(context: Context) =
        prefs(context).edit().remove(KEY_HISTORY).apply()
}

data class HistoryOffer(
    val payout: Int,
    val netEarnings: Double,
    val distanceKm: Double,
    val ratePerKm: Double,
    val estTimeMinutes: Int,
    val rating: String,
    val timestamp: Long
)