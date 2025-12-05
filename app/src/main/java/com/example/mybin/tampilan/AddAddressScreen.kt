package com.example.mybin.tampilan

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.ui.theme.MyBinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(navController: NavController) {
    var hariTanggal by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Address",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // This spacer is used to balance the title in the center,
                    // as the navigation icon takes up space.
                    Spacer(modifier = Modifier.width(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color(0xFFF8F8F8)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    InfoRow(icon = Icons.Default.PersonOutline, label = "Hari & Tanggal", value = hariTanggal, onValueChange = { hariTanggal = it })
                    Divider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    InfoRow(icon = Icons.Default.Phone, label = "Phone number", value = phoneNumber, onValueChange = { phoneNumber = it })
                    Divider(color = Color.LightGray.copy(alpha = 0.4f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    InfoRow(icon = Icons.Default.LocationOn, label = "Address", value = address, onValueChange = { address = it })
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            RingkasanSampahDropdown()
            Spacer(modifier = Modifier.height(16.dp))
            SampahItem(icon = Icons.Default.Eco, category = "An Organik", name = "Botol Plastik", weight = "1.5 Kg", backgroundColor = Color(0xFFE6F8F0), iconColor = Color(0xFF2EBD70))
            Spacer(modifier = Modifier.height(12.dp))
            SampahItem(icon = Icons.Default.Spa, category = "Organik", name = "Rumput", weight = "5.0 Kg", backgroundColor = Color(0xFFFFF0F5), iconColor = Color.Magenta.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(24.dp))
            EstimasiPointsCard()
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3F5B3))
            ) {
                Text("Selesai", color = Color(0xFF1F4B1F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
             Spacer(modifier = Modifier.height(16.dp))
        }
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
            focusedIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = true
    )
}

@Composable
private fun RingkasanSampahDropdown() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Language, contentDescription = "Ringkasan Sampah", tint = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Ringkasan Sampah", color = Color.Gray, fontSize = 16.sp)
            }
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.Gray)
        }
    }
}

@Composable
private fun SampahItem(icon: ImageVector, category: String, name: String, weight: String, backgroundColor: Color, iconColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    modifier = Modifier.size(32.dp),
                    tint = iconColor
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(category, fontSize = 12.sp, color = Color(0xFF2EBD70))
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(weight, fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun EstimasiPointsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFC8E6C9), Color(0xFFA5D6A7).copy(alpha = 0.8f))
                    )
                )
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Estimasi Points:", color = Color(0xFF1B5E20))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Points",
                        tint = Color(0xFFF9A825),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "1,500",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1B5E20)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddAddressScreenPreview() {
    MyBinTheme {
        AddAddressScreen(rememberNavController())
    }
}
