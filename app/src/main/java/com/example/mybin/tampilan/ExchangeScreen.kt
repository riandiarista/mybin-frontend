package com.example.mybin.tampilan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign // FIX: Import untuk TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mybin.viewmodel.SetoranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeScreen(navController: NavController, viewModel: SetoranViewModel) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }

    // Sinkronisasi poin saat layar dibuka agar data selalu terbaru
    LaunchedEffect(Unit) {
        viewModel.loadLaporanHistory { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    val poinTersedia = viewModel.totalPoinUser
    val minimalTukar = 1000
    val isEligible = poinTersedia >= minimalTukar && phoneNumber.length >= 10

    // Perhitungan konversi (Contoh: 1 Poin = Rp 1)
    val estimasiSaldo = if (poinTersedia >= minimalTukar) poinTersedia else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tukar Poin ke GoPay", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2EBD70))
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (isEligible) {
                        Toast.makeText(context, "Permintaan penukaran Rp $estimasiSaldo sedang diproses!", Toast.LENGTH_LONG).show()
                    }
                },
                enabled = isEligible,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isEligible) Color(0xFF2EBD70) else Color.LightGray,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Tukar Poin Sekarang",
                    color = if (isEligible) Color.White else Color.DarkGray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Card Saldo Poin Real-time dari ViewModel
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF0)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Poin Tersedia (MyBin Points):", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, contentDescription = "Points", tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format("%,d Poin", poinTersedia),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Color.DarkGray
                        )
                    }
                    Text(
                        text = "Minimal penukaran: $minimalTukar Poin",
                        fontSize = 12.sp,
                        color = if (poinTersedia < minimalTukar) Color.Red else Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Input Nomor HP
            Text("Nomor HP Akun GoPay", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { if (it.length <= 15) phoneNumber = it },
                placeholder = { Text("Contoh: 08123456789") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                // FIX: Menggunakan API colors terbaru untuk OutlinedTextField
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2EBD70),
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Text("Pastikan nomor GoPay Anda sudah terdaftar dan aktif.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Summary Penukaran
            Text("Rincian Penukaran", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Poin yang akan ditukar:", color = Color.Gray)
                        Text("${if (poinTersedia >= minimalTukar) String.format("%,d", poinTersedia) else 0} Poin", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Saldo GoPay Diterima:", color = Color.Gray)
                        Text(
                            text = "Rp ${String.format("%,d", estimasiSaldo)}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2EBD70),
                            fontSize = 18.sp
                        )
                    }
                }
            }

            if (poinTersedia < minimalTukar) {
                Text(
                    text = "Poin Anda belum cukup untuk melakukan penukaran.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}