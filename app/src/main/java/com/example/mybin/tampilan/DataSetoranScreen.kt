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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.ui.theme.MyBinTheme

data class AdminSetoran(val id: String, val tanggal: String, val jenis: String, val lokasi: String, val status: String)

val dummyAdminData = listOf(
    AdminSetoran("#20251016", "16 Oktober 2025, 09:00 WIB", "5.0 kg Kertas & Kardus", "Bank Sampah Sentral, Jakarta", "Diproses"),
    AdminSetoran("#20251015", "15 Oktober 2025, 14:30 WIB", "3.5 kg Sampah Campur (Plastik & Kertas)", "Jl. Mahoni No. 5, Padang", "Selesai"),
    AdminSetoran("#20251017", "15 Oktober 2025, 14:30 WIB", "3.5 kg Sampah Campur (Plastik & Kertas)", "Jl. Mahoni No. 5, Padang", "Selesai"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSetoranScreen(navController: NavController) {
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
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Download, contentDescription = "Unduh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        bottomBar = { MyBinBottomNavBar(navController = navController, selectedRoute = "exchange") },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                TambahDataButton(navController) // Pass NavController
                Spacer(modifier = Modifier.height(16.dp))
                DataStatusHeader()
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(dummyAdminData) { setoran ->
                AdminSetoranItem(setoran = setoran)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TambahDataButton(navController: NavController) { // Accept NavController
    val gradient = Brush.horizontalGradient(listOf(Color(0xFF86F3B8), Color(0xFF53E690)))
    Button(
        onClick = { navController.navigate("AddAddressScreen") }, // Navigate on click
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
            onClick = { /*TODO*/ },
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
fun AdminSetoranItem(setoran: AdminSetoran) {
    val barColor = if (setoran.status == "Diproses") Color(0xFFF0AD4E) else Color(0xFF2EBD70)
    val chipColor = if (setoran.status == "Diproses") Color(0xFFFFFBE6) else Color(0xFFD7F5E6)
    val chipContentColor = if (setoran.status == "Diproses") Color(0xFFF0AD4E) else Color(0xFF2EBD70)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row {
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
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = chipColor)) {
                            Text(setoran.status, color = chipContentColor, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hapus", color = Color.Red, fontSize = 12.sp)
                    }
                    if (setoran.status == "Diproses") {
                        Text("Status +", color = Color.Blue, fontSize = 12.sp)
                    } else {
                        Text("Lihat Detail", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MyBinBottomNavBar(navController: NavController, selectedRoute: String) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(selected = selectedRoute == "home", onClick = { /*navController.navigate("home")*/ }, icon = { Icon(Icons.Filled.Home, contentDescription = "Home") }, label = { Text("Home") })
        NavigationBarItem(selected = selectedRoute == "laporan", onClick = { /*navController.navigate("laporan")*/ }, icon = { Icon(Icons.Filled.Assessment, contentDescription = "Laporan") }, label = { Text("Laporan") })
        NavigationBarItem(selected = selectedRoute == "exchange", onClick = { /* Already here */ }, icon = { Icon(Icons.Filled.Recycling, contentDescription = "Exchange", modifier = Modifier.size(40.dp)) }, label = {})
        NavigationBarItem(selected = selectedRoute == "chat", onClick = { /*navController.navigate("chat")*/ }, icon = { Icon(Icons.Filled.Chat, contentDescription = "Chat") }, label = { Text("Chat") })
        NavigationBarItem(selected = selectedRoute == "akun", onClick = { /*navController.navigate("akun")*/ }, icon = { Icon(Icons.Filled.Person, contentDescription = "Akun") }, label = { Text("Akun") })
    }
}

@Preview(showBackground = true)
@Composable
fun DataSetoranScreenPreview() {
    MyBinTheme {
        DataSetoranScreen(rememberNavController())
    }
}
