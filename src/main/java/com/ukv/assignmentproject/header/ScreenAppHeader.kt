package com.ukv.assignmentproject.header

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ScreenAppHeader(
    title: String,
    onBackClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val waveColor = MaterialTheme.colorScheme.primaryContainer

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        // Background Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            primaryColor,
                            primaryColor.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Wave Effect
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(40.dp)
        ) {
            val width = size.width
            val height = size.height

            drawPath(
                path = Path().apply {
                    moveTo(0f, height * 0.4f)
                    cubicTo(
                        width * 0.25f, 0f,
                        width * 0.75f, height,
                        width, height * 0.4f
                    )
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                },
                color = waveColor
            )
        }

        // Header Content with Back Arrow
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBackClick() }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
