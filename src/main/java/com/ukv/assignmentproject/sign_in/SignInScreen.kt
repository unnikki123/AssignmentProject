package com.ukv.assignmentproject.sign_in

import android.R.attr.bitmap
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.ukv.assignmentproject.R
import com.ukv.assignmentproject.header.AppHeader
import java.io.File
import java.net.URLEncoder

@Composable
fun SignInScreen(
    state: SignInState,
    onGoogleSignInClick: () -> Unit,
    navController: NavController
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var selectedGalleryImageUri by remember { mutableStateOf<Uri?>(null) }
// Prepare file for camera
    val photoFile = remember {
        File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "photo_${System.currentTimeMillis()}.jpg"
        )
    }

    val cameraLauncher =
        rememberLauncherForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                imageBitmap = bitmap
            }
        }

    val galleryLauncher = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it

            //Log.d("GalleryImage", "URI: $uri, Bitmap: ${bitmap.width}x${bitmap.height}")


            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.use {
                    val bitmap = BitmapFactory.decodeStream(it)
                    imageBitmap = bitmap
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Unable to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            imageBitmap = BitmapFactory.decodeStream(inputStream)
        }
    }
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppHeader("Assignment Project")
        Spacer(modifier = Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(stringResource(R.string._1_user_authentication))
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onGoogleSignInClick,
                modifier = Modifier
                    .wrapContentWidth()
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_google), // Your Google logo here
                    contentDescription = "Google Logo",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
                Text("Login with Google")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string._2_report_pdf_viewer))
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val pdfUrl =
                        context.getString(R.string.url)
                    navController.navigate("pdf_viewer?url=${URLEncoder.encode(pdfUrl, "UTF-8")}")
                }) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "PDF Icon",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                )
                Text("View PDF")
            }
            Spacer(modifier = Modifier.height(8.dp))


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string._3_image_capture_gallery_selection))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = {
                        val uri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            photoFile
                        )
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        cameraLauncher.launch(intent)
                    }) {
                        Icon(Icons.Filled.Face, contentDescription = "Camera")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Camera")
                    }

                    Button(onClick = {
                        pickImageLauncher.launch("image/*") // This is important!
                    }) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Gallery Icon",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text("Gallery")
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string._4_room_db_implementation))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                    navController.navigate("products")
                }) {
                    Icon(Icons.Filled.Info, contentDescription = "Api")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Room DB")
                }

                Spacer(modifier = Modifier.height(24.dp))

                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
            }
        }
    }
}