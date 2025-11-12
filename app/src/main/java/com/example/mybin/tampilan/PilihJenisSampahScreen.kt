package com.example.mybin.tampilan

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.R
import com.example.mybin.ui.theme.MyBinTheme

@Composable
fun PilihJenisSampahScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.orang),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.1f
        )
        Column {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pilih Jenis\nSampah",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        JenisSampahCard(
                            title = "Organik",
                            price = "1000/kg",
                            imagePainterId = R.drawable.introawal, // Placeholder
                            color = Color(0xFF4CAF50),
                            onClick = { navController.navigate("DetailSampahScreen/Organik/1000 per kg") }
                        )
                    }
                    item {
                        JenisSampahCard(
                            title = "Anorganik",
                            price = "2000/kg",
                            imagePainterId = R.drawable.introawal, // Placeholder
                            color = Color.Black,
                            onClick = { navController.navigate("DetailSampahScreen/Anorganik/2000 per kg") }
                        )
                    }
                    item {
                        JenisSampahCard(
                            title = "B3",
                            price = "1500/kg",
                            imagePainterId = R.drawable.introawal, // Placeholder
                            color = Color.Red,
                            onClick = { navController.navigate("DetailSampahScreen/B3/1500 per kg") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JenisSampahCard(title: String, price: String, imagePainterId: Int, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Price",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(text = price, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            }
            Image(
                painter = painterResource(id = imagePainterId),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PilihJenisSampahScreenPreview() {
    MyBinTheme {
        PilihJenisSampahScreen(rememberNavController())
    }
}
