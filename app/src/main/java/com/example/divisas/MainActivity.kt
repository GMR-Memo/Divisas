package com.example.divisas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.divisas.ui.theme.DivisasTheme
import com.example.divisas.worker.ExchangeRateWorker

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Encolar una tarea única para sincronizar las tasas de cambio
        val workRequest = OneTimeWorkRequestBuilder<ExchangeRateWorker>().build()
        WorkManager.getInstance(this).enqueue(workRequest)

        setContent {
            DivisasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Sincronización con exito",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "¡Hola, $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DivisasTheme {
        Greeting("Android")
    }
}
