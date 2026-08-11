package org.example.batalha_naval.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import kotlin.math.ceil

// Direções da animação.
enum class DirecaoAnimacao {
    CIMA_PARA_BAIXO,
    BAIXO_PARA_CIMA
}

@Composable
fun NavegadorAnimado(
    telaAlvo: String, 
    corQuadradoA: Color = Color.Black,
    corQuadradoB: Color = Color.Black,
    corQuadradoC: Color = Color.Black,
    direcao: DirecaoAnimacao = DirecaoAnimacao.CIMA_PARA_BAIXO,
    content: @Composable (String) -> Unit
) {
    // A tela que está sendo EXIBIDA agora (ela só muda no meio da animação)
    var telaExibida by remember { mutableStateOf(telaAlvo) }
    
    // O valor do progresso: 
    // 0.0f a 1.0f = Blocos crescendo (cobrindo)
    // 1.0f a 2.0f = Blocos encolhendo (revelando)
    val progressoTransicão = remember { Animatable(2f) }

    LaunchedEffect(telaAlvo) {
        if (telaAlvo != telaExibida) {
            // Fase 1: Anima do 0 ao 1 (A tela escurece de baixo para cima)
            progressoTransicão.snapTo(0f)
            progressoTransicão.animateTo(
                targetValue = 1f, 
                animationSpec = tween(durationMillis = 600, easing = LinearEasing)
            )
            
            // O MOMENTO MÁGICO: A tela fica preta, então nós trocamos 
            // a tela escondida por trás dos blocos!
            telaExibida = telaAlvo
            
            // Fase 2: Anima do 1 ao 2 (A tela revela de baixo para cima)
            progressoTransicão.animateTo(
                targetValue = 2f, 
                animationSpec = tween(durationMillis = 600, easing = LinearEasing)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        
        // 1. CAMADA DO FUNDO: Desenha a interface (Botões, Textos, Tabuleiro)
        content(telaExibida)

        // 2. CAMADA DA FRENTE: Desenha o Canvas preto se estiver no meio de uma transição
        if (progressoTransicão.value < 2f) {
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val valorAtual = progressoTransicão.value
                val cobrindo = valorAtual <= 1f
                
                // Normaliza o progresso de 0 a 1 em ambas as fases
                val progressoFase = if (cobrindo) valorAtual else valorAtual - 1f

                // Tamanho de cada bloquinho na malha (ex: 50 pixels)
                val tamanhoBloco = 60.dp.toPx()
                
                // Quantos blocos cabem na largura e altura da tela
                val colunas = ceil(size.width / tamanhoBloco).toInt()
                val linhas = ceil(size.height / tamanhoBloco).toInt()

                // Reduzimos a duração base para 40% para dar tempo da segunda camada brilhar
                val tempoDegrau = 0.5f 
                val duracaoBase = 0.4f
                
                // A segunda camada começa 15% atrasada e leva os mesmos 40% de tempo
                val atrasoOverlay = 0.15f 
                val duracaoOverlay = 0.4f 
                
                val atrasoPorLinha = if (linhas > 1) tempoDegrau / (linhas - 1) else 0f

                for (linha in 0 until linhas) {
                    
                    val ordemDaLinha = if (direcao == DirecaoAnimacao.BAIXO_PARA_CIMA) {
                        (linhas - 1) - linha 
                    } else {
                        linha 
                    }
                    
                    val inicioDaLinha = ordemDaLinha * atrasoPorLinha
                    
                    // Calcula o quão animada ESTA LINHA original está (de 0 a 1)
                    val progressoLocal = ((progressoFase - inicioDaLinha) / duracaoBase).coerceIn(0f, 1f)
                    val fatorTamanho = if (cobrindo) progressoLocal else (1f - progressoLocal)

                    val corAtual = if (cobrindo) {
                        lerp(corQuadradoA, corQuadradoB, progressoLocal)
                    } else {
                        lerp(corQuadradoB, corQuadradoC, progressoLocal)
                    }

                    if (fatorTamanho > 0f) {
                        val tamanhoAtualPx = tamanhoBloco * fatorTamanho
                        for (coluna in 0 until colunas) {
                            val centroX = (coluna * tamanhoBloco) + (tamanhoBloco / 2f)
                            val centroY = (linha * tamanhoBloco) + (tamanhoBloco / 2f)

                            drawRect(
                                color = lerp(corAtual, Color.Black, 0.4f), // Escurece a cor um pocuo pra ficar massa
                                topLeft = Offset(
                                    x = centroX - (tamanhoAtualPx / 2f), 
                                    y = centroY - (tamanhoAtualPx / 2f)
                                ),
                                size = Size(tamanhoAtualPx * 0.8f, tamanhoAtualPx * 0.8f) 
                            )
                        }
                    }
                }
                
                // Vamos de 0 até 'linhas' e 'colunas' (UM A MAIS) para cobrir todas as quinas da tela perfeitamente!
                for (linha in 0..linhas) {
                    
                    // A ordem máxima tem que ser limitada para não quebrar a sincronia no limite da tela
                    val ordemDaLinha = if (direcao == DirecaoAnimacao.BAIXO_PARA_CIMA) {
                        (linhas - linha).coerceAtMost(linhas - 1)
                    } else {
                        linha.coerceAtMost(linhas - 1)
                    }
                    
                    val inicioDaLinha = ordemDaLinha * atrasoPorLinha
                    
                    // O offset de tempo (atrasoOverlay) para dar tipo um efeito de "onda"
                    val progressoLocalOverlay = ((progressoFase - (inicioDaLinha + atrasoOverlay)) / duracaoOverlay)
                        .coerceIn(0f, 1f)
                        
                    val fatorTamanhoOverlay = if (cobrindo) progressoLocalOverlay else (1f - progressoLocalOverlay)

                    val corAtualOverlay = if (cobrindo) {
                        lerp(corQuadradoA, corQuadradoB, progressoLocalOverlay)
                    } else {
                        lerp(corQuadradoB, corQuadradoC, progressoLocalOverlay)
                    }

                    if (fatorTamanhoOverlay > 0f) {
                        val tamanhoAtualPx = tamanhoBloco * fatorTamanhoOverlay
                        
                        for (coluna in 0..colunas) {
                            // Isso coloca o novo quadrado nas vértices (quinas) da grade original (a grade que é craida por baixo dessa)
                            val centroX = coluna * tamanhoBloco
                            val centroY = linha * tamanhoBloco

                            drawRect(
                                color = corAtualOverlay,
                                topLeft = Offset(
                                    x = centroX - (tamanhoAtualPx / 2f), 
                                    y = centroY - (tamanhoAtualPx / 2f)
                                ),
                                size = Size(tamanhoAtualPx, tamanhoAtualPx)
                            )
                        }
                    }
                }
                
            }
        }
    }
}