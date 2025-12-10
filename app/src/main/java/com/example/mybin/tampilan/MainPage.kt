package com.example.mybin.tampilan

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

private enum class MenuState {
    EXPANDED,
    COLLAPSED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(navController: NavController) {
    val greenColor = Color(0xFF2EBD70)
    var menuState by remember { mutableStateOf(MenuState.COLLAPSED) }
    val transition = updateTransition(targetState = menuState, label = "Menu Transition")

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val width = this.maxWidth

                    val itemAlpha by transition.animateFloat(label = "item_alpha") { state ->
                        if (state == MenuState.EXPANDED) 1f else 0f
                    }
                    val homeOffset by transition.animateDp(label = "home_offset") { state ->
                        if (state == MenuState.EXPANDED) -(width * 0.4f) else 0.dp
                    }
                    val activityOffset by transition.animateDp(label = "activity_offset") { state ->
                        if (state == MenuState.EXPANDED) -(width * 0.2f) else 0.dp
                    }
                    val messageOffset by transition.animateDp(label = "message_offset") { state ->
                        if (state == MenuState.EXPANDED) (width * 0.2f) else 0.dp
                    }
                    val accountOffset by transition.animateDp(label = "account_offset") { state ->
                        if (state == MenuState.EXPANDED) (width * 0.4f) else 0.dp
                    }

                    BottomNavItem(
                        modifier = Modifier
                            .offset(x = homeOffset)
                            .alpha(itemAlpha),
                        icon = Icons.Default.Home,
                        label = "Home",
                        isSelected = true
                    )
                    BottomNavItem(
                        modifier = Modifier
                            .offset(x = activityOffset)
                            .alpha(itemAlpha),
                        icon = Icons.AutoMirrored.Filled.List,
                        label = "Laporan",
                        onClick = { navController.navigate("LaporanScreen") }
                    )
                    BottomNavItem(
                        modifier = Modifier
                            .offset(x = messageOffset)
                            .alpha(itemAlpha),
                        icon = Icons.Default.Notifications,
                        label = "Notifikasi",
                        onClick = { navController.navigate("notifikasi_screen") }
                    )
                    BottomNavItem(
                        modifier = Modifier
                            .offset(x = accountOffset)
                            .alpha(itemAlpha),
                        icon = Icons.Default.Person,
                        label = "Account",
                        onClick = { navController.navigate("profile_screen") } // Navigate to ProfileScreen
                    )

                    FloatingActionButton(
                        onClick = {
                            menuState = if (menuState == MenuState.EXPANDED) MenuState.COLLAPSED else MenuState.EXPANDED
                        },
                        shape = CircleShape,
                        containerColor = greenColor,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recycle", tint = Color.White)
                    }
                }
            }
        },
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Image(
                painter = painterResource(id = R.drawable.orang), // Ganti dengan gambar latar belakang Anda
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column {
                // Top Greeting Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.introawal), // Ganti dengan gambar profil Anda
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Selamat Sore, Puan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = greenColor)
                            Text("Yuk daur ulang sampahmu", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Main Content
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Selection", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = greenColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "Bin Points", tint = Color(0xFFFFC107))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("0 Bin Points", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        // Grid of selections
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            SelectionItem(icon = Icons.Default.Refresh, label = "Recycle", onClick = { navController.navigate("RecycleScreen") })
                            SelectionItem(icon = Icons.Default.Savings, label = "Deposit", onClick = { navController.navigate("DataSetoranScreen") })
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            SelectionItem(icon = Icons.Default.CurrencyExchange, label = "Exchange", onClick = { navController.navigate("ExchangeScreen") })
                            SelectionItem(icon = Icons.Default.Info, label = "News", onClick = { navController.navigate("NewsScreen") })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(modifier: Modifier = Modifier, icon: ImageVector, label: String, isSelected: Boolean = false, onClick: () -> Unit = {}) {
    val greenColor = Color(0xFF2EBD70)
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = if (isSelected) greenColor else Color.Gray)
        Text(label, fontSize = 12.sp, color = if (isSelected) greenColor else Color.Gray)
    }
}

@Composable
fun SelectionItem(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Card(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = Color(0xFF2EBD70), modifier = Modifier.size(40.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontWeight = FontWeight.Medium, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun MainPagePreview() {
    MyBinTheme {
        MainPage(rememberNavController())
    }
}
