package org.example.batalha_naval.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp

import org.example.batalha_naval.themes.palettes.*

@Composable
fun PainelDeConteudo(
    // Recebe um bloco de código Composable como parâmetro.
    largura: Float = 0.6f, // Largura padrão do painel
    altura: Float = 0.6f, // Altura padrão do painel
    paddingExterno: Dp = 8.dp, // Padding externo padrão
    paddingInternoSuperior: Dp = 32.dp, // Padding interno padrão
    paddingInternoInferior: Dp = 32.dp, // Padding interno padrão
    paddingInternoEsquerdo: Dp = 32.dp, // Padding interno padrão
    paddingInternoDireito: Dp = 32.dp, // Padding interno padrão
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.01f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center 
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(largura).fillMaxHeight(altura),
                color = marromMadeira,
                shape = RoundedCornerShape(16.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize().padding(paddingExterno),
                    color = brancoTexto.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(top = paddingInternoSuperior, bottom = paddingInternoInferior, start = paddingInternoEsquerdo, end = paddingInternoDireito),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Desenhamos o código que você passar entre as chaves {}
                        content() 
                    }
                }
            }
        }
    }
}