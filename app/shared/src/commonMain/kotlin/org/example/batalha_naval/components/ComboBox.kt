package org.example.batalha_naval.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    opcoes: List<String>,              // A lista de palavras que vão ser as opções da ComboBox.
    opcaoSelecionada: String,          // Qual opção (palavra) está escolhida agora.
    onOpcaoSelecionada: (String) -> Unit, // O que fazer quando o usuário escolher uma opção nova.
    label: String = "Selecione",       // O título pequeno que fica em cima da caixa.
    modifier: Modifier = Modifier.fillMaxWidth(), // Permite customizar o tamanho da ComboBox.
    icone: @Composable (() -> Unit)? = null // <-- NOVO: Permite passar uma imagem customizada!
) {
    // Essa variável controla se a listinha de opções está visível (true) ou escondida (false)
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it }, // Quando o usuário clica, ele inverte o estado
        modifier = modifier
    ) {
        // Esta é a caixa de texto que o usuário vê o tempo todo
        TextField(
            value = opcaoSelecionada,
            onValueChange = {}, // Como é readOnly (somente leitura), não fazemos nada aqui
            readOnly = true,
            label = { Text(label) },
            
            // Colocamos a imagem/ícone do lado esquerdo (se você passar alguma)
            leadingIcon = icone,
            
            // O ícone de setinha pra baixo que gira automaticamente quando abre!
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            
            // O menuAnchor() diz ao Compose que essa caixa de texto é a "âncora" do menu flutuante.
            // ATUALIZADO: Avisamos que é o campo principal e não permite digitação, isso evita uma warning do compilador.
            modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        
        // Este é o menu flutuante que aparece quando clica
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false } // Quando clica fora, fecha o menu
        ) {
            
            // Aqui nós passamos por cada palavra da sua lista e criamos um item clicável para ela
            opcoes.forEach { selecao ->
                DropdownMenuItem(
                    text = { Text(selecao) },
                    onClick = {
                        // Avisamos à tela que uma nova opção foi escolhida!
                        onOpcaoSelecionada(selecao) 
                        // Fechamos o menu após a escolha
                        expandido = false 
                    }
                )
            }
        }
    }
}