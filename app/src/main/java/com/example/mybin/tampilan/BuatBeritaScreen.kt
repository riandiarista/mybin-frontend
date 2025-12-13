package com.example.mybin.tampilan

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mybin.ui.theme.MyBinTheme
import com.example.mybin.viewmodel.BeritaViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun BuatBeritaScreen(navController: NavController, viewModel: BeritaViewModel, beritaId: String? = null) {
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tanggal by remember { mutableStateOf("") }
    var waktu by remember { mutableStateOf("") }

    LaunchedEffect(beritaId) {
        if (beritaId != null) {
            val berita = viewModel.getBeritaById(beritaId)
            if (berita != null) {
                judul = berita.title
                deskripsi = berita.description
                lokasi = berita.location
                if (berita.imageUri != null) {
                    imageUri = Uri.parse(berita.imageUri)
                }
                val dateParts = berita.date.split(", ")
                if (dateParts.size == 2) {
                    tanggal = dateParts[0]
                    waktu = dateParts[1].replace(" WIB", "")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(navController, isEdit = beritaId != null)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (beritaId != null) "Edit Berita Anda" else "Buat Berita Anda",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF006400)
            )
            Spacer(modifier = Modifier.height(24.dp))

            CustomTextField(label = "Judul Kegiatan", value = judul, onValueChange = { judul = it })
            Spacer(modifier = Modifier.height(16.dp))
            CustomTextField(label = "Deskripsi Detail", value = deskripsi, onValueChange = { deskripsi = it }, singleLine = false)
            Spacer(modifier = Modifier.height(16.dp))

            DateTimeSection(
                tanggal = tanggal,
                waktu = waktu,
                onTanggalChange = { tanggal = it },
                onWaktuChange = { waktu = it }
            )
            Spacer(modifier = Modifier.height(24.dp))

            EducationSection(
                selectedImageUri = imageUri,
                onImageSelected = { uri -> imageUri = uri }
            )
            Spacer(modifier = Modifier.height(24.dp))

            LocationSection(lokasi, onLocationChange = { lokasi = it })
            Spacer(modifier = Modifier.height(32.dp))

            ActionButtons(
                onSubmit = {
                    if (judul.isNotBlank()) {
                        val imageString = imageUri?.toString()
                        if (beritaId != null) {
                            viewModel.updateBerita(beritaId, judul, deskripsi, lokasi, imageString, tanggal, waktu)
                        } else {
                            viewModel.addBerita(judul, deskripsi, lokasi, imageString, tanggal, waktu)
                        }
                        navController.popBackStack()
                    }
                },
                onDelete = {
                    if (beritaId != null) {
                        viewModel.deleteBerita(beritaId)
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

@Composable
private fun TopAppBar(navController: NavController, isEdit: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(colors = listOf(Color(0xFFA5D6A7), Color(0xFFC8E6C9)))
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }
        Text(
            text = if (isEdit) "Edit Berita" else "Buat Berita",
            modifier = Modifier.align(Alignment.Center),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, singleLine: Boolean = true) {
    Column {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0xFF4CAF50),
                unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f)
            ),
            singleLine = singleLine,
            minLines = if (!singleLine) 5 else 1
        )
    }
}

@Composable
private fun DateTimeSection(
    tanggal: String,
    waktu: String,
    onTanggalChange: (String) -> Unit,
    onWaktuChange: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(y, m, d)
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
            onTanggalChange(dateFormat.format(selectedDate.time))
        },
        year, month, day
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, h, m -> onWaktuChange("$h:$m") },
        hour, minute, true
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Tanggal Kegiatan", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (tanggal.isNotBlank()) tanggal else "Pilih Tanggal")
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Waktu Kegiatan", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedButton(
                onClick = { timePickerDialog.show() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (waktu.isNotBlank()) waktu else "Pilih Waktu")
            }
        }
    }
}

@Composable
private fun EducationSection(
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            onImageSelected(tempImageUri)
        }
    }

    fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = context.cacheDir
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Foto Kegiatan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (selectedImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { 
                        val photoFile = createImageFile(context)
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )
                        tempImageUri = uri
                        cameraLauncher.launch(uri)
                    }, 
                    modifier = Modifier.weight(1f), 
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ambil Foto")
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") }, 
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (selectedImageUri != null) "Ganti Foto" else "Unggah Foto")
                }
            }
        }
    }
}

@Composable
private fun LocationSection(location: String, onLocationChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFFFA000))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lokasi Kegiatan", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mengganti Text statis menjadi CustomTextField untuk input manual
            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Masukkan lokasi kegiatan...") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color(0xFFFFA000),
                    unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f)
                ),
                singleLine = true
            )
        }
    }
}

@Composable
private fun ActionButtons(onSubmit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(48.dp)
                .background(Color.Red.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, Color.Red, CircleShape)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
        }
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Submit Berita", fontSize = 16.sp)
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun BuatBeritaScreenPreview() {
    MyBinTheme {
        BuatBeritaScreen(rememberNavController(), BeritaViewModel())
    }
}
