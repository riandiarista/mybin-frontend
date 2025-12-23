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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybin.model.SetoranData
import com.example.mybin.viewmodel.SetoranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifikasiSampahScreen(
    navController: NavController,
    setoranViewModel: SetoranViewModel = viewModel()
) {
    // Refresh data setiap kali layar dibuka
    LaunchedEffect(Unit) {
        setoranViewModel.fetchSetoran()
    }

    // State untuk SearchBar
    var searchQuery by remember { mutableStateOf("") }

    // Mengamati list setoran dari ViewModel
    val setoranList = setoranViewModel.setoranList

    // FILTER LOGIC: Menggunakan derivedStateOf agar filter dihitung ulang hanya saat list atau query berubah
    val filteredList by remember(setoranList, searchQuery) {
        derivedStateOf {
            setoranList.filter { setoran ->
                // Hanya tampilkan yang berstatus menunggu atau diproses
                val matchesStatus = setoran.status.equals("menunggu", ignoreCase = true) ||
                        setoran.status.equals("Diproses", ignoreCase = true)

                val matchesSearch = setoran.jenis.contains(searchQuery, ignoreCase = true) ||
                        setoran.id.toString().contains(searchQuery) ||
                        setoran.namaUser.contains(searchQuery, ignoreCase = true)

                matchesStatus && matchesSearch
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verifikasi Admin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // --- SEARCH BAR ---
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari jenis, ID, atau nama user...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF2EBD70),
                    unfocusedBorderColor = Color.LightGray
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tampilkan Loading Indicator jika sedang mengambil data
            if (setoranViewModel.isLoading && setoranList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2EBD70))
                }
            } else if (filteredList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isEmpty()) "Tidak ada setoran yang perlu diverifikasi"
                        else "Data tidak ditemukan",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredList, key = { it.id }) { setoran ->
                        VerifikasiSetoranItem(
                            setoran = setoran,
                            onApprove = {
                                // Memanggil fungsi updateStatus di ViewModel
                                setoranViewModel.updateStatus(setoran.id, "selesai")
                            },
                            onReject = {
                                setoranViewModel.updateStatus(setoran.id, "ditolak")
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VerifikasiSetoranItem(
    setoran: SetoranData,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isWaiting = setoran.status.equals("menunggu", ignoreCase = true)
    val barColor = if (isWaiting) Color(0xFFF0AD4E) else Color(0xFF2EBD70)
    val chipColor = if (isWaiting) Color(0xFFFFFBE6) else Color(0xFFD7F5E6)
    val chipContentColor = if (isWaiting) Color(0xFFF0AD4E) else Color(0xFF2EBD70)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(barColor))
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Text("Setoran #${setoran.id}", fontWeight = FontWeight.Bold)
                Text(setoran.tanggal, fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.RestoreFromTrash, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(setoran.jenis, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, "User", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(setoran.namaUser, fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MonetizationOn, null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${setoran.totalKoin} Poin", fontSize = 14.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(shape = RoundedCornerShape(8.dp), color = chipColor) {
                        Text(
                            text = setoran.status,
                            color = chipContentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp
                        )
                    }

                    Row {
                        OutlinedButton(
                            onClick = onReject,
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            border = BorderStroke(1.dp, Color.Red)
                        ) {
                            Text("Tolak", color = Color.Red, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onApprove,
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2EBD70))
                        ) {
                            Text("Verifikasi", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}