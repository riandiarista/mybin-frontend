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
import androidx.compose.material.icons.filled.MonetizationOn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mybin.R
import com.example.mybin.model.SampahData
import com.example.mybin.ui.theme.MyBinTheme
import com.example.mybin.viewmodel.SampahViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilihSetoranScreen(navController: NavController, sampahViewModel: SampahViewModel = viewModel()) {
    val greenColor = Color(0xFF4CAF50)
    val sampahList = sampahViewModel.sampahList
    val checkedState = remember { mutableStateListOf<Boolean>() }

    LaunchedEffect(sampahList.size) {
        checkedState.clear()
        checkedState.addAll(List(sampahList.size) { false })
    }

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
                onClick = { 
                    val selectedIds = sampahList.filterIndexed { index, _ -> checkedState[index] }.joinToString(",") { it.id }
                    val totalKoin = sampahList.filterIndexed { index, _ -> checkedState[index] }.sumOf { it.estimasiKoin }
                    navController.navigate("AddAddressScreen?sampahIds=$selectedIds&totalKoin=$totalKoin") 
                },
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
                        checked = checkedState.isNotEmpty() && checkedState.all { it },
                        onCheckedChange = {
                            val allChecked = checkedState.isNotEmpty() && checkedState.all { it }
                            for (i in checkedState.indices) {
                                checkedState[i] = !allChecked
                            }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = greenColor)
                    )
                    OutlinedButton(
                        onClick = {
                            val allChecked = checkedState.isNotEmpty() && checkedState.all { it }
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
            itemsIndexed(sampahList) { index, item ->
                if (index < checkedState.size) {
                    PilihSetoranItem(
                        item = item,
                        isChecked = checkedState[index],
                        onCheckedChange = { checkedState[index] = it }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun PilihSetoranItem(item: SampahData, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val jenisColor = when (item.jenisSampah) {
        "Anorganik" -> Color(0xFF4CAF50)
        "Organik" -> Color.Green
        "B3" -> Color.Red
        else -> Color.Gray
    }
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
            if (item.imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(item.imageUri),
                    contentDescription = item.detailSampah,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.introawal),
                    contentDescription = item.detailSampah,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.jenisSampah, color = jenisColor, fontSize = 12.sp)
                Text(text = item.detailSampah, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = item.totalBobot, color = Color.Gray, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Koin", fontSize = 12.sp, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Koin",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.estimasiKoin.toString(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFFFC107)
                    )
                }
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
