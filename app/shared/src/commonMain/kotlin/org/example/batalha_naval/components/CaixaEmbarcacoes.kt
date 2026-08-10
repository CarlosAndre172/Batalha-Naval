package org.example.batalha_naval.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.example.batalha_naval.jogo.Embarcacao
import org.example.batalha_naval.themes.palettes.*

// Caixinha que mostra a sua frota e a frota do inimigo.
// Cada embarcação vira uma fileira de quadradinhos do tamanho dela.
@Composable
fun CaixaEmbarcacoes(
    embarcacoesJogador: List<Embarcacao>,
    afundadasDoJogador: List<Int>,
    embarcacoesInimigo: List<Embarcacao>,
    afundadasDoInimigo: List<Int>,
    modifier: Modifier = Modifier
) {
    CaixaLateral(titulo = "EMBARCAÇÕES", modifier = modifier) {

        Frota(rotulo = "Suas", embarcacoes = embarcacoesJogador, afundadas = afundadasDoJogador)

        Spacer(modifier = Modifier.height(8.dp))

        Frota(rotulo = "Inimigo", embarcacoes = embarcacoesInimigo, afundadas = afundadasDoInimigo)
    }
}

@Composable
private fun Frota(rotulo: String, embarcacoes: List<Embarcacao>, afundadas: List<Int>) {

    Text(
        text = rotulo,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = azulProfundo
    )

    embarcacoes.forEach { embarcacao ->
        val estaAfundada = embarcacao.id in afundadas

        Row(
            modifier = Modifier.padding(vertical = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            repeat(embarcacao.tamanho) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(if (estaAfundada) vermelhoAfundado else azulOceano)
                )
            }
        }
    }
}
