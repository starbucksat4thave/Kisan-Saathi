package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BarkatGold
import com.example.ui.theme.FieldGreen
import com.example.ui.theme.NotoNastaliqUrdu
import com.example.ui.theme.WheatGold
import com.example.util.LocalAppLanguage
import com.example.util.isUrdu

@Composable
fun BarkatBasisDialog(
    maundEstimate: Double,
    lossFactorBasis: String,
    fieldAreaAcre: Double = 4.0,
    expectedYieldPerAcre: Double = 32.0,
    onDismiss: () -> Unit
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    tint = WheatGold,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "برکت کا تخمینہ (Barkat Basis)" else "Barkat Basis (Loss Prevention)",
                    fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                    fontSize = if (isUrdu) 20.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF8E1),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = BarkatGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "تمام اعداد و شمار تحقیقی ماڈل کا اندازہ ہیں (placeholder)۔"
                                else "All calculations are agronomic estimates from NASA models (placeholder).",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF795548)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isUrdu) "حساب کا فارمولا (Estimation Formula):" else "Estimation Formula:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isUrdu) "بچائی گئی برکت = رقبہ (ایکڑ) × متوقع پیداوار × نقصان سے بچاؤ کا تناسب"
                        else "Saved Barkat = Area (Acres) × Expected Yield × Loss Prevention Factor",
                    fontSize = 13.sp,
                    color = FieldGreen,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                // Breakdown list
                BreakdownRow(
                    if (isUrdu) "رقبہ (Field Area)" else "Field Area",
                    if (isUrdu) "$fieldAreaAcre ایکڑ (Bara Khet)" else "$fieldAreaAcre Acres (Bara Khet)"
                )
                BreakdownRow(
                    if (isUrdu) "متوقع اوسط پیداوار" else "Expected Avg Yield",
                    if (isUrdu) "$expectedYieldPerAcre من فی ایکڑ (بارانی علاقہ)" else "$expectedYieldPerAcre maunds/acre (Rainfed)"
                )
                BreakdownRow(
                    if (isUrdu) "تخمینہ برکت (Saved)" else "Estimated Barkat (Saved)",
                    if (isUrdu) "$maundEstimate من (۱ من = ۴۰ کلوگرام)" else "$maundEstimate maunds (1 maund = 40 kg)"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isUrdu) "تفصیل و بنیاد (Agronomic Rationale):" else "Agronomic Rationale:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$lossFactorBasis (placeholder)",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FieldGreen),
                modifier = Modifier.testTag("close_barkat_basis_btn")
            ) {
                Text(if (isUrdu) "سمجھ گیا (Understood)" else "Understood")
            }
        }
    )
}

@Composable
private fun BreakdownRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
