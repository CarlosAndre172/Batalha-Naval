package org.example.batalha_naval.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.batalha_naval.BotaoAnimado

import androidx.compose.ui.graphics.Color

// Recebemos as "ações de clique" como parâmetros! 
// A tela não sabe o que acontece quando clica, ela só avisa o App.kt.
@Composable
fun TelaInicial(
    onNovoJogoClick: () -> Unit,
    onOpcoesClick: () -> Unit
) {

    val AzulProfundo = Color(0xFF0B192C)
    val AzulOceano = Color(0xFF1A5276)
    val AzulClaro = Color(0xFFD4E6F1)
    val BrancoTexto = Color(0xFFFFFFFF)
    val MarromMadeira = Color(0xFF8F642D) 
    val Branco = Color(0xFFF5F5DC)

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
                modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight(0.6f),
                color = MarromMadeira,
                shape = RoundedCornerShape(16.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    color = Branco,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center 
                    )   { 
                        Text("🛳️ Batalha Naval", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AzulProfundo)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        )   {
                                BotaoAnimado(texto = "Novo Jogo") { onNovoJogoClick() }
                                BotaoAnimado(texto = "Opções") { onOpcoesClick() }
                                BotaoAnimado(texto = "Sair") { println("Clicou em Sair!") }
                        }
                        Spacer(modifier = Modifier.height(128.dp))
                    }
                }
            }
        }   
    }
}