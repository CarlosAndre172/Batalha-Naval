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
import org.example.batalha_naval.components.BotaoAnimado
import org.example.batalha_naval.components.PainelDeConteudo

import androidx.compose.ui.graphics.Color

// Recebemos as "ações de clique" como parâmetros! 
// A tela não sabe o que acontece quando clica, ela só avisa o App.kt.
@Composable
fun TelaMenuPrincipal(
    onNovoJogoClick: () -> Unit,
    onRankingClick: () -> Unit,
    onSairClick: () -> Unit
) {
    val Vermelho = Color(0xFFFF0000)

    PainelDeConteudo {
        // Aqui dentro vai o conteúdo do painel, que é o título e os botões.
        Text("🛳️ Batalha Naval", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Column(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalAlignment = Alignment.CenterHorizontally
        )   {
                BotaoAnimado(texto = "Novo Jogo") { onNovoJogoClick() }
                BotaoAnimado(texto = "Ranking") { onRankingClick() }
                BotaoAnimado(texto = "Sair", corFundo = Vermelho) { onSairClick() }
        }
        Spacer(modifier = Modifier.height(128.dp))
    }
}


@Composable
fun BotoesEscolherTabuleiro() {

    var tabuleiroSelecionado by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .size(70.dp)
            .border(
                3.dp,
                if (tabuleiroSelecionado == "espada")
                    Color.Yellow
                else
                    Color.Gray
            )
            .clickable {
                tabuleiroSelecionado = "espada"
            },
        contentAlignment = Alignment.Center
    ) {
        /*Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Espada"
        )*/
    }

    Spacer(modifier = Modifier.width(10.dp))

    Box(
        modifier = Modifier
            .size(70.dp)
            .border(
                3.dp,
                if (tabuleiroSelecionado == "escudo")
                    Color.Yellow
                else
                    Color.Gray
            )
            .clickable {
                tabuleiroSelecionado = "escudo"
            },
        contentAlignment = Alignment.Center
    ) {
        /*Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Escudo"
        )*/
    }

    Spacer(modifier = Modifier.width(10.dp))

    Box(
        modifier = Modifier
            .size(70.dp)
            .border(
                3.dp,
                if (tabuleiroSelecionado == "bomba")
                    Color.Yellow
                else
                    Color.Gray
            )
            .clickable {
                tabuleiroSelecionado = "bomba"
            },
        contentAlignment = Alignment.Center
    ) {
        /*Icon(
            imageVector = Icons.Default.Face,
            contentDescription = "Bomba"
        )*/
    }
}