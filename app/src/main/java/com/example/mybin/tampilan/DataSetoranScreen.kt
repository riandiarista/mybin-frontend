package com.example.mybin.tampilan

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybin.model.SetoranData
import com.example.mybin.viewmodel.SetoranViewModel


fun decodeBase64ToBitmap(base64Str: String?): Bitmap? {
    if (base64Str.isNullOrEmpty()) return null
    return try {
        val pureBase64 = if (base64Str.contains(",")) base64Str.split(",")[1] else base64Str
        val imageBytes = Base64.decode(pureBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataSetoranScreen(navController: NavController, setoranViewModel: SetoranViewModel = viewModel()) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedSetoranId by remember { mutableStateOf<String?>(null) }

    
    LaunchedEffect(Unit) {
        setoranViewModel.getSetoran()
    }

    val setoranList = setoranViewModel.setoranList

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus data setoran ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedSetoranId?.let { id ->
                            setoranViewModel.deleteSetoran(
                                setoranId = id.toInt(),
                                onSuccess = { pesan ->
                                    Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show()
                                },
                                onError = { pesan ->
                                    Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data Setoran", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { setoranViewModel.getSetoran() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
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
                TambahDataButton(navController)
                Spacer(modifier = Modifier.height(16.dp))
                DataStatusHeader()
                Spacer(modifier = Modifier.height(8.dp))

                if (setoranList.isEmpty() && !setoranViewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Belum ada sampah yang disetor", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            items(setoranList) { setoran ->
                SetoranItem(
                    setoran = setoran,
                    onDeleteClick = {
                        selectedSetoranId = setoran.id
                        showDeleteDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun SetoranItem(setoran: SetoranData, onDeleteClick: () -> Unit) {
    val isPending = setoran.status.contains("menunggu", ignoreCase = true) ||
            setoran.status.contains("Diproses", ignoreCase = true)

    val barColor = if (isPending) Color(0xFFF0AD4E) else Color(0xFF2EBD70)
    val chipColor = if (isPending) Color(0xFFFFFBE6) else Color(0xFFD7F5E6)
    val chipContentColor = if (isPending) Color(0xFFF0AD4E) else Color(0xFF2EBD70)

    
    val bitmap = remember(setoran.detailFoto) {
        decodeBase64ToBitmap(setoran.detailFoto)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(8.dp).fillMaxHeight().background(barColor))
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Setoran ${setoran.id}", fontWeight = FontWeight.Bold)
                    Text(setoran.tanggal, fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Foto Sampah",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Redeem, contentDescription = null, tint = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(setoran.jenis, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(setoran.lokasi, fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MonetizationOn, contentDescription = "Koin", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${setoran.totalKoin} Poin", fontSize = 14.sp, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = chipColor)) {
                            Text(setoran.status.uppercase(), color = chipContentColor, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Hapus",
                            color = Color.Red,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.clickable { onDeleteClick() }
                        )
                    }
                    Text(if (isPending) "Status +" else "Lihat Detail", color = if (isPending) Color.Blue else Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun TambahDataButton(navController: NavController) {
    val gradient = Brush.horizontalGradient(listOf(Color(0xFF86F3B8), Color(0xFF53E690)))
    Button(
        onClick = { navController.navigate("pilih_setoran_screen") },
        modifier = Modifier.fillMaxWidth().height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(modifier = Modifier.background(gradient).fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tambah Data +", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun DataStatusHeader() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Data & Status", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = { /* Filter */ }) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.Gray)
        }
    }
}