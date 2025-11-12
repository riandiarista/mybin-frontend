package com.example.mybin.tampilan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.R
import com.example.mybin.ui.theme.MyBinTheme

@Composable
fun DetailSampahScreen(navController: NavController, jenisSampah: String, harga: String) {
    var detailSampah by remember { mutableStateOf("") }
    var totalBobot by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.orang),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Top Bar with Back Button and Title
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF4CAF50))
                }
                Text(
                    text = "Sampah $jenisSampah",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .background(
                    Color(0xFFE8F5E9), // Light green background
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = "Price",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text(text = harga, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Form Section
            Column(horizontalAlignment = Alignment.Start) {
                Text("Detail Sampah", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = detailSampah,
                    onValueChange = { detailSampah = it },
                    placeholder = { Text("isilahh.......") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Total Bobot (kg)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                OutlinedTextField(
                    value = totalBobot,
                    onValueChange = { totalBobot = it },
                    placeholder = { Text("isilahh.......") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Upload Foto Sampah", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Bisa berupa foto atau dokumen pendukung", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Default.Folder, contentDescription = "Pilih Foto", tint = Color.Black)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Pilih Foto", color = Color.Black)
                    }
                    Button(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Kamera", tint = Color.Black)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Kamera", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Alamat Rumah (GPS)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.GpsFixed, contentDescription = "Maps", tint = Color.White)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Ambil Lokasi di Maps", color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Tambahkan", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailSampahScreenPreview() {
    MyBinTheme {
        DetailSampahScreen(rememberNavController(), "Organik", "1000/kg")
    }
}
