package com.example.mybin.tampilan

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.rememberAsyncImagePainter
import com.example.mybin.R
import com.example.mybin.ui.theme.MyBinTheme
import com.example.mybin.viewmodel.BeritaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(navController: NavController, viewModel: BeritaViewModel? = null, beritaId: String? = null) {

    var judul by remember { mutableStateOf("Sampah plastik: Reduce dan Reuse dahulu sebelum Recycle") }
    var deskripsi by remember { mutableStateOf("Indonesia adalah salah satu negara penghasil sampah terbanyak di dunia...") }
    var tanggal by remember { mutableStateOf("22 Juli 2022") }
    var lokasi by remember { mutableStateOf("Indonesia") }
    var sumber by remember { mutableStateOf("Greenpeace Indonesia") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var imageRes by remember { mutableStateOf(R.drawable.introawal) }

    LaunchedEffect(beritaId) {
        if (beritaId != null && viewModel != null) {
            val berita = viewModel.getBeritaById(beritaId)
            if (berita != null) {

                judul = berita.judul
                deskripsi = berita.deskripsi
                tanggal = berita.createdAt
                lokasi = if (!berita.lokasi.isNullOrEmpty()) berita.lokasi else "Lokasi tidak tersedia"
                sumber = "User"
                imageUri = berita.cover
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News", color = Color(0xFF4CAF50)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Source Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = sumber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = tanggal, fontSize = 12.sp, color = Color.Gray)
                }
            }


            val painter = if (!imageUri.isNullOrEmpty()) {
                rememberAsyncImagePainter(model = Uri.parse(imageUri))
            } else {
                painterResource(id = imageRes)
            }

            Image(
                painter = painter,
                contentDescription = "News Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = lokasi, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))


                Text(
                    text = judul,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = deskripsi,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )


                if (beritaId == null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Indonesia sebagai ladang sampah",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sudah menjadi rahasia umum bahwa selain menjadi ladang penuh sumber daya pangan, Indonesia juga menjadi ladang sampah...",
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Chart",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewsDetailScreenPreview() {
    MyBinTheme {
        NewsDetailScreen(rememberNavController())
    }
}