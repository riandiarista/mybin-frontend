package com.example.mybin.tampilan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.ui.theme.MyBinTheme

enum class NotificationType {
    Urgent, Schedule, General
}

data class Notification(val id: Int, val type: NotificationType, val title: String, val description: String, val time: String)

private data class NotificationCardConfig(val icon: ImageVector, val color: Color, val bgColor: Color)

@Composable
fun NotifikasiScreen(navController: NavController) {
    val notifications = remember {
        listOf(
            Notification(1, NotificationType.Urgent, "Aksi Mendesak: Perlu Pembaruan!", "Anda memiliki 3 kategori sampah (seperti Baterai, Kabel, Botol Kaca) yang belum diperbarui harga/poinnya dalam 30 hari terakhir.", "10:30 WIB"),
            Notification(2, NotificationType.Schedule, "Jadwal Penjemputan Sampah", "Pengambilan setoran Anda hari ini dijadwalkan pukul 14:00. Mohon siapkan sampah Anda!", "09:00 WIB"),
            Notification(3, NotificationType.General, "Selamat! Poin Bertambah", "Anda berhasil menukar 5 Kg botol plastik dan mendapatkan 5.000 Poin. Tukar jadi uang atau voucher!", "Kemarin")
        )
    }

    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { MyBinBottomNavBar(navController = navController, activeScreen = "Notifikasi") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F9F7))
        ) {
            FilterSection()
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Terbaru", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(notifications.filter { it.type != NotificationType.General }) { notification ->
                    NotificationCard(notification)
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sebelumnya", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(notifications.filter { it.type == NotificationType.General }) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
private fun TopBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(colors = listOf(Color(0xFF2E7D32), Color(0xFF4CAF50)))
            )
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        IconButton(onClick = { navController.navigate("MainPage") }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Text(
            text = "Pusat Notifikasi",
            modifier = Modifier.align(Alignment.Center),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection() {
    var selectedChip by remember { mutableStateOf("All") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val chipColors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF388E3C),
            selectedLabelColor = Color.White,
            containerColor = Color.White
        )

        listOf("All", "News").forEach { title ->
            FilterChip(
                selected = selectedChip == title,
                onClick = { selectedChip = title },
                label = { Text(title) },
                colors = chipColors,
                shape = RoundedCornerShape(16.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = { /*TODO*/ }) {
            Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Filter", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun NotificationCard(notification: Notification) {
    val config = when (notification.type) {
        NotificationType.Urgent -> NotificationCardConfig(icon = Icons.Default.LocalFlorist, color = Color(0xFF2E7D32), bgColor = Color(0xFFE8F5E9))
        NotificationType.Schedule -> NotificationCardConfig(icon = Icons.Default.CalendarToday, color = Color(0xFF1976D2), bgColor = Color(0xFFE3F2FD))
        NotificationType.General -> NotificationCardConfig(icon = Icons.Default.EmojiEvents, color = Color(0xFFF57C00), bgColor = Color(0xFFFFF3E0))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(config.color, shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            )
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(config.bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(config.icon, contentDescription = null, tint = config.color, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(notification.title, fontWeight = FontWeight.Bold)
                    Text(notification.description, fontSize = 14.sp, color = Color.Gray)
                    if (notification.type == NotificationType.Urgent) {
                        Row(Modifier.padding(top = 8.dp)) {
                            TextButton(onClick = { /*TODO*/ }, modifier = Modifier.height(36.dp)) {
                                Text("Perbarui Sekarang")
                            }
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = { /*TODO*/ }, modifier = Modifier.height(36.dp), colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)) {
                                Text("Tunda (1 Hari)")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(notification.time, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotifikasiScreenPreview() {
    MyBinTheme {
        NotifikasiScreen(rememberNavController())
    }
}
