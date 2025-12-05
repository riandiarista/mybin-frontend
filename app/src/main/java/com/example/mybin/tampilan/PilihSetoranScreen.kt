package com.example.mybin.tampilan

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

// Reusing SampahData from SampahkuScreen.kt
data class SampahSetorData(
    val id: Int,
    val jenis: String,
    val nama: String,
    val berat: String,
    val imageRes: Int,
    val jenisColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihSetoranScreen(navController: NavController) {
    val greenColor = Color(0xFF4CAF50)
    val sampleData = remember {
        listOf(
            SampahSetorData(1, "An Organik", "Botol Plastik", "1.5 Kg", R.drawable.introawal, Color(0xFF4CAF50)),
            SampahSetorData(2, "Organik", "Rumput", "5.0 Kg", R.drawable.introawal, Color.Green),
            SampahSetorData(3, "Sampah B3", "Baterai", "1.50 Kg", R.drawable.introawal, Color.Red),
            SampahSetorData(4, "An Organik", "Botol Plastik", "1.5 Kg", R.drawable.introawal, Color(0xFF4CAF50)),
            SampahSetorData(5, "An Organik", "Botol Plastik", "1.5 Kg", R.drawable.introawal, Color(0xFF4CAF50))
        )
    }
    val checkedState = remember { mutableStateListOf<Boolean>().apply { addAll(List(sampleData.size) { false }) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pilih Sampah Setoran",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                actions = { Spacer(Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = greenColor)
            )
        },
        bottomBar = {
            Button(
                onClick = { navController.navigate("AddAddressScreen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = greenColor)
            ) {
                Text("Setorkan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = checkedState.all { it },
                        onCheckedChange = {
                            val allChecked = checkedState.all { it }
                            for (i in checkedState.indices) {
                                checkedState[i] = !allChecked
                            }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = greenColor)
                    )
                    OutlinedButton(
                        onClick = {
                            val allChecked = checkedState.all { it }
                            for (i in checkedState.indices) {
                                checkedState[i] = !allChecked
                            }
                        },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Choose All")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            itemsIndexed(sampleData) { index, item ->
                PilihSetoranItem(item = item, isChecked = checkedState[index], onCheckedChange = { checkedState[index] = it })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PilihSetoranItem(item: SampahSetorData, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
            )
            Spacer(modifier = Modifier.width(8.dp))
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

@Preview(showBackground = true)
@Composable
fun PilihSetoranScreenPreview() {
    MyBinTheme {
        PilihSetoranScreen(rememberNavController())
    }
}
