package com.example.mybin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.mybin.ui.theme.MyBinTheme
import com.example.mybin.viewmodel.BeritaViewModel
import com.example.mybin.viewmodel.SampahViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyBinTheme {
                val navController = rememberNavController()
                val beritaViewModel: BeritaViewModel = viewModel()
                val sampahViewModel: SampahViewModel = viewModel()

                NavHost(navController = navController, startDestination = "OnboardingScreen") {
                    composable("OnboardingScreen") {
                        OnboardingScreen(navController)
                    }
                    composable("LoginScreen") {
                        LoginScreen(navController)
                    }
                    composable("MainPage") {
                        MainPage(navController)
                    }
                    composable("LaporanScreen") {
                        LaporanScreen(navController)
                    }
                    composable("DataSetoranScreen") {
                        DataSetoranScreen(navController)
                    }
                    composable("AddAddressScreen") {
                        AddAddressScreen(navController)
                    }
                    composable("NewsScreen") {
                        NewsScreen(navController, beritaViewModel)
                    }
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
                    composable(
                        "edit_berita_screen/{beritaId}",
                        arguments = listOf(navArgument("beritaId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val beritaId = backStackEntry.arguments?.getString("beritaId")
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
                        PilihSetoranScreen(navController)
                    }
                    composable(
                        "edit_sampah_screen/{sampahId}",
                        arguments = listOf(
                            navArgument("sampahId") { type = NavType.StringType }
                        )
                    ) {
                        EditSampahScreen(
                            navController,
                            it.arguments?.getString("sampahId"),
                            sampahViewModel
                        )
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
}
