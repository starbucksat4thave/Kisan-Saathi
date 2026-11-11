package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun PartnerDashboardDialog(
    onDismiss: () -> Unit
) {
    val isUrdu = LocalAppLanguage.current.isUrdu
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = FieldGreen,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "حکومتی و ادارہ جاتی ڈیش بورڈ (Partner Dashboard)" else "Extension & Partner Dashboard",
                    fontFamily = if (isUrdu) NotoNastaliqUrdu else null,
                    fontSize = if (isUrdu) 19.sp else 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "پنجاب ایگریکلچر ایکسٹینشن اور این جی او شراکت داروں کے لیے ضلع چکوال کا عمومی خلاصہ (Anonymized Data)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 4 Metric KPI Cards
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KpiCard("فعال کسان (Farmers)", "1,248", FieldGreen, Modifier.weight(1f))
                    KpiCard("الرٹس جاری (Alerts)", "4,820", Color(0xFF0288D1), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KpiCard("اقدامات تصدیق (Actions)", "3,150", WheatGold, Modifier.weight(1f))
                    KpiCard("برکت اناج بچاؤ (Maunds)", "4,725 من*", Color(0xFF2E7D32), Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Regional Risk Hotspots
                Text(
                    text = "علاقائی خطرات اور ہاٹ اسپاٹس (Risk Hotspots):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                HotspotItem("تحصیل تلہ گنگ (Talagang)", "خشک سالی و وتر کی کمی (SMAP: 14% soil moisture)", Color(0xFFE65100))
                HotspotItem("تحصیل کلر کہار (Kallar Kahar)", "پیلے کنگی (Yellow Rust) کے ابتدائی کیسز", Color(0xFFD32F2F))
                HotspotItem("ڈھڈیال و چکوال شہر", "بروقت بوائی مہم 92% کسانوں کی جانب سے مکمل", FieldGreen)

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "* برکت کی کل گنتی ریسرچ ماڈل کے مطابق تخمینہ (DRAFT) ہے۔",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = FieldGreen)
            ) {
                Text(if (isUrdu) "بند کریں (Close)" else "Close")
            }
        }
    )
}

@Composable
private fun KpiCard(title: String, value: String, accentColor: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
private fun HotspotItem(location: String, note: String, color: Color) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = location, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(text = note, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
