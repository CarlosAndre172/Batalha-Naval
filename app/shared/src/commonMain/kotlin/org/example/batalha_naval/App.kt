package org.example.batalha_naval

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.example.batalha_naval.themes.BatalhaNavalTheme
import org.example.batalha_naval.themes.FundoGradiente
import org.example.batalha_naval.screens.*
import org.example.batalha_naval.jogo.TipoMapa

@Composable
fun App( fecharApp: () -> Unit = {} ) {
    BatalhaNavalTheme {
        
        // Aqui tem as rotas (telas) do app.
        val telaInicial = "Menu Inicial"
        val telaIniciarJogo = "Iniciar Jogo"
        val telaTabuleiro = "Tabuleiro"
        val telaRanking = "Ranking"
        
        val telaAtual = remember { mutableStateOf(telaInicial) }

        // Guarda o mar escolhido na tela de início (Poça 5x5, Lagoa 8x8 ou Oceano 10x10).
        val mapaEscolhido = remember { mutableStateOf(TipoMapa.POCA) }

        // Colocamos o fundo animado POR FORA das telas. 
        // Assim, a animação da água não reinicia quando você troca de tela!
        FundoGradiente {
            
            if (telaAtual.value == telaInicial) {
                // Passamos o que a tela deve fazer quando os botões forem clicados
                TelaMenuPrincipal(
                    onNovoJogoClick = { telaAtual.value = telaIniciarJogo },
                    onRankingClick = { telaAtual.value = telaRanking },
                    onSairClick = { fecharApp() } // Fica pronto para o futuro
                )
            } else if (telaAtual.value == telaIniciarJogo) {
                TelaIniciarJogo(
                    // O START manda o mar escolhido junto e já abre o tabuleiro.
                    onIniciarPartida = { tipoMapa ->
                        mapaEscolhido.value = tipoMapa
                        telaAtual.value = telaTabuleiro
                    },
                    onVoltarClick = { telaAtual.value = telaInicial }
                )
            } else if (telaAtual.value == telaTabuleiro) {
                TelaTabuleiro(
                    tipoMapa = mapaEscolhido.value,
                    onVoltarClick = { telaAtual.value = telaIniciarJogo }
                )
            } else if (telaAtual.value == telaRanking) {
                TelaRanking(
                    onVoltarClick = { telaAtual.value = telaInicial }
                )
            }
            //} else if (telaAtual.value == telaRanking) {
            //    telaRanking()
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