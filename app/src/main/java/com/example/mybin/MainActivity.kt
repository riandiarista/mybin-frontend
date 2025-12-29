package com.example.mybin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.mybin.tampilan.*
import com.example.mybin.ui.theme.MyBinTheme
import com.example.mybin.viewmodel.BeritaViewModel
import com.example.mybin.viewmodel.SampahViewModel
import com.example.mybin.viewmodel.SetoranViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifikasi aktif", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notifikasi tidak akan muncul", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        askNotificationPermission()


        checkPlayServices()


        fetchFcmToken()

        setContent {
            MyBinTheme {
                val navController = rememberNavController()
                val beritaViewModel: BeritaViewModel = viewModel()
                val sampahViewModel: SampahViewModel = viewModel()
                val setoranViewModel: SetoranViewModel = viewModel()

                NavHost(navController = navController, startDestination = "OnboardingScreen") {

                    composable("OnboardingScreen") { OnboardingScreen(navController) }
                    composable("LoginScreen") { LoginScreen(navController) }


                    composable("HomeAdmin") { HomeAdmin(navController) }
                    composable("VerifikasiSampahScreen") { VerifikasiSampahScreen(navController) }


                    composable("MainPage") { MainPage(navController, setoranViewModel) }
                    composable("LaporanScreen") { LaporanScreen(navController, setoranViewModel) }
                    composable("DataSetoranScreen") { DataSetoranScreen(navController, setoranViewModel) }

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


                    composable("NewsScreen") { NewsScreen(navController, beritaViewModel) }

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

                    composable("berita_anda_screen") { BeritaAndaScreen(navController, beritaViewModel) }
                    composable("buat_berita_screen") { BuatBeritaScreen(navController, beritaViewModel) }

                    composable(
                        "edit_berita_screen/{beritaId}",
                        arguments = listOf(navArgument("beritaId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val beritaId = backStackEntry.arguments?.getString("beritaId")
                        BuatBeritaScreen(navController, beritaViewModel, beritaId)
                    }


                    composable("notifikasi_screen") { NotifikasiScreen(navController) }
                    composable("profile_screen") { ProfileScreen(navController) }
                    composable("pengaturan_akun_screen") { PengaturanAkunScreen(navController) }


                    composable("pilih_setoran_screen") { PilihSetoranScreen(navController, sampahViewModel) }

                    composable(
                        "edit_sampah_screen/{sampahId}",
                        arguments = listOf(navArgument("sampahId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val sampahId = backStackEntry.arguments?.getString("sampahId")
                        if (sampahId != null) {
                            EditSampahScreen(navController, sampahId, sampahViewModel)
                        }
                    }

                    composable("RecycleScreen") { RecycleScreen(navController) }
                    composable("PilihJenisSampahScreen") { PilihJenisSampahScreen(navController) }

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

                    composable("SampahkuScreen") { SampahkuScreen(navController, sampahViewModel) }
                    composable("ExchangeScreen") { ExchangeScreen(navController, setoranViewModel) }
                }
            }
        }
    }

    /**
     * Memeriksa ketersediaan Google Play Services di perangkat.
     */
    private fun checkPlayServices() {
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e("FCM_CHECK", "Google Play Services tidak tersedia")
            if (availability.isUserResolvableError(resultCode)) {
                availability.getErrorDialog(this, resultCode, 9000)?.show()
            } else {
                Toast.makeText(this, "Perangkat ini tidak mendukung Google Play Services", Toast.LENGTH_LONG).show()
            }
        } else {
            Log.d("FCM_CHECK", "Google Play Services aktif")
        }
    }

    /**
     * Mengambil Token FCM untuk verifikasi di Logcat
     */
    private fun fetchFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_DEBUG", "Gagal mengambil token", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_DEBUG", "FCM Token: $token")
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