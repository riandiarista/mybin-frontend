package com.example.mybin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mybin.tampilan.AddAddressScreen
import com.example.mybin.tampilan.DataSetoranScreen
import com.example.mybin.tampilan.DetailSampahScreen
import com.example.mybin.tampilan.LaporanScreen
import com.example.mybin.tampilan.LoginScreen
import com.example.mybin.tampilan.MainPage
import com.example.mybin.tampilan.NewsScreen
import com.example.mybin.tampilan.OnboardingScreen
import com.example.mybin.tampilan.PilihJenisSampahScreen
import com.example.mybin.tampilan.RecycleScreen
import com.example.mybin.tampilan.SampahkuScreen
import com.example.mybin.ui.theme.MyBinTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyBinTheme {
                val navController = rememberNavController()
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
                        NewsScreen(navController)
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
                        DetailSampahScreen(navController, jenisSampah, harga)
                    }
                    composable("SampahkuScreen") {
                        SampahkuScreen(navController)
                    }
                }
            }
        }
    }
}
