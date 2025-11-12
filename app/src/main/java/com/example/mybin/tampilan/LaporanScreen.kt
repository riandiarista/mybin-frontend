package com.example.mybin.tampilan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.ui.theme.MyBinTheme

data class Setoran(val id: String, val tanggal: String, val jenis: String, val lokasi: String, val poin: Int, val status: String)

val dummyData = listOf(
    Setoran("#20251015", "15 Oktober 2025, 14:30 WIB", "3.5 kg Sampah Campur (Plastik & Kertas)", "Jl. Mahoni No. 5, Padang", 150, "Selesai"),
    Setoran("#20251016", "16 Oktober 2025, 09:00 WIB", "5.0 kg Kertas & Kardus", "Bank Sampah Sentral, Jakarta", 0, "Menunggu"),
    Setoran("#20251010", "10 Oktober 2025, 17:00 WIB", "1.2 kg Botol Kaca", "Kantor Verifikasi, Bogor", 0, "Ditolak"),
    Setoran("#20251009", "09 Oktober 2025, 11:45 WIB", "2.1 kg Sampah Organik", "Jl. Merdeka No. 1, Bandung", 75, "Selesai"),
    Setoran("#20251008", "08 Oktober 2025, 13:00 WIB", "10.0 kg Logam", "Gudang Utama, Surabaya", 350, "Selesai"),
    Setoran("#20251007", "07 Oktober 2025, 10:20 WIB", "4.5 kg Plastik", "Jl. Kenanga No. 8, Medan", 0, "Menunggu"),
    Setoran("#20251005", "05 Oktober 2025, 19:00 WIB", "0.8 kg Baterai Bekas", "Pusat Daur Ulang, Bekasi", 0, "Ditolak"),
    Setoran("#20251004", "04 Oktober 2025, 16:15 WIB", "6.2 kg Sampah Elektronik", "Jl. Gatot Subroto No. 12, Semarang", 0, "Ditolak"),
    Setoran("#20251003", "03 Oktober 2025, 08:30 WIB", "7.0 kg Besi Tua", "Kawasan Industri, Cikarang", 250, "Selesai"),
    Setoran("#20251002", "02 Oktober 2025, 15:50 WIB", "2.5 kg Kaleng Aluminium", "Perumahan Indah, Depok", 0, "Menunggu"),
    Setoran("#20251001", "01 Oktober 2025, 10:00 WIB", "3.0 kg Kertas", "Jl. Sudirman Kav. 5, Jakarta", 120, "Selesai"),
    Setoran("#20250930", "30 September 2025, 12:00 WIB", "1.5 kg Botol Plastik PET", "Taman Kota, Tangerang", 50, "Selesai")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(navController: NavController) {
    var showFilterDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Penyetoran", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showExportDialog = true }) {
                        Icon(Icons.Default.Download, contentDescription = "Unduh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                TotalPointsCard()
                Spacer(modifier = Modifier.height(16.dp))
                TransactionHeader { showFilterDialog = true }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(dummyData) { setoran ->
                SetoranItem(setoran)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog { showFilterDialog = false }
    }

    if (showExportDialog) {
        ExportDialog { showExportDialog = false }
    }
}

@Composable
fun TotalPointsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD7F5E6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Runtah Points Saat Ini:", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🪙", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("1,500", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Poin akan di-update setelah verifikasi selesai.", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun TransactionHeader(onFilterClick: () -> Unit) {
    val greenColor = Color(0xFF2EBD70)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Transaksi Terakhir", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        TextButton(onClick = onFilterClick) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = greenColor)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Filter", color = greenColor)
        }
    }
}

@Composable
fun SetoranItem(setoran: Setoran) {
    val statusColor = when (setoran.status) {
        "Selesai" -> Color(0xFF2EBD70)
        "Menunggu" -> Color(0xFFFFA500)
        else -> Color.Red
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                Spacer(modifier = Modifier.width(8.dp))
                Text(setoran.id, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                if (setoran.poin > 0) {
                    Text("+${setoran.poin} Poin", color = Color(0xFF2EBD70), fontWeight = FontWeight.Bold)
                }
            }
            Text(setoran.tanggal, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(setoran.jenis, fontWeight = FontWeight.Medium)
            Text(setoran.lokasi, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                when (setoran.status) {
                    "Selesai", "Ditolak" -> {
                        Text(
                            text = setoran.status,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                    "Menunggu" -> {
                        Text(text = setoran.status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                TextButton(onClick = { /*TODO*/ }) {
                    Text(if (setoran.status == "Ditolak") "Alasan Penolakan" else if (setoran.status == "Menunggu") "" else "Lihat Detail")
                }
            }
        }
    }
}

@Composable
fun FilterDialog(onDismiss: () -> Unit) {
    val statusOptions = listOf("Selesai", "Diproses", "Ditolak")
    var selectedStatus by remember { mutableStateOf("Selesai") }
    val jenisSampahOptions = listOf("Semua Jenis", "Plastik", "Kertas", "Logam", "Kaca", "Organik")
    var selectedJenisSampah by remember { mutableStateOf(jenisSampahOptions.first()) }
    var isJenisSampahDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Filter Riwayat (Jenis & Tanggal)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Rentang Tanggal", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("00/00/0000") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = "", onValueChange = {}, placeholder = { Text("00/00/0000") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Status Transaksi", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    statusOptions.forEach { status ->
                        val isSelected = selectedStatus == status
                        val (containerColor, borderColor, contentColor) = when {
                            isSelected && status == "Selesai" -> Triple(Color(0xFFD7F5E6), Color(0xFF2EBD70), Color(0xFF2EBD70))
                            isSelected && status == "Diproses" -> Triple(Color(0xFFFFFBE6), Color(0xFFF0AD4E), Color(0xFFF0AD4E))
                            isSelected && status == "Ditolak" -> Triple(Color(0xFFF8D7DA), Color(0xFFD9534F), Color(0xFFD9534F))
                            else -> Triple(Color.White, Color.LightGray, Color.Gray)
                        }
                        Button(onClick = { selectedStatus = status }, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor), border = BorderStroke(1.dp, borderColor), modifier = Modifier.weight(1f), elevation = ButtonDefaults.buttonElevation(0.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
                            Text(status, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Jenis Sampah", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(value = selectedJenisSampah, onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth(), trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, "") }, shape = RoundedCornerShape(12.dp))
                    DropdownMenu(expanded = isJenisSampahDropdownExpanded, onDismissRequest = { isJenisSampahDropdownExpanded = false }, modifier = Modifier.fillMaxWidth(0.8f)) {
                        jenisSampahOptions.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { selectedJenisSampah = option; isJenisSampahDropdownExpanded = false })
                        }
                    }
                    Box(modifier = Modifier.matchParentSize().clickable { isJenisSampahDropdownExpanded = true })
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { onDismiss() }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EBD70))) {
                    Text("Terapkan Filter", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ExportDialog(onDismiss: () -> Unit) {
    var selectedFormat by remember { mutableStateOf("PDF") }
    val rentangWaktuOptions = listOf("7 Hari Terakhir", "30 Hari Terakhir", "90 Hari Terakhir")
    var selectedRentangWaktu by remember { mutableStateOf(rentangWaktuOptions.first()) }
    var isRentangWaktuDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Export Laporan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Pilih format dan rentang waktu untuk mengunduh riwayat penyetoran Anda ke *File Manager*.", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Pilih Format File", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FormatButton(text = "PDF", icon = Icons.Filled.Article, isSelected = selectedFormat == "PDF", onClick = { selectedFormat = "PDF" }, modifier = Modifier.weight(1f))
                    FormatButton(text = "Excel", icon = Icons.Filled.TableChart, isSelected = selectedFormat == "Excel", onClick = { selectedFormat = "Excel" }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Rentang Waktu Laporan", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    OutlinedTextField(
                        value = selectedRentangWaktu,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, "") },
                        shape = RoundedCornerShape(12.dp)
                    )
                    DropdownMenu(
                        expanded = isRentangWaktuDropdownExpanded,
                        onDismissRequest = { isRentangWaktuDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rentangWaktuOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedRentangWaktu = option
                                    isRentangWaktuDropdownExpanded = false
                                }
                            )
                        }
                    }
                    Box(modifier = Modifier.matchParentSize().clickable { isRentangWaktuDropdownExpanded = true })
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EBD70))
                ) {
                    Text("Unduh ke File Manager", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormatButton(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val borderColor = if (isSelected) Color(0xFF2EBD70) else Color.LightGray
    val backgroundColor = if (isSelected) Color(0xFFD7F5E6) else Color.White
    val contentColor = if (isSelected) Color(0xFF2EBD70) else Color.Gray

    Card(
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = text, tint = contentColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, color = contentColor, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LaporanScreenPreview() {
    MyBinTheme {
        LaporanScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun FilterDialogPreview() {
    MyBinTheme {
        FilterDialog { }
    }
}

@Preview(showBackground = true)
@Composable
fun ExportDialogPreview() {
    MyBinTheme {
        ExportDialog { }
    }
}
