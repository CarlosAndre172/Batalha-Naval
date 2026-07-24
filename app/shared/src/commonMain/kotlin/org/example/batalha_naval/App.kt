package org.example.batalha_naval

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.example.batalha_naval.screens.TelaInicial
import org.example.batalha_naval.screens.TelaTabuleiro
import org.example.batalha_naval.themes.BatalhaNavalTheme
import org.example.batalha_naval.themes.FundoGradiente

@Composable
fun App() {
    BatalhaNavalTheme {
        
        // Aqui tem as rotas (telas) do app.
        val telaInicial = "Menu Inicial"
        val telaTabuleiro = "Tabuleiro"
        val telaOpcoes = "Opções"
        
        val telaAtual = remember { mutableStateOf(telaInicial) }

        // Colocamos o fundo animado POR FORA das telas. 
        // Assim, a animação da água não reinicia quando você troca de tela!
        FundoGradiente {
            
            if (telaAtual.value == telaInicial) {
                // Passamos o que a tela deve fazer quando os botões forem clicados
                TelaInicial(
                    onNovoJogoClick = { telaAtual.value = telaTabuleiro },
                    onOpcoesClick = { telaAtual.value = telaOpcoes } // Fica pronto para o futuro
                )
            } else if (telaAtual.value == telaTabuleiro) {
                TelaTabuleiro(
                    onVoltarClick = { telaAtual.value = telaInicial }
                )
            }
            //} else if (telaAtual.value == telaOpcoes) {
            //    TelaOpcoes()
            //}
            
        }
    }
}

/*
fun TelaTabuleiro() {
    FundoGradiente {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tabuleiro",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
            BotaoAnimado(texto = "Voltar") {
                telaAtual.value = telaInicial
            }
        }
    }
}*/