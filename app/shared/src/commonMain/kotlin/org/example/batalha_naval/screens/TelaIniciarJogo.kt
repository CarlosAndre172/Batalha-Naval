package org.example.batalha_naval.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import org.example.batalha_naval.components.*
import org.example.batalha_naval.themes.palettes.*
import org.example.batalha_naval.jogo.TipoMapa

import androidx.compose.ui.graphics.Color

// Recebemos as "ações de clique" como parâmetros! 
// A tela não sabe o que acontece quando clica, ela só avisa o App.kt.
@Composable
fun TelaIniciarJogo(
    onIniciarPartida: (TipoMapa, String) -> Unit,
    onVoltarClick: () -> Unit
) {

    var tabuleiroSelecionado by remember { mutableStateOf("Poça") } // Poça é o valor padrão do modo escolhido.

    PainelDeConteudo(largura = 0.6f, altura = 0.9f, paddingInternoInferior = 8.dp) {
        Text(
            "Modo de Jogo", 
            fontSize = 16.sp,
            color = azulProfundo,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))
        
        // Primeira linha de botões
        Row {
            listOf("Poça", "Lagoa", "Oceano").forEach { opcaoTabuleiro ->

                BotaoAnimado(
                    onClick = { tabuleiroSelecionado = opcaoTabuleiro },
                    texto = opcaoTabuleiro,
                    corFundo = if (tabuleiroSelecionado == opcaoTabuleiro) azulOceano else cinza
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Text(
            "Powerup",
            fontSize = 16.sp,
            color = azulProfundo,
            fontWeight = FontWeight.Bold
        )
        // Os dois power-ups já vêm disponíveis em toda partida, não há mais escolha aqui.
        Text(
            "Bomba e Bombardeio disponíveis (1 uso cada)",
            fontSize = 14.sp,
            color = azulProfundo
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Seu Nome", 
            fontSize = 16.sp,
            color = azulProfundo,
            fontWeight = FontWeight.Bold
        )

        var nomePlayer by remember { mutableStateOf("") }
        var erroValidacao by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = nomePlayer,
            label = { Text("Nome") },
            onValueChange = { novoNome -> 
                // Regex: Aceita letras (a-z, A-Z), números (0-9), underlines (_) e caracteres acentuados (À-ÿ).
                // O asterisco (*) diz que pode ser de tamanho zero ou mais.
                // O ^ e o $ garantem que a palavra INTEIRA siga essa regra.
                val padraoPermitido = "^[a-zA-Z0-9_À-ÿ]*$".toRegex()

                // Só atualiza a variável se o texto seguir a regra E tiver no máximo 12 caracteres
                if (novoNome.matches(padraoPermitido) && novoNome.length <= 12) {
                    nomePlayer = novoNome 
                    erroValidacao = false
                } else {
                    erroValidacao = true
                }
                // Se o usuário digitar algum nome inválido, o campo não atualiza e a variável de erro é ativada.
                // Esse isError deixa o campo de texto com a borda vermelha, e o supportingText mostra a mensagem de erro.
            },
                isError = erroValidacao,
                supportingText = {
                if (erroValidacao) {
                    Text("Apenas letras/números (Máx 12 caracteres)", color = Color.Red)
                }
            }
        )

        Button (
            // Converte o texto do botão selecionado ("Poça", "Lagoa"...) no tipo de mar e começa a partida.
            onClick = { onIniciarPartida(TipoMapa.porNome(tabuleiroSelecionado), nomePlayer) },
            enabled = nomePlayer.isNotBlank(),
            modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("START")
            }

        // Botão para voltar ao menu
        BotaoAnimado(
            texto = "Voltar", 
            corFundo = Color(0xFFD32F2F), // Vermelho
            corTexto = Color.White,
            modifier = Modifier.padding(2.dp)
        ) {
            onVoltarClick()
        }

    }
}


