package com.example.mybin.tampilan

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen(navController: NavController, viewModel: SetoranViewModel) {
    val context = LocalContext.current
    var showFilterDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    // Memanggil data riwayat (Selesai/Ditolak) dari API saat layar dibuka
    LaunchedEffect(Unit) {
        viewModel.loadLaporanHistory { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
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
                        Icon(Icons.Default.Download, contentDescription = "Unduh")
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
                    TotalPointsCard()
                    Spacer(modifier = Modifier.height(16.dp))
                    TransactionHeader { showFilterDialog = true }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (!viewModel.isLoading && viewModel.laporanList.isEmpty()) {
                    item {
                        Text(
                            text = "Belum ada riwayat laporan (Selesai/Ditolak).",
                            modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                }

                // Menampilkan data asli dari Backend melalui ViewModel
                items(viewModel.laporanList) { setoran ->
                    SetoranItemComponent(setoran)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(onDismiss = { showFilterDialog = false })
    }

    if (showExportDialog) {
        ExportDialog(onDismiss = { showExportDialog = false })
    }
}

@Composable
fun SetoranItemComponent(setoran: SetoranData) {
    // Penanganan status null atau tidak dikenal secara aman
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

                // Tampilan poin yang disesuaikan dengan status
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

// --- KOMPONEN UI PENDUKUNG ---

@Composable
fun TotalPointsCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD7F5E6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Runtah Points Saat Ini:", fontSize = 14.sp, color = Color.Gray)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🪙", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("1,500", fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Text("Poin terupdate otomatis setelah verifikasi.", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun TransactionHeader(onFilterClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Laporan Final", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        TextButton(onClick = onFilterClick) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = Color(0xFF2EBD70))
            Text(" Filter", color = Color(0xFF2EBD70))
        }
    }
}

// --- BOTTOM NAVIGATION COMPONENTS ---

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

// --- DIALOGS ---

@Composable
fun FilterDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Filter Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Fitur filter segera hadir di Galaloc.std", color = Color.Gray)
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth().padding(top = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EBD70))) {
                    Text("Tutup")
                }
            }
        }
    }
}

@Composable
fun ExportDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Export Laporan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Unduh riwayat penyetoran dalam format PDF.", modifier = Modifier.padding(vertical = 8.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EBD70))) {
                    Text("Download")
                }
            }
        }
    }
}