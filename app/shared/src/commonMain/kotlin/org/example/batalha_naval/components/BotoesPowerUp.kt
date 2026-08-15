package org.example.batalha_naval.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import org.example.batalha_naval.jogo.PowerUp
import org.example.batalha_naval.themes.palettes.*

// Os dois botões de power-up que ficam do lado direito do tabuleiro.
// Clicar em um deixa ele "armado": o próximo tiro usa o efeito dele.
@Composable
fun BotoesPowerUp(
    powerUpAtivo: PowerUp?,
    usosRestantes: (PowerUp) -> Int,
    onSelecionar: (PowerUp) -> Unit,
    modifier: Modifier = Modifier
) {
    CaixaLateral(titulo = "POWER", modifier = modifier) {

        PowerUp.entries.forEach { powerUp ->
            val usos = usosRestantes(powerUp)

            BotaoAnimado(
                texto = "${powerUp.nomeExibido} ($usos)",
                corFundo = when {
                    powerUpAtivo == powerUp -> laranjaAcerto // Armado e esperando o tiro.
                    usos > 0 -> azulOceano                   // Disponível.
                    else -> cinza                            // Já foi usado.
                },
                habilitado = usos > 0,
                modifier = Modifier.fillMaxWidth().padding(2.dp)
            ) {
                onSelecionar(powerUp)
            }
        }
    }
}
