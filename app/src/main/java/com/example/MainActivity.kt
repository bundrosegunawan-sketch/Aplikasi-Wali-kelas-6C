package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainScaffold
import com.example.ui.WaliKelasViewModel
import com.example.ui.theme.WaliKelasTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      WaliKelasTheme {
        val viewModel: WaliKelasViewModel = viewModel()
        Surface(modifier = Modifier.fillMaxSize()) {
          MainScaffold(viewModel = viewModel)
        }
      }
    }
  }
}


