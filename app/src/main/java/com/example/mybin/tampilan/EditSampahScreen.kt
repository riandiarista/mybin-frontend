package com.example.mybin.tampilan

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.mybin.R
import com.example.mybin.viewmodel.SampahViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditSampahScreen(navController: NavController, sampahId: String, sampahViewModel: SampahViewModel = viewModel()) {
    val sampah = sampahViewModel.getSampahById(sampahId)

    if (sampah != null) {
        var detailSampah by remember { mutableStateOf(sampah.detailSampah) }
        var totalBobot by remember { mutableStateOf(sampah.totalBobot.removeSuffix(" Kg")) }
        var imageUri by remember { mutableStateOf(sampah.imageUri) }
        var tempImageUri by remember { mutableStateOf<Uri?>(null) }
        val context = LocalContext.current

        var estimasiKoin by remember { mutableStateOf(sampah.estimasiKoin) }

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri -> imageUri = uri }
        )

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                if (success) {
                    imageUri = tempImageUri
                }
            }
        )

        fun createImageFile(context: Context): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir = context.cacheDir
            return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        }
        
        fun calculateKoin() {
            val bobot = totalBobot.toFloatOrNull() ?: 0f
            val koinPerKg = when (sampah.jenisSampah) {
                "Organik" -> 1000
                "Anorganik" -> 2000
                "B3" -> 1500
                else -> 0
            }
            estimasiKoin = (bobot * koinPerKg).toInt()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.orang),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF4CAF50))
                    }
                    Text(
                        text = "Edit Sampah ${sampah.jenisSampah}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Price",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(text = "$estimasiKoin Koin", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(horizontalAlignment = Alignment.Start) {
                    Text("Detail Sampah", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(
                        value = detailSampah,
                        onValueChange = { detailSampah = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Total Bobot (kg)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    OutlinedTextField(
                        value = totalBobot,
                        onValueChange = { 
                            totalBobot = it
                            calculateKoin()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (imageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Text("Upload Foto Sampah", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Bisa berupa foto atau dokumen pendukung", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = "Pilih Foto", tint = Color.Black)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Pilih Foto", color = Color.Black)
                        }
                        Button(
                            onClick = {
                                val photoFile = createImageFile(context)
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", photoFile)
                                tempImageUri = uri
                                cameraLauncher.launch(uri)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Kamera", tint = Color.Black)
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Kamera", color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val updatedSampah = sampah.copy(
                                detailSampah = detailSampah,
                                totalBobot = "$totalBobot Kg",
                                imageUri = imageUri,
                                estimasiKoin = estimasiKoin
                            )
                            sampahViewModel.updateSampah(updatedSampah)
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Simpan Perubahan", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    } else {
        // Handle case where sampah is not found
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sampah tidak ditemukan.")
        }
    }
}
