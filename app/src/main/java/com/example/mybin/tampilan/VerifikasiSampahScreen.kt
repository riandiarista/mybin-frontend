
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.model.SetoranData
import com.example.mybin.ui.theme.MyBinTheme
import com.example.mybin.viewmodel.SetoranViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifikasiSampahScreen(navController: NavController, setoranViewModel: SetoranViewModel = viewModel()) {
    val setoranList = setoranViewModel.setoranList

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verifikasi Sampah", fontWeight = FontWeight.Bold) },
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
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                DataStatusHeader()
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(setoranList) { setoran ->
                SetoranItem(setoran = setoran)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifikasiSampahScreenPreview() {
    MyBinTheme {
        VerifikasiSampahScreen(rememberNavController())
    }
}
