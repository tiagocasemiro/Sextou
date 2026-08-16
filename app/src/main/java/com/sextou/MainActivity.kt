package com.sextou

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sextou.designsystem.theme.SextouTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SextouTheme {
                // O conteúdo das features será inserido dentro do tema compartilhado.
            }
        }
    }
}
