package com.example.mybin.tampilan

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mybin.viewmodel.SetoranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeScreen(navController: NavController, viewModel: SetoranViewModel) {
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }
    var inputPoin by remember { mutableStateOf("") }


    var showResultDialog by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        viewModel.loadUserBalance()
    }


    val totalPoinTersedia = viewModel.totalPoinUser
    val amountToExchange = inputPoin.toIntOrNull() ?: 0
    val minimalTukar = 1000


    val isEligible = phoneNumber.length >= 10 &&
            amountToExchange >= minimalTukar &&
            amountToExchange <= totalPoinTersedia

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

                        viewModel.submitExchange(
                            amountPoin = amountToExchange,
                            phoneNumber = phoneNumber,
                            onSuccess = { message, newBalance ->

                                resultMessage = message
                                isSuccess = true
                                showResultDialog = true
                            },
                            onError = { error ->
                                resultMessage = error
                                isSuccess = false
                                showResultDialog = true
                            }
                        )
                    }
                },
                enabled = isEligible && !viewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2EBD70),
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Konfirmasi Penukaran", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF0)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Poin Anda:", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, contentDescription = "Points", tint = Color(0xFFFFC107), modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = String.format("%,d Poin", totalPoinTersedia),
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Text("Nomor HP Akun GoPay", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("Contoh: 08123456789") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2EBD70),
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))


            Text("Jumlah Poin yang Ditukar", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            OutlinedTextField(
                value = inputPoin,
                onValueChange = { inputPoin = it },
                placeholder = { Text("Minimal 1.000") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                suffix = { Text("Poin") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2EBD70),
                    unfocusedContainerColor = Color(0xFFF5F5F5)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(24.dp))


            Text("Rincian Penukaran (1 Poin = Rp 1)", fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimasi Rupiah:", color = Color.Gray)
                        Text(
                            text = "Rp ${String.format("%,d", amountToExchange)}",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2EBD70),
                            fontSize = 18.sp
                        )
                    }
                }
            }


            if (totalPoinTersedia < minimalTukar) {
                Text(
                    text = "Poin Anda belum cukup (Min. 1.000)",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else if (amountToExchange > totalPoinTersedia) {
                Text(
                    text = "Jumlah melebihi saldo poin Anda.",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }


    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            icon = {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (isSuccess) Color(0xFF2EBD70) else Color.Red,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = if (isSuccess) "Penukaran Berhasil" else "Penukaran Gagal",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(resultMessage, textAlign = TextAlign.Center)
                    if (isSuccess) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Status penukaran: BERHASIL. Notifikasi sukses telah dikirim ke Admin.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResultDialog = false
                        if (isSuccess) {

                            navController.popBackStack()
                        }
                    }
                ) {
                    Text("Tutup", fontWeight = FontWeight.Bold, color = Color(0xFF2EBD70))
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}