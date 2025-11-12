package com.example.mybin.tampilan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.mybin.R
import com.example.mybin.ui.theme.MyBinTheme

private class WavyTopShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            val waveHeight = 60.dp.toPx(density)
            moveTo(0f, waveHeight)

            // First wave
            quadraticTo(
                x1 = size.width * 0.25f,
                y1 = waveHeight + 40.dp.toPx(density),
                x2 = size.width * 0.5f,
                y2 = waveHeight
            )

            // Second wave
            quadraticTo(
                x1 = size.width * 0.75f,
                y1 = waveHeight - 40.dp.toPx(density),
                x2 = size.width,
                y2 = waveHeight
            )

            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }

    private fun Dp.toPx(density: Density) = with(density) { this@toPx.toPx() }
}

@Composable
fun OnboardingScreen(navController: NavController) {
    val greenColor = Color(0xFF2EBD70)
    val subtitleColor = Color(0xFF93C5A5)

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.orang),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Content layered on top
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.65f) // Adjusted content height
                    .graphicsLayer { clip = true; shape = WavyTopShape() }
                    .background(Color.White)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(170.dp)) // Adjust spacer for the new wave shape

                Text(
                    text = """Ayo Bergabung
Bersama Kami""",
                    color = greenColor,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 44.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = """Menjadi agen Peduli pengolahan
sampah Peduli masa depan""",
                    color = subtitleColor,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 25.sp
                )

                Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom

                Button(
                    onClick = { navController.navigate("LoginScreen") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor)
                ) {
                    Text(
                        text = "Let's Get Started",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingPreview() {
    MyBinTheme {
        OnboardingScreen(rememberNavController())
    }
}
