package org.example.batalha_naval.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import org.example.batalha_naval.components.PainelDeConteudo
import org.example.batalha_naval.components.BotaoAnimado
// Importando as cores do seu tema
import org.example.batalha_naval.themes.palettes.*
import org.example.batalha_naval.jogo.TipoMapa
import org.example.batalha_naval.rede.ApiClient

// Uma linha da tábua de líderes, montada a partir do RankingEntryDTO que vem do servidor.
data class JogadorScore(val nome: String, val pontuacao: Int)

@Composable
fun TelaRanking(onVoltarClick: () -> Unit) {

    // Variável de estado para saber qual modo (aba) está selecionado
    var modoSelecionado by remember { mutableStateOf(TipoMapa.POCA) }

    var ranking by remember { mutableStateOf<List<JogadorScore>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(modoSelecionado) {
        carregando = true
        erro = null
        try {
            ranking = ApiClient.buscarRanking(modoSelecionado)
                .map { JogadorScore(nome = it.nomeJogador, pontuacao = it.pontuacao) }
        } catch (e: Exception) {
            erro = "Não foi possível carregar o ranking."
        } finally {
            carregando = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        // Menu de Abas (Poça, Lagoa, Oceano)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TipoMapa.entries.forEach { modo ->
                BotaoAnimado(
                    texto = modo.nomeExibido,
                    // Lógica para mudar a cor se o botão for o selecionado
                    onClick = { modoSelecionado = modo },
                    corFundo = if (modoSelecionado == modo) azulOceano else cinza
                )
            }
        }

        PainelDeConteudo(largura = 2f, altura = 1f) {
            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título da Tela
                Text(
                    text = "🏆 Tábua de Líderes", 
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = azulProfundo
                )
                
                Spacer(modifier = Modifier.height(8.dp))
    
                // Cabeçalho da tabela
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Posição", fontWeight = FontWeight.Bold, color = azulProfundo)
                    Text("Nome", fontWeight = FontWeight.Bold, color = azulProfundo)
                    Text("Pontos", fontWeight = FontWeight.Bold, color = azulProfundo)
                }
                
                HorizontalDivider(color = azulProfundo, thickness = 2.dp)

                if (carregando) {
                    Text(
                        text = "Carregando ranking...",
                        color = azulProfundo,
                        modifier = Modifier.weight(1f).padding(top = 16.dp)
                    )
                } else if (erro != null) {
                    Text(
                        text = erro ?: "",
                        color = Color.Red,
                        modifier = Modifier.weight(1f).padding(top = 16.dp)
                    )
                } else {
                    // LazyColumn é a lista que rola!
                    // O Modifier.weight(1f) é o segredo: ele faz a lista empurrar o botão "Voltar"
                    // para o final da tela e ocupar apenas o espaço que sobrar no meio.
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Ocupa todo o espaço vertical disponível
                            .padding(vertical = 3.dp)
                    ) {
                        // itemsIndexed passa por cada jogador da nossa lista e nos dá o índice (0, 1, 2...)
                        itemsIndexed(ranking) { index, jogador ->
                            // Uma linha para cada jogador
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    // Coloca um fundo levemente azulado nas posições pares para facilitar a leitura
                                    .background(
                                        color = if (index % 2 == 0) azulProfundo.copy(alpha = 0.5f) else Color.Transparent
                                    )
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Posição (colocamos +1 porque o index começa em 0)
                                Text(text = "${index + 1}º", fontWeight = FontWeight.Bold, color = azulProfundo)
                                // Nome
                                Text(text = jogador.nome, color = azulProfundo)
                                // Pontuação
                                Text(text = "${jogador.pontuacao}", fontWeight = FontWeight.Bold, color = azulOceano)
                            }
                        }
                    }
                }
    
                Spacer(modifier = Modifier.height(4.dp))
                
                // Botão para voltar ao menu
                BotaoAnimado(
                    texto = "Voltar", 
                    corFundo = Color(0xFFD32F2F), // Vermelho
                    corTexto = Color.White
                ) {
                    onVoltarClick()
                }
            }
        }
    }
}