package com.example.zentix.UI

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Process
import android.view.ViewGroup
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.zentix.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ConsoleAppGame(
    val title: String,
    val packageName: String,
    val appIcon: Drawable?
)

private const val PREFS_NAME = "zentix_console_prefs"
private const val KEY_SAVED_GAMES = "saved_packages"

private fun saveGamesToPrefs(context: Context, packages: List<String>) {
    try {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_SAVED_GAMES, packages.toSet()).apply()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun loadGamesFromPrefs(context: Context): Set<String> {
    return try {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.getStringSet(KEY_SAVED_GAMES, emptySet()) ?: emptySet()
    } catch (e: Exception) {
        emptySet()
    }
}

@Composable
fun GameModeScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // ألوان تفاعلية بناءً على الوضع الداكن العام للتطبيق
    val isDark = ZentixGlobalSettings.isDarkMode
    val cinematicBlack = if (isDark) Color(0xFF010204) else Color(0xFFF4F6FA)
    val ps3GlassCard = if (isDark) Color(0x26FFFFFF) else Color(0x26000000)
    val ps3GlassBorder = if (isDark) Color(0x4DFFFFFF) else Color(0x4D000000)
    val pureWhiteText = if (isDark) Color(0xFFFFFFFF) else Color(0xFF1A1D26)
    val mutedTextGray = if (isDark) Color(0xFFA0A6B8) else Color(0xFF6B7280)

    DisposableEffect(Unit) {
        val activity = context as? Activity
        val originalOrientation = activity?.requestedOrientation
        try {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            activity?.window?.let { window ->
                WindowCompat.setDecorFitsSystemWindows(window, false)
                WindowInsetsControllerCompat(window, window.decorView).let { controller ->
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onDispose {
            try {
                activity?.requestedOrientation = originalOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                activity?.window?.let { window ->
                    WindowCompat.setDecorFitsSystemWindows(window, true)
                    WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    DisposableEffect(Unit) {
        try {
            System.gc()
            Runtime.getRuntime().gc()
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onDispose {}
    }

    var globalMediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var startupMediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isContentLoaded by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val startupPlayer = try {
            MediaPlayer.create(context, R.raw.ps3_startup)?.apply {
                setVolume(0.8f, 0.8f)
                setOnCompletionListener {
                    isContentLoaded = true
                    it.release()
                }
                start()
            }
        } catch (e: Exception) {
            isContentLoaded = true
            null
        }
        startupMediaPlayer = startupPlayer

        val player = try {
            MediaPlayer.create(context, R.raw.nostologia)?.apply {
                isLooping = true
                setVolume(0.25f, 0.25f)
                start()
            }
        } catch (e: Exception) {
            null
        }
        globalMediaPlayer = player

        onDispose {
            try {
                startupMediaPlayer?.release()
                player?.let {
                    if (it.isPlaying) it.stop()
                    it.release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(2500)
        isContentLoaded = true
    }

    fun playClickSound() {
        try {
            val clickPlayer = MediaPlayer.create(context, R.raw.ps3_click)
            clickPlayer?.setVolume(0.5f, 0.5f)
            clickPlayer?.setOnCompletionListener { mp -> mp.release() }
            clickPlayer?.start()
        } catch (e: Exception) {}
    }

    fun stopMusicAndProceed() {
        try {
            startupMediaPlayer?.release()
            globalMediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
            globalMediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    BackHandler {
        stopMusicAndProceed()
        onNavigateBack()
    }

    var customGamesList by remember { mutableStateOf(emptyList<ConsoleAppGame>()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val savedPkgs = loadGamesFromPrefs(context)
                val pm = context.packageManager
                val list = mutableListOf<ConsoleAppGame>()
                for (pkg in savedPkgs) {
                    try {
                        val appInfo = pm.getApplicationInfo(pkg, 0)
                        val title = pm.getApplicationLabel(appInfo).toString()
                        val icon = pm.getApplicationIcon(pkg)
                        list.add(ConsoleAppGame(title, pkg, icon))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                customGamesList = list.sortedBy { it.title }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    var selectedGameIndex by remember { mutableIntStateOf(0) }
    var currentTime by remember { mutableStateOf("") }

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showAppPickerScreen by remember { mutableStateOf(false) }
    var showConfigScreen by remember { mutableStateOf(false) }
    var showOptionsMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            currentTime = sdf.format(Date())
            delay(1000)
        }
    }

    val filteredGames = customGamesList.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    val activeGame = if (filteredGames.isNotEmpty()) {
        filteredGames.getOrElse(selectedGameIndex) { filteredGames[0] }
    } else {
        null
    }

    if (showAppPickerScreen) {
        FullPageAppPickerScreen(
            context = context,
            existingGames = customGamesList,
            onDismiss = { showAppPickerScreen = false },
            onGamesSelected = { selectedApps ->
                customGamesList = selectedApps
                saveGamesToPrefs(context, selectedApps.map { it.packageName })
                showAppPickerScreen = false
            },
            cinematicBlack = cinematicBlack,
            ps3GlassCard = ps3GlassCard,
            ps3GlassBorder = ps3GlassBorder,
            pureWhiteText = pureWhiteText,
            mutedTextGray = mutedTextGray
        )
        return
    }

    if (showConfigScreen) {
        ZentixGameConfigScreen(
            onNavigateBack = { showConfigScreen = false }
        )
        return
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cinematicBlack)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { event ->
                val nativeEvent = event.nativeKeyEvent
                if (nativeEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                    when (nativeEvent.keyCode) {
                        android.view.KeyEvent.KEYCODE_DPAD_RIGHT, android.view.KeyEvent.KEYCODE_SYSTEM_NAVIGATION_RIGHT -> {
                            if (filteredGames.isNotEmpty()) {
                                selectedGameIndex = (selectedGameIndex + 1).coerceAtMost(filteredGames.size - 1)
                                playClickSound()
                                coroutineScope.launch {
                                    listState.animateScrollToItem(selectedGameIndex)
                                }
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_DPAD_LEFT, android.view.KeyEvent.KEYCODE_SYSTEM_NAVIGATION_LEFT -> {
                            if (filteredGames.isNotEmpty()) {
                                selectedGameIndex = (selectedGameIndex - 1).coerceAtLeast(0)
                                playClickSound()
                                coroutineScope.launch {
                                    listState.animateScrollToItem(selectedGameIndex)
                                }
                            }
                            true
                        }
                        android.view.KeyEvent.KEYCODE_BUTTON_A, android.view.KeyEvent.KEYCODE_ENTER, android.view.KeyEvent.KEYCODE_NUMPAD_ENTER, android.view.KeyEvent.KEYCODE_DPAD_CENTER -> {
                            if (activeGame != null) {
                                stopMusicAndProceed()
                                try {
                                    val launchIntent = context.packageManager.getLaunchIntentForPackage(activeGame.packageName)
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                VideoView(ctx).apply {
                    val uri = Uri.parse("android.resource://${ctx.packageName}/${R.raw.ps3_wave}")
                    setVideoURI(uri)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setOnPreparedListener { mp ->
                        mp.isLooping = true
                        mp.setVolume(0f, 0f)
                        mp.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                        scaleX = 1.45f
                        scaleY = 1.45f
                        start()
                    }
                    start()
                }
            },
            update = { view ->
                if (!view.isPlaying) {
                    view.start()
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            cinematicBlack.copy(alpha = 0.2f),
                            cinematicBlack.copy(alpha = 0.5f),
                            cinematicBlack.copy(alpha = 0.9f),
                            cinematicBlack
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFF0070D1), RoundedCornerShape(4.dp))
                    ) {
                    }
                    Text(
                        text = stringResource(R.string.console_title),
                        color = pureWhiteText.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSearchActive) {
                        Box(
                            modifier = Modifier
                                .height(36.dp)
                                .width(180.dp)
                                .shadow(8.dp, RoundedCornerShape(10.dp), spotColor = Color.Black)
                                .background(ps3GlassCard, RoundedCornerShape(10.dp))
                                .border(1.dp, ps3GlassBorder, RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (searchQuery.isEmpty()) {
                                Text(stringResource(R.string.search_games), color = mutedTextGray, fontSize = 12.sp)
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(color = pureWhiteText, fontSize = 12.sp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    IconButton(
                        onClick = { isSearchActive = !isSearchActive },
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(6.dp, RoundedCornerShape(10.dp), spotColor = Color.Black)
                            .background(ps3GlassCard, RoundedCornerShape(10.dp))
                            .border(1.dp, ps3GlassBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            tint = pureWhiteText,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = { showConfigScreen = true },
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(6.dp, RoundedCornerShape(10.dp), spotColor = Color.Black)
                            .background(ps3GlassCard, RoundedCornerShape(10.dp))
                            .border(1.dp, ps3GlassBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = stringResource(R.string.settings), tint = pureWhiteText, modifier = Modifier.size(16.dp))
                    }

                    Text(
                        text = currentTime,
                        color = pureWhiteText.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    IconButton(
                        onClick = {
                            stopMusicAndProceed()
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(6.dp, RoundedCornerShape(10.dp), spotColor = Color.Black)
                            .background(ps3GlassCard, RoundedCornerShape(10.dp))
                            .border(1.dp, ps3GlassBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = pureWhiteText, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = {
                        if (filteredGames.isNotEmpty()) {
                            selectedGameIndex = (selectedGameIndex - 1).coerceAtLeast(0)
                            playClickSound()
                            coroutineScope.launch {
                                listState.animateScrollToItem(selectedGameIndex)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color.Black)
                        .background(ps3GlassCard, RoundedCornerShape(12.dp))
                        .border(1.dp, ps3GlassBorder, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.previous),
                        tint = pureWhiteText,
                        modifier = Modifier.size(24.dp)
                    )
                }

                AnimatedVisibility(
                    visible = isContentLoaded,
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(700)),
                    exit = fadeOut(animationSpec = tween(400)),
                    modifier = Modifier.weight(1f)
                ) {
                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(95.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(68.dp)
                                    .shadow(10.dp, RoundedCornerShape(18.dp), spotColor = Color.Black)
                                    .clickable { showAppPickerScreen = true },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = ps3GlassCard),
                                border = BorderStroke(1.dp, ps3GlassBorder)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.add_games),
                                        tint = pureWhiteText,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }

                        itemsIndexed(filteredGames) { index, game ->
                            val isSelected = index == selectedGameIndex

                            val cardWidth by animateFloatAsState(
                                targetValue = if (isSelected) 130f else 100f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                                label = "CardWidth"
                            )
                            val cardHeight by animateFloatAsState(
                                targetValue = if (isSelected) 84f else 68f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                                label = "CardHeight"
                            )

                            val iconAlpha by animateFloatAsState(
                                targetValue = if (isSelected) 1f else 0.7f,
                                animationSpec = tween(300),
                                label = "IconAlpha"
                            )

                            Card(
                                modifier = Modifier
                                    .width(cardWidth.dp)
                                    .height(cardHeight.dp)
                                    .shadow(if (isSelected) 16.dp else 4.dp, RoundedCornerShape(20.dp), spotColor = Color.Black)
                                    .clickable {
                                        selectedGameIndex = index
                                        playClickSound()
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(index)
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) ps3GlassCard.copy(alpha = 0.6f) else ps3GlassCard
                                ),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) pureWhiteText else ps3GlassBorder
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(iconAlpha),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (game.appIcon != null) {
                                        val bitmap = remember(game.appIcon) {
                                            try {
                                                game.appIcon.toBitmap(72, 72).asImageBitmap()
                                            } catch (e: Exception) {
                                                null
                                            }
                                        }
                                        if (bitmap != null) {
                                            Image(
                                                bitmap = bitmap,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(if (isSelected) 46.dp else 36.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = {
                        if (filteredGames.isNotEmpty()) {
                            selectedGameIndex = (selectedGameIndex + 1).coerceAtMost(filteredGames.size - 1)
                            playClickSound()
                            coroutineScope.launch {
                                listState.animateScrollToItem(selectedGameIndex)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(6.dp, RoundedCornerShape(12.dp), spotColor = Color.Black)
                        .background(ps3GlassCard, RoundedCornerShape(12.dp))
                        .border(1.dp, ps3GlassBorder, RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.next),
                        tint = pureWhiteText,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (activeGame != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = activeGame.title,
                        color = pureWhiteText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                stopMusicAndProceed()
                                try {
                                    val launchIntent = context.packageManager.getLaunchIntentForPackage(activeGame.packageName)
                                    if (launchIntent != null) {
                                        context.startActivity(launchIntent)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            },
                            modifier = Modifier
                                .height(46.dp)
                                .width(165.dp)
                                .shadow(10.dp, RoundedCornerShape(10.dp), spotColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = pureWhiteText)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = cinematicBlack,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(R.string.play_game),
                                    color = cinematicBlack,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box {
                            IconButton(
                                onClick = { showOptionsMenu = true },
                                modifier = Modifier
                                    .size(46.dp)
                                    .shadow(8.dp, RoundedCornerShape(10.dp), spotColor = Color.Black)
                                    .background(ps3GlassCard, RoundedCornerShape(10.dp))
                                    .border(1.dp, ps3GlassBorder, RoundedCornerShape(10.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = stringResource(R.string.options),
                                    tint = pureWhiteText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showOptionsMenu,
                                onDismissRequest = { showOptionsMenu = false },
                                modifier = Modifier
                                    .background(cinematicBlack)
                                    .border(1.dp, ps3GlassBorder, RoundedCornerShape(10.dp))
                            ) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.remove_from_hub), color = Color(0xFFFF5252), fontSize = 13.sp) },
                                    onClick = {
                                        showOptionsMenu = false
                                        val targetPkg = activeGame.packageName
                                        customGamesList = customGamesList.filter { it.packageName != targetPkg }
                                        saveGamesToPrefs(context, customGamesList.map { it.packageName })
                                        if (selectedGameIndex >= customGamesList.size && customGamesList.isNotEmpty()) {
                                            selectedGameIndex = customGamesList.size - 1
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.no_games_added),
                        color = pureWhiteText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.add_games_hint),
                        color = mutedTextGray,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FullPageAppPickerScreen(
    context: Context,
    existingGames: List<ConsoleAppGame>,
    onDismiss: () -> Unit,
    onGamesSelected: (List<ConsoleAppGame>) -> Unit,
    cinematicBlack: Color,
    ps3GlassCard: Color,
    ps3GlassBorder: Color,
    pureWhiteText: Color,
    mutedTextGray: Color
) {
    var installedApps by remember { mutableStateOf<List<ConsoleAppGame>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var filterText by remember { mutableStateOf("") }

    var selectedPackages by remember {
        mutableStateOf(existingGames.map { it.packageName }.toSet())
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val pm = context.packageManager
                val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                val list = mutableListOf<ConsoleAppGame>()

                for (appInfo in packages) {
                    val name = pm.getApplicationLabel(appInfo).toString()
                    val icon = try { pm.getApplicationIcon(appInfo.packageName) } catch (e: Exception) { null }
                    list.add(ConsoleAppGame(name, appInfo.packageName, icon))
                }

                installedApps = list.sortedBy { hb -> hb.title }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    val filteredList = installedApps.filter { it.title.contains(filterText, ignoreCase = true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(cinematicBlack)
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.select_apps_title),
                        color = pureWhiteText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TextButton(onClick = onDismiss) {
                            Text(text = stringResource(R.string.cancel), color = mutedTextGray, fontSize = 14.sp)
                        }
                        Button(
                            onClick = {
                                val finalList = installedApps.filter { selectedPackages.contains(it.packageName) }
                                onGamesSelected(finalList)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = pureWhiteText),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(38.dp).shadow(6.dp, RoundedCornerShape(10.dp), spotColor = Color.Black)
                        ) {
                            Text(text = stringResource(R.string.done_count, selectedPackages.size), color = cinematicBlack, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .shadow(6.dp, RoundedCornerShape(10.dp), spotColor = Color.Black)
                        .background(ps3GlassCard, RoundedCornerShape(10.dp))
                        .border(1.dp, ps3GlassBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (filterText.isEmpty()) {
                        Text(stringResource(R.string.search_all_apps), color = mutedTextGray, fontSize = 13.sp)
                    }
                    BasicTextField(
                        value = filterText,
                        onValueChange = { filterText = it },
                        singleLine = true,
                        textStyle = TextStyle(color = pureWhiteText, fontSize = 13.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = pureWhiteText)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredList) { app ->
                            val isChecked = selectedPackages.contains(app.packageName)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isChecked) ps3GlassCard else Color.Transparent)
                                    .clickable {
                                        selectedPackages = if (isChecked) {
                                            selectedPackages - app.packageName
                                        } else {
                                            selectedPackages + app.packageName
                                        }
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    if (app.appIcon != null) {
                                        val bmp = remember(app.appIcon) {
                                            try {
                                                app.appIcon.toBitmap(48, 48).asImageBitmap()
                                            } catch (e: Exception) {
                                                null
                                            }
                                        }
                                        if (bmp != null) {
                                            Image(
                                                bitmap = bmp,
                                                contentDescription = null,
                                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp))
                                            )
                                        }
                                    }
                                    Text(
                                        text = app.title,
                                        color = pureWhiteText,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        selectedPackages = if (checked) {
                                            selectedPackages + app.packageName
                                        } else {
                                            selectedPackages - app.packageName
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = pureWhiteText,
                                        uncheckedColor = mutedTextGray,
                                        checkmarkColor = cinematicBlack
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}