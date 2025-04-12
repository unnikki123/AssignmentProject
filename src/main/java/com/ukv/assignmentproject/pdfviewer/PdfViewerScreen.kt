package com.ukv.assignmentproject.pdfviewer

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.ukv.assignmentproject.header.AppHeader
import com.ukv.assignmentproject.header.ScreenAppHeader

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PdfViewerScreen(pdfUrl: String, navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        ScreenAppHeader(title = "PDF Viewer", onBackClick = {navController.popBackStack()})

        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true // Allow JS for Google Docs Viewer
                        allowFileAccess = false // Prevent file access
                        allowContentAccess = false // Prevent content access
                        domStorageEnabled = true // Only if needed
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                            // Prevent navigating to other URLs
                            return url?.startsWith("https://docs.google.com") != true

                        }
                    }

                    loadUrl("https://docs.google.com/gview?embedded=true&url=$pdfUrl")
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        )

    }
}
