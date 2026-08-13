package org.example.batalha_naval

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.example.batalha_naval.themes.BatalhaNavalTheme
import org.example.batalha_naval.themes.FundoGradiente
import org.example.batalha_naval.screens.*
import org.example.batalha_naval.themes.palettes.*
import org.example.batalha_naval.jogo.TipoMapa

import org.example.batalha_naval.components.NavegadorAnimado
import org.example.batalha_naval.components.DirecaoAnimacao

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

        // Nome digitado na tela de início, usado pra salvar o score no servidor.
        val nomeJogador = remember { mutableStateOf("") }

        // Dita a direção do efeito de transição entre telas (de cima para baixo ou de baixo para cima).
        var direcaoAnimacao by remember { mutableStateOf(DirecaoAnimacao.BAIXO_PARA_CIMA) }

        FundoGradiente {
            
            NavegadorAnimado(
                telaAlvo = telaAtual.value,
                corQuadradoA = azulClaro,
                corQuadradoB = roxoClaro,
                corQuadradoC = azulOceano,
                
                direcao = direcaoAnimacao 
            ) { telaAtiva ->
                
                if (telaAtiva == telaInicial) {
                    TelaMenuPrincipal(
                        onNovoJogoClick = { 
                            direcaoAnimacao = DirecaoAnimacao.CIMA_PARA_BAIXO
                            telaAtual.value = telaIniciarJogo 
                        },
                        onRankingClick = { 
                            direcaoAnimacao = DirecaoAnimacao.BAIXO_PARA_CIMA
                            telaAtual.value = telaRanking 
                        },
                        onSairClick = { fecharApp() }
                    )
                } else if (telaAtiva == telaIniciarJogo) {
                    TelaIniciarJogo(
                        onIniciarPartida = { tipoMapa, nome ->
                            mapaEscolhido.value = tipoMapa
                            nomeJogador.value = nome
                            direcaoAnimacao = DirecaoAnimacao.BAIXO_PARA_CIMA
                            telaAtual.value = telaTabuleiro
                        },
                        onVoltarClick = {
                            direcaoAnimacao = DirecaoAnimacao.BAIXO_PARA_CIMA
                            telaAtual.value = telaInicial
                        }
                    )
                } else if (telaAtiva == telaTabuleiro) {
                    TelaTabuleiro(
                        tipoMapa = mapaEscolhido.value,
                        nomeJogador = nomeJogador.value,
                        onVoltarClick = { telaAtual.value = telaIniciarJogo }
                    )
                } else if (telaAtiva == telaRanking) {
                    TelaRanking(
                        onVoltarClick = { telaAtual.value = telaInicial }
                    )
                }
            }
        }
    }
}
