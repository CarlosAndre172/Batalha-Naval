Este é um projeto Kotlin Multiplatform com foco em Desktop (JVM) e Servidor.

* O diretório /app/shared é para o código que será compartilhado entre as suas aplicações Compose Multiplatform. Ele contém várias subpastas:
  * commonMain é para o código que é comum a todos os alvos (plataformas).

  * As outras pastas são para código Kotlin que será compilado apenas para a plataforma indicada no nome da pasta. Por exemplo, se você quiser usar o CoreCrypto da Apple para a parte iOS do seu app Kotlin, a pasta iosMain seria o lugar certo. Da mesma forma, se você quiser editar a parte específica para Desktop (JVM), a pasta jvmMain é o local apropriado.

* A pasta [/core](./core/src) é para o código que será compartilhado entre todas as partes do projeto. A subpasta mais importante é a [commonMain](./core/src/commonMain/kotlin). Se preferir, você também pode adicionar código às pastas específicas de plataforma aqui.

* A pasta [/server](./server/src/main/kotlin) é destinada à aplicação do servidor Ktor.


### Rodando as aplicações
Use as configurações de execução fornecidas pelo widget de execução na barra de ferramentas da sua IDE. Alternativamente, você pode usar os seguintes comandos e opções no terminal (lembre-se de rodar o servidor em uma aba e o jogo em outra):

* Aplicativo Desktop:

  * Hot reload (Recarregamento em tempo real): ./gradlew :app:desktopApp:hotRun --auto
  * Execução padrão: ./gradlew :app:desktopApp:run

* Servidor: ./gradlew :server:run

### Rodando os testes

Use o botão de execução na margem do editor da sua IDE ou rode os testes usando as tarefas do Gradle no terminal:

* Testes do Desktop: ./gradlew :app:shared:jvmTest
* Testes do Servidor: ./gradlew :server:test

### Banco de Dados
O MySQL precisa estar rodando e acessível. O esquema (banco de dados batalha_navalbd + tabelas jogadores/partidas) é criado automaticamente na primeira vez que o servidor é iniciado — você não precisa executar o arquivo sql/batalha_naval.sql manualmente, embora ele seja mantido atualizado como documentação do esquema.

A conexão padrão utiliza o usuário root sem senha no localhost. Se o seu MySQL exigir credenciais diferentes, configure estas variáveis de ambiente antes de iniciar a aplicação (seja o aplicativo desktop ou o servidor):

  * BATALHA_NAVAL_DB_URL (padrão: jdbc:mysql://localhost/batalha_navalbd)

  * BATALHA_NAVAL_DB_USER (padrão: root)

  * BATALHA_NAVAL_DB_PASSWORD (padrão: vazio)

Se o banco de dados estiver inacessível, o jogo continuará funcionando normalmente — você apenas não conseguirá salvar as pontuações ou carregar o ranking até que a conexão seja reestabelecida.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
