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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.R
import com.example.mybin.ui.theme.MyBinTheme

data class SampahData(
    val id: Int,
    val jenis: String,
    val nama: String,
    val berat: String,
    val imageRes: Int,
    val jenisColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampahkuScreen(navController: NavController) {
    val sampleData = listOf(
        SampahData(1, "An Organik", "Botol Plastik", "1.5 Kg", R.drawable.introawal, Color(0xFF4CAF50)),
        SampahData(2, "Organik", "Rumput", "5.0 Kg", R.drawable.introawal, Color.Green),
        SampahData(3, "Sampah B3", "Baterai", "1.50 Kg", R.drawable.introawal, Color.Red),
        SampahData(4, "An Organik", "Botol Plastik", "1.5 Kg", R.drawable.introawal, Color(0xFF4CAF50)),
        SampahData(5, "An Organik", "Botol Plastik", "1.5 Kg", R.drawable.introawal, Color(0xFF4CAF50))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SampahKu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4CAF50),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    navController.navigate("MainPage") {
                        popUpTo("MainPage") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Kembali ke Home", color = Color.White, fontSize = 18.sp)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(sampleData, key = { it.id }) { item ->
                when (item.id) {
                    2 -> SampahItemWithDelete(item)
                    5 -> SampahItemWithEdit(item)
                    else -> SampahItemCard(item)
                }
            }
        }
    }
}

@Composable
fun SampahItemCard(item: SampahData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.nama,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.jenis, color = item.jenisColor, fontSize = 12.sp)
                Text(text = item.nama, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = item.berat, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SampahItemWithDelete(item: SampahData) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.align(Alignment.CenterEnd)) {
            Spacer(modifier = Modifier.width(100.dp))
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(88.dp) // Match card height
                    .background(Color.Red, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { /* TODO: Delete action */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                }
            }
        }
        SampahItemCard(item, modifier = Modifier.padding(end = 24.dp))
    }
}

@Composable
fun SampahItemWithEdit(item: SampahData) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(60.dp)
                .height(88.dp)
                .background(Color(0xFF4CAF50), shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { /* TODO: Edit action */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        SampahItemCard(item)
    }
}


@Preview(showBackground = true)
@Composable
fun SampahkuScreenPreview() {
    MyBinTheme {
        SampahkuScreen(rememberNavController())
    }
}
