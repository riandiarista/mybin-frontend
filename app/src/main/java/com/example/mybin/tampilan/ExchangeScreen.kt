package com.example.mybin.tampilan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.ui.theme.MyBinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tukar Poin ke GoPay", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF4CAF50))
            )
        },
        bottomBar = {
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Tukar Poin Sekarang", color = Color.DarkGray, fontSize = 18.sp, modifier = Modifier.padding(8.dp))
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
            // Point Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF0)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Poin Anda Saat Ini:", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SentimentSatisfied, contentDescription = "Points", tint = Color.Gray, modifier = Modifier.size(24.dp))
                        Text("1.500 Poin", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.DarkGray)
                    }
                    Text("Minimal penukaran: 1.000 Poin", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // GoPay Account Section
            Text("Nomor HP Akun GoPay", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = { Text("Contoh: 08123456789") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5)),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Text("Pastikan nomor GoPay Anda sudah terdaftar dan aktif.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

            Spacer(modifier = Modifier.height(24.dp))

            // Exchange Summary
            Text("Jumlah Poin yang Ditukar", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF0)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Poin Ditukar:", color = Color.Gray)
                        Text("0 Poin", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Saldo GoPay Diterima:", color = Color.Gray)
                        Text("Rp 0", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExchangeScreenPreview() {
    MyBinTheme {
        ExchangeScreen(rememberNavController())
    }
}
