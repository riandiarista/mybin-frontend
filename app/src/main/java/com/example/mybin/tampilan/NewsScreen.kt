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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.R
import com.example.mybin.ui.theme.MyBinTheme

@Composable
fun NewsScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.orang),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp) // Adjusted padding to make space for back button
        ) {
            Header()
            Body()
        }
    }
}

@Composable
private fun Header() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.introawal),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = "Selamat Sore, Puan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Jangan sampai ketinggalan daur ulang sampahmu ya!", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notification",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun Body() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(
                Color(0xFFF5F5F5),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "News",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Trending", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        NewsItem(
            imageRes = R.drawable.introawal,
            title = "Sampah plastik: Reduce dan Reuse dahulu sebelum Recycle",
            source = "Greenpeace",
            date = "22 Juli 2022"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(contentAlignment = Alignment.BottomEnd) {
            NewsItem(
                imageRes = R.drawable.introawal,
                title = "Menjaga Hutan untuk Masa Depan yang Lebih Baik",
                source = "WWF Indonesia",
                date = "15 Juli 2022"
            )
            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.padding(16.dp),
                shape = CircleShape,
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
            }
        }
    }
}

@Composable
private fun NewsItem(imageRes: Int, title: String, source: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            if (title.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = "Source",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(text = source, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Date",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 8.dp)
                            )
                            Text(text = date, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                        }
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsScreenPreview() {
    MyBinTheme {
        NewsScreen(rememberNavController())
    }
}
