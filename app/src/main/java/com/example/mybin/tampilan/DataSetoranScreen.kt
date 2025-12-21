package com.example.mybin.tampilan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.model.SetoranData
import com.example.mybin.ui.theme.MyBinTheme
import com.example.mybin.viewmodel.SetoranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSetoranScreen(navController: NavController, setoranViewModel: SetoranViewModel = viewModel()) {

    // --- LANGKAH 1: Trigger pengambilan data dari backend saat layar dibuka ---
    LaunchedEffect(Unit) {
        setoranViewModel.getSetoran()
    }

    // Mengambil list data yang sudah di-mapping oleh ViewModel dari API
    val setoranList = setoranViewModel.setoranList

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Setoran", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Tombol Refresh manual untuk cek update status dari Admin
                    IconButton(onClick = { setoranViewModel.getSetoran() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { /* Fitur Unduh */ }) {
                        Icon(Icons.Default.Download, contentDescription = "Unduh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
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
                TambahDataButton(navController)
                Spacer(modifier = Modifier.height(16.dp))
                DataStatusHeader()
                Spacer(modifier = Modifier.height(8.dp))

                // Jika list kosong setelah fetch, tampilkan keterangan
                if (setoranList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada sampah yang disetor", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            // --- LANGKAH 2: Render UI menggunakan data asli dari database ---
            items(setoranList) { setoran ->
                SetoranItem(setoran = setoran)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TambahDataButton(navController: NavController) {
    val gradient = Brush.horizontalGradient(listOf(Color(0xFF86F3B8), Color(0xFF53E690)))
    Button(
        onClick = { navController.navigate("pilih_setoran_screen") },
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Tambah Data +", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun DataStatusHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Data & Status", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        OutlinedButton(
            onClick = { /* Filter */ },
            border = BorderStroke(1.dp, Color.LightGray),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.Gray, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Filter", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun SetoranItem(setoran: SetoranData) {
    // Sinkronisasi warna dengan status backend: 'menunggu' (Orange) atau 'selesai' (Hijau)
    val isPending = setoran.status.contains("menunggu", ignoreCase = true) || setoran.status.contains("Diproses", ignoreCase = true)

    val barColor = if (isPending) Color(0xFFF0AD4E) else Color(0xFF2EBD70)
    val chipColor = if (isPending) Color(0xFFFFFBE6) else Color(0xFFD7F5E6)
    val chipContentColor = if (isPending) Color(0xFFF0AD4E) else Color(0xFF2EBD70)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(barColor, shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)))
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Setoran ${setoran.id}", fontWeight = FontWeight.Bold)
                    Text("Edit", color = Color(0xFF2EBD70), fontWeight = FontWeight.Bold)
                }
                Text(setoran.tanggal, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Redeem, contentDescription = "Jenis Sampah", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(setoran.jenis, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Lokasi", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(setoran.lokasi, fontSize = 14.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MonetizationOn, contentDescription = "Koin", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${setoran.totalKoin} Poin", fontSize = 14.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = chipColor)) {
                            // Status diambil langsung dari kolom 'status' di database backend
                            Text(setoran.status.uppercase(), color = chipContentColor, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hapus", color = Color.Red, fontSize = 12.sp)
                    }
                    if (isPending) {
                        Text("Status +", color = Color.Blue, fontSize = 12.sp)
                    } else {
                        Text("Lihat Detail", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}