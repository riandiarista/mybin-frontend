package com.example.mybin.tampilan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybin.model.SampahData
import com.example.mybin.viewmodel.SampahViewModel
import com.example.mybin.viewmodel.SetoranViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    navController: NavController,
    sampahViewModel: SampahViewModel = viewModel(),
    setoranViewModel: SetoranViewModel = viewModel(),
    sampahIds: String?,
    totalKoin: Int?
) {
    var hariTanggal by remember { mutableStateOf("") }
    var jam by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val selectedSampahList = remember { mutableStateListOf<SampahData>() }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var isSubmitting by remember { mutableStateOf(false) }

    // Dialog Pemilih Tanggal
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> hariTanggal = "$dayOfMonth/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    // Dialog Pemilih Jam
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute -> jam = String.format("%02d:%02d", hour, minute) },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    // Load data sampah untuk ringkasan dropdown
    LaunchedEffect(sampahIds) {
        selectedSampahList.clear()
        sampahIds?.split(",")?.forEach { id ->
            sampahViewModel.getSampahById(id)?.let { selectedSampahList.add(it) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Konfirmasi Setoran", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Card Input Informasi
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    PickerInfoRow(icon = Icons.Default.DateRange, label = "Hari & Tanggal", value = hariTanggal, onClick = { datePickerDialog.show() })
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.4f))
                    PickerInfoRow(icon = Icons.Default.AccessTime, label = "Jam Penjemputan", value = jam, onClick = { timePickerDialog.show() })
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.4f))
                    InfoRow(icon = Icons.Default.Phone, label = "Nomor Telepon", value = phoneNumber, onValueChange = { phoneNumber = it })
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.4f))
                    InfoRow(icon = Icons.Default.LocationOn, label = "Alamat Lengkap", value = address, onValueChange = { address = it })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fitur Dropdown Ringkasan Sampah
            RingkasanSampahDropdown(selectedSampahList)

            Spacer(modifier = Modifier.height(24.dp))

            // Card Estimasi Poin
            totalKoin?.let { EstimasiPointsCard(it) }

            Spacer(modifier = Modifier.height(24.dp))

            // Tombol Submit: Bagian krusial yang memicu Notifikasi ke Superbin
            Button(
                onClick = {
                    if (hariTanggal.isNotEmpty() && jam.isNotEmpty() && address.isNotEmpty() && !sampahIds.isNullOrEmpty()) {
                        isSubmitting = true
                        // Memanggil submitSetoran di ViewModel
                        setoranViewModel.submitSetoran(
                            sampahIds = sampahIds,
                            totalKoin = totalKoin ?: 0,
                            lokasi = address,
                            onSuccess = { message ->
                                isSubmitting = false
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                // Navigasi ke Riwayat/Data Setoran
                                navController.navigate("DataSetoranScreen") { popUpTo("MainPage") { inclusive = false } }
                            },
                            onError = { error ->
                                isSubmitting = false
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isSubmitting,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Selesai & Setorkan", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PickerInfoRow(icon: ImageVector, label: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = value.ifEmpty { label }, color = if (value.isEmpty()) Color.Gray else Color.Black)
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(label, color = Color.Gray) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Gray) },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
private fun RingkasanSampahDropdown(selectedSampahList: List<SampahData>) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.List, contentDescription = null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Ringkasan Sampah (${selectedSampahList.size})", color = Color.Gray)
                }
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
            }
            if (expanded) {
                selectedSampahList.forEach { sampah ->
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text("• ${sampah.jenisSampah}: ${sampah.detailSampah} (${sampah.totalBobot})", fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun EstimasiPointsCard(totalKoin: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Total Estimasi Poin", color = Color(0xFF2E7D32))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color(0xFFF9A825), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = totalKoin.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            }
        }
    }
}