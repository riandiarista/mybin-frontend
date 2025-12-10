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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsDetailScreen(navController: NavController) {
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
                    painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with Greenpeace logo
                    contentDescription = "Source Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = "Greenpeace Indonesia", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "22 Juli 2022", fontSize = 12.sp, color = Color.Gray)
                }
            }
            Image(
                painter = painterResource(id = R.drawable.introawal), // Replace with news image
                contentDescription = "News Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Indonesia", fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sampah plastik: Reduce dan Reuse dahulu sebelum Recycle",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Indonesia adalah salah satu negara penghasil sampah terbanyak di dunia dengan menduduki urutan kedua kontributor sampah terbanyak di dunia setelah China. Berdasarkan temuan data yang dilansir dari laporan The World Bank bekerja sama dengan Kementerian Koordinator Bidang Kemaritiman dan investasi Republik Indonesia (Kemenko Maritim), Plastic Waste Discharge, menemukan bahwa Indonesia menghasilkan sompah sekitar 7,8 juta ton sampah plastik di lout setiap tahunnya, itu hanyalah sampah plastik, belum termasuk sampah jenis lainnya.\n\nSayangnya, hanya sebagian sampah plastik yang berhasil di daur ulang, Sisanya, sampah akan berakhir di tempat pembuangan akhir, mesin insinerator, menyumbat aliran air hingga mencemari dan mengancam blota lout. Belum cukup disitu, bahkan sampah dari negara lain juga dibuang di Indonesia .",
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Indonesia sebagai ladang sampah",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sudah menjadi rahasia umum bahwa selain menjadi ladang penuh sumber daya pangan, Indonesia juga menjadi ladang sampah. Walaupun begitu, fakta itu tidak menyurutkan pelaku usaha pengimpor sampah plastik dari negara kaya\n\nIndonesia sudah lama menjadi salah satu Importir sampah terbesar di dunia Dilansir dari data UN Comtrade, sekitar 188 ribu ton sampah plastik diimpor dari berbagai negara maju pada tahun 2020. Belanda menjadi pengimpor sampah plastik terbesar di Indonesia. Sebanyak 51,5 ribu ton sampah plastik diimpor dari negara tersebut. Tok hanya Belanda, negara seperti Jerman, Slovenia, Amerika Serikat, bahkan Singapura turut menjadi negara pengimpor.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with chart image
                    contentDescription = "Chart",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Mereka berdalih sampah yang diimpor dari luar negeri digunakan untuk didaur ulang. Anehnya, alih-alih mendaur ulang sampah di Indonesia yang telah menggunung dan tak tersentuh, justru malah menambah sampah dengan mengimpor dari negara lain. Ditambah, risiko dan dampak besar turut menggentayangi lingkungan dan kesehatan masyarakat akibat regulasi dan kebijakan pemerintah yang kendor.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
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
