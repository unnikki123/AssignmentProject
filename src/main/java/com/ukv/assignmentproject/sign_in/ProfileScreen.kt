package com.ukv.assignmentproject.sign_in

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ukv.assignmentproject.header.AppHeader


@Composable
fun ProfileScreen(
    userData: User?,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(8.dp),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader("Assignment")
        if(userData?.profilePictureUrl != null) {
            AsyncImage(
                model = userData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if(userData?.username != null) {
            Text(
                text = userData.username,
                textAlign = TextAlign.Center,
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        HorizontalDivider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(0.5f) // Half width (optional for center look)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = onSignOut,
                modifier = Modifier
                    .width(250.dp)
                    .padding(bottom = 24.dp) // Optional: spacing from bottom
            ) {
                Text("Sign Out")
            }
        }
    }
}