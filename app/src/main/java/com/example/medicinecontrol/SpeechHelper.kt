package com.example.medicinecontrol

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.util.Locale

@Composable
fun SpeechToTextButton(onResult: (String) -> Unit) {
    val context = LocalContext.current

    if (!SpeechRecognizer.isRecognitionAvailable(context)) return

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = matches?.firstOrNull()
            if (!text.isNullOrBlank()) {
                onResult(text)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasAudioPermission = granted
        if (granted) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "CL").toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga el nombre del medicamento")
            }
            speechLauncher.launch(intent)
        }
    }

    IconButton(onClick = {
        if (hasAudioPermission) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "CL").toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Diga el nombre del medicamento")
            }
            speechLauncher.launch(intent)
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Dictar nombre por voz",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
