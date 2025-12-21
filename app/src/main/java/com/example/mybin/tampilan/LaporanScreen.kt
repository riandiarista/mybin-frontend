package com.example.mybin.tampilan

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.mybin.model.SetoranData
import com.example.mybin.viewmodel.SetoranViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(navController: NavController, viewModel: SetoranViewModel) {
    val context = LocalContext.current
    var showFilterDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    // SINKRONISASI REAL-TIME: Mengambil riwayat DAN saldo bersih terbaru dari tabel User
    LaunchedEffect(Unit) {
        viewModel.loadLaporanHistory { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
        viewModel.loadUserBalance() // Sinkronisasi saldo profil
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat Penyetoran", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("MainPage") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showExportDialog = true }) {
                        Icon(Icons.Default.Download, contentDescription = "Unduh PDF")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = { MyBinBottomNavBar(navController = navController, activeScreen = "Laporan") },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF2EBD70)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    // Tampilkan saldo dari kolom total_poin_user di database
                    TotalPointsCard(points = viewModel.totalPoinUser)
                    Spacer(modifier = Modifier.height(16.dp))

                    TransactionHeader { showFilterDialog = true }

                    if (viewModel.filterStatus != "Semua" || viewModel.filterJenis != "Semua") {
                        Text(
                            text = "Filter aktif: ${viewModel.filterStatus} | ${viewModel.filterJenis}",
                            fontSize = 12.sp,
                            color = Color(0xFF2EBD70),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val listToDisplay = viewModel.filteredLaporanList

                if (!viewModel.isLoading && listToDisplay.isEmpty()) {
                    item {
                        Text(
                            text = "Tidak ada riwayat yang sesuai dengan filter.",
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }

                items(listToDisplay) { setoran ->
                    SetoranItemComponent(setoran)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Beri ruang ekstra di bawah agar tidak tertutup Navbar
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // --- DIALOG FILTER ---
    if (showFilterDialog) {
        FilterDialog(
            currentStatus = viewModel.filterStatus,
            currentJenis = viewModel.filterJenis,
            onFilterApplied = { status, jenis ->
                viewModel.filterStatus = status
                viewModel.filterJenis = jenis
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }

    // --- DIALOG EXPORT ---
    if (showExportDialog) {
        ExportDialog(
            onDismiss = { showExportDialog = false },
            viewModel = viewModel,
            context = context
        )
    }
}

// --- FUNGSI EXPORT PDF (FULL STYLING) ---
fun exportLaporanToPdf(context: Context, laporanList: List<SetoranData>) {
    val pdfDocument = PdfDocument()
    val paint = Paint()
    val titlePaint = Paint()

    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas

    titlePaint.textSize = 18f
    titlePaint.isFakeBoldText = true
    canvas.drawText("LAPORAN PENYETORAN", 40f, 50f, titlePaint)

    paint.textSize = 10f
    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    canvas.drawText("Dicetak pada: $date", 40f, 75f, paint)
    canvas.drawLine(40f, 85f, 555f, 85f, paint)

    var yPos = 110f
    paint.isFakeBoldText = true
    canvas.drawText("ID", 40f, yPos, paint)
    canvas.drawText("Jenis Sampah", 100f, yPos, paint)
    canvas.drawText("Status", 300f, yPos, paint)
    canvas.drawText("Poin", 480f, yPos, paint)

    paint.isFakeBoldText = false
    yPos += 15f
    canvas.drawLine(40f, yPos - 5f, 555f, yPos - 5f, paint)
    yPos += 10f

    laporanList.forEach { item ->
        if (yPos < 800f) {
            canvas.drawText("#${item.id}", 40f, yPos, paint)
            canvas.drawText(item.jenis, 100f, yPos, paint)
            canvas.drawText(item.status.uppercase(), 300f, yPos, paint)
            canvas.drawText("${item.totalKoin}", 480f, yPos, paint)
            yPos += 25f
        }
    }

    pdfDocument.finishPage(page)

    val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    val file = File(directory, "Laporan_MyBin_${System.currentTimeMillis()}.pdf")

    try {
        pdfDocument.writeTo(FileOutputStream(file))
        Toast.makeText(context, "PDF disimpan di folder Download", Toast.LENGTH_LONG).show()
    } catch (e: IOException) {
        Toast.makeText(context, "Gagal simpan PDF: ${e.message}", Toast.LENGTH_SHORT).show()
    } finally {
        pdfDocument.close()
    }
}

@Composable
fun ExportDialog(onDismiss: () -> Unit, viewModel: SetoranViewModel, context: Context) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Export Laporan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    "Unduh ${viewModel.filteredLaporanList.size} data riwayat saat ini ke dalam format PDF.",
                    modifier = Modifier.padding(vertical = 8.dp),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        exportLaporanToPdf(context, viewModel.filteredLaporanList)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EBD70))
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download PDF")
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Batal", color = Color.Gray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentStatus: String,
    currentJenis: String,
    onFilterApplied: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempStatus by remember { mutableStateOf(currentStatus) }
    var tempJenis by remember { mutableStateOf(currentJenis) }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Filter Riwayat", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Status", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Semua", "Selesai", "Ditolak").forEach { status ->
                        FilterChip(
                            selected = tempStatus == status,
                            onClick = { tempStatus = status },
                            label = { Text(status) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD7F5E6))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Jenis Sampah", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Semua", "Organik").forEach { jenis ->
                            FilterChip(
                                selected = tempJenis == jenis,
                                onClick = { tempJenis = jenis },
                                label = { Text(jenis) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD7F5E6))
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Anorganik", "B3").forEach { jenis ->
                            FilterChip(
                                selected = tempJenis == jenis,
                                onClick = { tempJenis = jenis },
                                label = { Text(jenis) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFD7F5E6))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onFilterApplied(tempStatus, tempJenis) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EBD70))
                ) {
                    Text("Terapkan Filter")
                }

                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Batal", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SetoranItemComponent(setoran: SetoranData) {
    val statusRaw = setoran.status.lowercase()
    val isDitolak = statusRaw == "ditolak"
    val statusColor = if (isDitolak) Color.Red else Color(0xFF2EBD70)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                Spacer(modifier = Modifier.width(8.dp))
                Text("#${setoran.id}", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (isDitolak) "0 Poin" else "+${setoran.totalKoin} Poin",
                    color = if (isDitolak) Color.Gray else Color(0xFF2EBD70),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(setoran.tanggal, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(setoran.jenis, fontWeight = FontWeight.Medium)
            Text(setoran.lokasi, color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = setoran.status.uppercase(),
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                TextButton(onClick = { /* Detail Action */ }) {
                    Text(
                        text = if (isDitolak) "Alasan Penolakan" else "Lihat Detail",
                        color = if (isDitolak) Color.Red else Color(0xFF2EBD70)
                    )
                }
            }
        }
    }
}

@Composable
fun TotalPointsCard(points: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD7F5E6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Bin Points:", fontSize = 14.sp, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Stars, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format("%,d Poin", points),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2EBD70)
                )
            }
            Text("Poin ini adalah saldo bersih yang sinkron dengan database.", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun TransactionHeader(onFilterClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Laporan Riwayat", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        TextButton(onClick = onFilterClick) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = Color(0xFF2EBD70))
            Text(" Filter", color = Color(0xFF2EBD70))
        }
    }
}

@Composable
fun MyBinBottomNavBar(navController: NavController, activeScreen: String) {
    BottomAppBar(containerColor = Color.White) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Home",
                isSelected = activeScreen == "Home",
                onClick = { navController.navigate("MainPage") }
            )
            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.List,
                label = "Laporan",
                isSelected = activeScreen == "Laporan",
                onClick = { navController.navigate("LaporanScreen") }
            )
            BottomNavItem(
                icon = Icons.Default.Notifications,
                label = "Notifikasi",
                isSelected = activeScreen == "Notifikasi",
                onClick = { navController.navigate("notifikasi_screen") }
            )
            BottomNavItem(
                icon = Icons.Default.Person,
                label = "Account",
                isSelected = activeScreen == "Account",
                onClick = { navController.navigate("profile_screen") }
            )
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val color = if (isSelected) Color(0xFF2EBD70) else Color.Gray
    Column(
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = color)
        Text(label, fontSize = 10.sp, color = color)
    }
}