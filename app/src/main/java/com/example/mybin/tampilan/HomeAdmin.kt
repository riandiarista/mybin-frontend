package com.example.mybin.tampilan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

// Color Palette
private val PrimaryGreen = Color(0xFF008037)
private val LightGreen = Color(0xFFD7F5D7)
private val ScreenBackgroundColor = Color(0xFFF0F4F3)
private val MenuContainerColor = Color.White
private val WelcomeCardColor = Color.White

@Composable
fun HomeAdmin(navController: NavController) { // NAMA DISESUAIKAN DENGAN MainActivity
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackgroundColor)
    ) {
        // Top Section: Background Image and Welcome Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, start = 24.dp, end = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = WelcomeCardColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_image),
                        contentDescription = "Admin Avatar",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Selamat Datang, Superbin!", // Nama disesuaikan role baru
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                }
            }
        }

        // Menu Section
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .padding(top = 320.dp)
                .fillMaxSize()
                .background(
                    color = MenuContainerColor,
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Menu Admin",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Navigasi ke Verifikasi Sampah
                MenuButton(
                    text = "Verifikasi\nSampah",
                    imageId = R.drawable.plastic_bottle_icon,
                    onClick = { navController.navigate("VerifikasiSampahScreen") }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Navigasi ke Admin News (Berita Terkini)
                MenuButton(
                    text = "Berita\nTerkini",
                    imageId = R.drawable.waving_trash_can_icon,
                    onClick = { navController.navigate("AdminNewsScreen") }
                )
            }
        }
    }
}

@Composable
fun MenuButton(text: String, imageId: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 45.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen,
                textAlign = TextAlign.Start,
                lineHeight = 28.sp
            )
            Image(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun HomeAdminPreview() {
    HomeAdmin(rememberNavController())
}