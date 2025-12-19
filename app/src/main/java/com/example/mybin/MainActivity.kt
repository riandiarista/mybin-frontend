package com.example.mybin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mybin.tampilan.AddAddressScreen
import com.example.mybin.tampilan.BeritaAndaScreen
import com.example.mybin.tampilan.BuatBeritaScreen
import com.example.mybin.tampilan.DataSetoranScreen
import com.example.mybin.tampilan.DetailSampahScreen
import com.example.mybin.tampilan.EditSampahScreen
import com.example.mybin.tampilan.ExchangeScreen
import com.example.mybin.tampilan.LaporanScreen
import com.example.mybin.tampilan.LoginScreen
import com.example.mybin.tampilan.MainPage
import com.example.mybin.tampilan.NewsDetailScreen
import com.example.mybin.tampilan.NewsScreen
import com.example.mybin.tampilan.NotifikasiScreen
import com.example.mybin.tampilan.OnboardingScreen
import com.example.mybin.tampilan.PengaturanAkunScreen
import com.example.mybin.tampilan.PilihJenisSampahScreen
import com.example.mybin.tampilan.PilihSetoranScreen
import com.example.mybin.tampilan.ProfileScreen
import com.example.mybin.tampilan.RecycleScreen
import com.example.mybin.tampilan.SampahkuScreen
import com.example.mybin.tampilan.HomeAdmin
import com.example.mybin.tampilan.VerifikasiSampahScreen
import com.example.mybin.ui.theme.MyBinTheme
import com.example.mybin.viewmodel.BeritaViewModel
import com.example.mybin.viewmodel.SampahViewModel
import com.example.mybin.viewmodel.SetoranViewModel

class MainActivity : ComponentActivity() {

    // Registrasi request permission handler
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Izin diberikan
            Toast.makeText(this, "Notifikasi aktif", Toast.LENGTH_SHORT).show()
        } else {
            // Izin ditolak
            Toast.makeText(this, "Notifikasi tidak akan muncul", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Panggil fungsi cek izin saat aplikasi dibuka
        askNotificationPermission()

        setContent {
            MyBinTheme {
                val navController = rememberNavController()
                val beritaViewModel: BeritaViewModel = viewModel()
                val sampahViewModel: SampahViewModel = viewModel()
                val setoranViewModel: SetoranViewModel = viewModel()

                NavHost(navController = navController, startDestination = "OnboardingScreen") {
                    composable("OnboardingScreen") {
                        OnboardingScreen(navController)
                    }
                    composable("LoginScreen") {
                        LoginScreen(navController)
                    }

                    // --- ROUTE ADMIN ---
                    composable("HomeAdmin") {
                        HomeAdmin(navController)
                    }

                    composable("VerifikasiSampahScreen") {
                        VerifikasiSampahScreen(navController)
                    }

                    composable("MainPage") {
                        MainPage(navController)
                    }
                    composable("LaporanScreen") {
                        LaporanScreen(navController)
                    }
                    composable("DataSetoranScreen") {
                        DataSetoranScreen(navController, setoranViewModel)
                    }
                    composable(
                        "AddAddressScreen?sampahIds={sampahIds}&totalKoin={totalKoin}",
                        arguments = listOf(
                            navArgument("sampahIds") { type = NavType.StringType; nullable = true },
                            navArgument("totalKoin") { type = NavType.IntType; defaultValue = 0 }
                        )
                    ) { backStackEntry ->
                        AddAddressScreen(
                            navController = navController,
                            sampahViewModel = sampahViewModel,
                            setoranViewModel = setoranViewModel,
                            sampahIds = backStackEntry.arguments?.getString("sampahIds"),
                            totalKoin = backStackEntry.arguments?.getInt("totalKoin")
                        )
                    }
                    composable("NewsScreen") {
                        NewsScreen(navController, beritaViewModel)
                    }

                    // PENYELARASAN: Pastikan rute detail menggunakan format yang konsisten
                    composable(
                        "news_detail_screen?beritaId={beritaId}",
                        arguments = listOf(navArgument("beritaId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        })
                    ) { backStackEntry ->
                        val beritaId = backStackEntry.arguments?.getString("beritaId")
                        NewsDetailScreen(navController, beritaViewModel, beritaId)
                    }

                    composable("berita_anda_screen") {
                        BeritaAndaScreen(navController, beritaViewModel)
                    }

                    composable("buat_berita_screen") {
                        BuatBeritaScreen(navController, beritaViewModel)
                    }

                    // PERBAIKAN: Rute khusus untuk Edit Berita agar ID terkirim dengan benar ke BuatBeritaScreen
                    composable(
                        "edit_berita_screen/{beritaId}",
                        arguments = listOf(navArgument("beritaId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val beritaId = backStackEntry.arguments?.getString("beritaId")
                        // Memanggil BuatBeritaScreen dengan beritaId untuk mode EDIT
                        BuatBeritaScreen(navController, beritaViewModel, beritaId)
                    }

                    composable("notifikasi_screen") {
                        NotifikasiScreen(navController)
                    }
                    composable("profile_screen") {
                        ProfileScreen(navController)
                    }
                    composable("pengaturan_akun_screen") {
                        PengaturanAkunScreen(navController)
                    }
                    composable("pilih_setoran_screen") {
                        PilihSetoranScreen(navController, sampahViewModel)
                    }
                    composable(
                        "edit_sampah_screen/{sampahId}",
                        arguments = listOf(
                            navArgument("sampahId") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val sampahId = backStackEntry.arguments?.getString("sampahId")
                        if (sampahId != null) {
                            EditSampahScreen(
                                navController,
                                sampahId,
                                sampahViewModel
                            )
                        }
                    }
                    composable("RecycleScreen") {
                        RecycleScreen(navController)
                    }
                    composable("PilihJenisSampahScreen") {
                        PilihJenisSampahScreen(navController)
                    }
                    composable(
                        "DetailSampahScreen/{jenisSampah}/{harga}",
                        arguments = listOf(
                            navArgument("jenisSampah") { type = NavType.StringType },
                            navArgument("harga") { type = NavType.StringType }
                        )
                    ) {
                        val jenisSampah = it.arguments?.getString("jenisSampah") ?: ""
                        val harga = it.arguments?.getString("harga") ?: ""
                        DetailSampahScreen(navController, jenisSampah, harga, sampahViewModel)
                    }
                    composable("SampahkuScreen") {
                        SampahkuScreen(navController, sampahViewModel)
                    }
                    composable("ExchangeScreen") {
                        ExchangeScreen(navController)
                    }
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}