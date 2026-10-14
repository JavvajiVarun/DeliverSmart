package app.deliversmart.ui.settings

import android.content.Intent
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.*
import app.deliversmart.Prefs
import app.deliversmart.service.ScreenshotService
import app.deliversmart.ui.AppTab
import app.deliversmart.ui.BottomNav
import app.deliversmart.ui.theme.*

@Composable
fun SettingsScreen(onHome: () -> Unit, onHistory: () -> Unit) {
    val context = LocalContext.current

    var vehicleType by remember { mutableStateOf(Prefs.getVehicleType(context)) }
    var fuelCost    by remember { mutableStateOf(Prefs.getFuelCost(context).toString()) }
    var threshold   by remember { mutableStateOf(Prefs.getThreshold(context).toString()) }

    var showVehicleDialog   by remember { mutableStateOf(false) }
    var showFuelDialog      by remember { mutableStateOf(false) }
    var showThresholdDialog by remember { mutableStateOf(false) }
    var showClearDialog     by remember { mutableStateOf(false) }

    fun saveAndRestart() {
        Prefs.setFuelCost(context, fuelCost.toDoubleOrNull() ?: 2.5)
        Prefs.setThreshold(context, threshold.toDoubleOrNull() ?: 12.0)
        Prefs.setVehicleType(context, vehicleType)
        ScreenshotService.fuelCostPerKm = fuelCost.toDoubleOrNull() ?: 2.5
        ScreenshotService.goodThreshold = threshold.toDoubleOrNull() ?: 12.0
        val intent = Intent(context, ScreenshotService::class.java).apply {
            putExtra("fuelCost", fuelCost.toDoubleOrNull() ?: 2.5)
            putExtra("threshold", threshold.toDoubleOrNull() ?: 12.0)
        }
        context.startForegroundService(intent)
    }

    if (showVehicleDialog) {
        val options = listOf("Bike", "Scooter", "Bicycle", "Car")
        AlertDialog(
            onDismissRequest = { showVehicleDialog = false },
            containerColor = Color(0xFF111111),
            shape = RoundedCornerShape(16.dp),
            title = { Text("Vehicle type", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    options.forEach { option ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = vehicleType == option,
                                onClick = {
                                    vehicleType = option
                                    Prefs.setVehicleType(context, option)
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Teal,
                                    unselectedColor = Color(0xFF555555)
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(option, color = Color(0xFFDDDDDD), fontSize = 14.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVehicleDialog = false }) {
                    Text("Done", color = Teal, fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }

    if (showFuelDialog) {
        var input by remember { mutableStateOf(fuelCost) }
        AlertDialog(
            onDismissRequest = { showFuelDialog = false },
            containerColor = Color(0xFF111111),
            shape = RoundedCornerShape(16.dp),
            title = { Text("Fuel cost per km", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your fuel cost in ₹ per km",
                        color = Color(0xFF555555), fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        prefix = { Text("₹", color = Teal) },
                        suffix = { Text("/km", color = Color(0xFF555555)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal,
                            unfocusedBorderColor = Color(0xFF2C2C2E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Teal
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    fuelCost = input
                    saveAndRestart()
                    showFuelDialog = false
                }) {
                    Text("Save", color = Teal, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFuelDialog = false }) {
                    Text("Cancel", color = Color(0xFF555555))
                }
            }
        )
    }

    if (showThresholdDialog) {
        var input by remember { mutableStateOf(threshold) }
        AlertDialog(
            onDismissRequest = { showThresholdDialog = false },
            containerColor = Color(0xFF111111),
            shape = RoundedCornerShape(16.dp),
            title = { Text("Good offer threshold", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Offers above this rate will be rated Good",
                        color = Color(0xFF555555), fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        prefix = { Text("₹", color = Teal) },
                        suffix = { Text("/km", color = Color(0xFF555555)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal,
                            unfocusedBorderColor = Color(0xFF2C2C2E),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Teal
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    threshold = input
                    saveAndRestart()
                    showThresholdDialog = false
                }) {
                    Text("Save", color = Teal, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showThresholdDialog = false }) {
                    Text("Cancel", color = Color(0xFF555555))
                }
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = Color(0xFF111111),
            shape = RoundedCornerShape(16.dp),
            title = { Text("Clear all history?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This will permanently delete all your scanned offers. This cannot be undone.",
                    color = Color(0xFF555555), fontSize = 13.sp, lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    Prefs.clearHistory(context)
                    showClearDialog = false
                }) {
                    Text("Clear", color = Red, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = Color(0xFF555555))
                }
            }
        )
    }

    Scaffold(
        containerColor = Background,
        bottomBar = { BottomNav(AppTab.SETTINGS, onHome = onHome, onHistory = onHistory, onSettings = {}) }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding)
        ) {
            /* Header */
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
                Text("Deliver", color = Color.White, fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
                Text("Smart", color = Teal, fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
            }

            SLabel("YOUR PREFERENCES")
            SGroup {
                SRow("🏍", Teal.copy(0.1f), "Vehicle type",
                    "Affects fuel cost calculation", vehicleType) { showVehicleDialog = true }
                SDivider()
                SRow("⛽", Amber.copy(0.1f), "Fuel cost per km",
                    "Used to calculate net earnings", "₹$fuelCost/km") { showFuelDialog = true }
                SDivider()
                SRow("🎯", Color(0xFF818CF8).copy(0.1f), "Good offer threshold",
                    "Minimum ₹/km to rate Good", "₹$threshold/km") { showThresholdDialog = true }
            }

            Spacer(Modifier.height(14.dp))

            SLabel("DATA")
            SGroup {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(30.dp).background(Red.copy(0.1f), RoundedCornerShape(9.dp)),
                            contentAlignment = Alignment.Center
                        ) { Text("🗑", fontSize = 15.sp) }
                        Spacer(Modifier.width(10.dp))
                        Text("Clear all history", color = Red,
                            fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    TextButton(onClick = { showClearDialog = true }) {
                        Text("›", color = Color(0xFF3A3A3C), fontSize = 18.sp)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            /* Privacy note */
            Row(
                Modifier.padding(horizontal = 14.dp).fillMaxWidth()
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF0D1412), Color(0xFF091812))),
                        RoundedCornerShape(14.dp)
                    )
                    .border(1.dp, Teal.copy(0.2f), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text("🔒", fontSize = 16.sp)
                Spacer(Modifier.width(10.dp))
                Text(
                    "Your screenshots never leave your phone. Everything runs on-device. No uploads, no account, no tracking.",
                    color = Color(0xFF3A4A44), fontSize = 11.sp, lineHeight = 16.sp
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable fun SLabel(text: String) {
    Text(text, color = Color(0xFF3A3A3C), fontSize = 9.sp, letterSpacing = 3.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 14.dp, bottom = 7.dp))
}

@Composable fun SGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.padding(horizontal = 14.dp).fillMaxWidth()
            .background(Surface, RoundedCornerShape(14.dp))
            .border(1.dp, Border, RoundedCornerShape(14.dp)),
        content = content
    )
}

@Composable fun SDivider() = HorizontalDivider(color = Color(0xFF111111), thickness = 1.dp)

@Composable fun SRow(
    icon: String, iconBg: Color, label: String,
    sub: String, value: String, onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(Modifier.size(30.dp).background(iconBg, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center) {
                Text(icon, fontSize = 15.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, color = Color(0xFFDDDDDD),
                    fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(2.dp))
                Text(sub, color = Color(0xFF555555), fontSize = 10.sp)
            }
        }
        TextButton(onClick = onClick, contentPadding = PaddingValues(0.dp)) {
            Text(value, color = Teal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(4.dp))
            Text("›", color = Color(0xFF3A3A3C), fontSize = 18.sp)
        }
    }
}