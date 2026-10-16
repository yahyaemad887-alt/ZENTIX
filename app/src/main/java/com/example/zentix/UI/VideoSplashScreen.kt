package com.example.zentix.UI

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import com.example.zentix.R

@Composable
fun VideoSplashScreen(
    onVideoFinished: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                VideoView(context).apply {
                    // رابط الفيديو من مجلد res/raw باسم intro
                    val videoUri = Uri.parse("android.resource://${context.packageName}/${R.raw.intro}")
                    setVideoURI(videoUri)

                    setOnCompletionListener {
                        // أول ما الفيديو ينتهي بيتم استدعاء هذه الدالة للانتقال للتطبيق
                        onVideoFinished()
                    }

                    start()
                }
            }
        )
    }
}