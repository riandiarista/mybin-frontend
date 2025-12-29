package com.example.mybin.tampilan

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mybin.R
import com.example.mybin.ui.theme.MyBinTheme
import com.example.mybin.viewmodel.BeritaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NewsScreen(navController: NavController, viewModel: BeritaViewModel) {
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        viewModel.fetchBerita()
        viewModel.loadCurrentUser(context)
    }

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
                .padding(top = 80.dp)
        ) {
            Header()
            Body(navController, viewModel)
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
private fun Body(navController: NavController, viewModel: BeritaViewModel) {
    val beritaList by viewModel.beritaList
    val isLoading by viewModel.isLoading
    val currentUsername by viewModel.currentUsername

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .background(
                    Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "News",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Berita Terbaru", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))


            if (isLoading && beritaList.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFF4CAF50)
                )
            }

            data class DisplayItem(
                val title: String,
                val source: String,
                val date: String,
                val imageRes: Int? = null,
                val imageUri: String? = null,
                val onClick: () -> Unit
            )

            val combinedList = remember(beritaList) {
                val hardcodedItems = listOf(
                    DisplayItem(
                        title = "Sampah plastik: Reduce dan Reuse dahulu sebelum Recycle",
                        source = "Greenpeace",
                        date = "22 Juli 2022",
                        imageRes = R.drawable.introawal,
                        onClick = { navController.navigate("news_detail_screen") }
                    ),
                    DisplayItem(
                        title = "Menjaga Hutan untuk Masa Depan yang Lebih Baik",
                        source = "WWF Indonesia",
                        date = "15 Juli 2022",
                        imageRes = R.drawable.introawal,
                        onClick = { navController.navigate("news_detail_screen") }
                    )
                )

                val dynamicItems = beritaList.map { berita ->
                    DisplayItem(
                        title = berita.judul,
                        source = if (!berita.lokasi.isNullOrEmpty()) berita.lokasi!! else "User",
                        date = berita.createdAt,
                        imageUri = berita.cover,
                        onClick = { navController.navigate("news_detail_screen?beritaId=${berita.id}") }
                    )
                }

                (hardcodedItems + dynamicItems).sortedByDescending { parseDate(it.date) }
            }

            combinedList.forEach { item ->
                NewsItem(
                    imageRes = item.imageRes ?: R.drawable.introawal,
                    title = item.title,
                    source = item.source,
                    date = item.date,
                    imageUri = item.imageUri,
                    onClick = item.onClick
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(80.dp))
        }


        if (currentUsername != "superbin") {
            FloatingActionButton(
                onClick = { navController.navigate("berita_anda_screen") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                shape = CircleShape,
                containerColor = Color(0xFF4CAF50)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
            }
        }
    }
}

private fun parseDate(dateStr: String): Date {
    val locale = Locale("id", "ID")
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd HH:mm:ss",
        "d MMMM yyyy, HH:mm 'WIB'",
        "d MMMM yyyy, HH:mm",
        "d MMMM yyyy"
    )

    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, locale)
            val date = sdf.parse(dateStr)
            if (date != null) return date
        } catch (_: Exception) { }
    }
    return Date(0)
}

@Composable
private fun NewsItem(imageRes: Int, title: String, source: String, date: String, imageUri: String? = null, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            val painter = if (!imageUri.isNullOrEmpty()) {
                rememberAsyncImagePainter(model = imageUri.toUri())
            } else {
                painterResource(id = imageRes)
            }

            Image(
                painter = painter,
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
        val mockViewModel: BeritaViewModel = viewModel()
        NewsScreen(rememberNavController(), mockViewModel)
    }
}