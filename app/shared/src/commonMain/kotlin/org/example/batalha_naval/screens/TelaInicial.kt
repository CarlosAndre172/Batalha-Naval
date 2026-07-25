package org.example.batalha_naval.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
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

    var tabuleiroSelecionado by remember { mutableStateOf("") }
    var powerUpSelecionado by remember { mutableStateOf("") }

    Row {

        // Lado esquerdo
        Column {

            Text(text = "Rank")

            Text(text = "João")
            Text(text = "Maria")
            Text(text = "Pedro")
            Text(text = "Ana")
        }

        Spacer(modifier = Modifier.width(24.dp))

        // Lado direito
        Column {

            // Primeira linha de botões
            Row {

                listOf("Poça", "Lago", "Mar").forEach { opcaoTabuleiro ->

                    Button(
                        onClick = { tabuleiroSelecionado = opcaoTabuleiro },
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                                if (tabuleiroSelecionado == opcaoTabuleiro)
                                    Color.Blue
                                else
                                    Color.Gray
                        )
                    ) {
                        Text(opcaoTabuleiro)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                }

                /*Button(onClick = { }) {
                Text("Poça")
                }

                Button(onClick = { }) {
                    Text("Lago")
                }

                Button(onClick = { }) {
                    Text("Mar")
                }*/
            }

            // Segunda linha de botões
            Row {

                /*Button(
                    onClick = { powerUpSelecionado = opcao },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (powerUpSelecionado == opcao)
                                Color.Blue
                            else
                                Color.Gray
                    )
                ) {
                    Text(opcao)
                }*/
                Spacer(modifier = Modifier.height(8.dp))

                /*
                Button(onClick = { }) {
                    Text("Y")
                }

                Button(onClick = { }) {
                    Text("Z")
                }*/
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = {
                    Text("Nome")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { }) {
                Text("START")
            }
        }
    }
}
/*    Surface(
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
    }*/

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
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Espada"
        )
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
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Escudo"
        )
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
        Icon(
            imageVector = Icons.Default.Face,
            contentDescription = "Bomba"
        )
    }
}