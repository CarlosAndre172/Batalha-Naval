This is a Kotlin Multiplatform project targeting Desktop (JVM), Server.

* [/app/shared](./app/shared/src) is for code that will be shared across your Compose Multiplatform applications. It
  contains several subfolders:
    - [commonMain](./app/shared/src/commonMain/kotlin) is for code that’s common for all targets.
    - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name. For
      example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
      the [iosMain](./app/shared/src/iosMain/kotlin) folder would be the right place for such calls. Similarly, if you
      want to edit the Desktop (JVM) specific part, the [jvmMain](./app/shared/src/jvmMain/kotlin)
      folder is the appropriate location.

* [/core](./core/src) is for the code that will be shared between all targets in the project. The most important
  subfolder is [commonMain](./core/src/commonMain/kotlin). If preferred, you can add code to the platform-specific
  folders here too.

* [/server](./server/src/main/kotlin) is for the Ktor server application. It talks to a local MySQL database
  (`batalha_navalbd`) to save match results and serve the ranking — see the "Database" section below.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and
options:

- Desktop app:
    - Hot reload: `./gradlew :app:desktopApp:hotRun --auto`
    - Standard run: `./gradlew :app:desktopApp:run`

  The desktop app starts the Ktor server **inside its own process** on launch (port 8080), so this is the only
  command you need — no second terminal required. If port 8080 is already taken (e.g. another instance of the game,
  or `:server:run` started manually), the app detects that and simply talks to whatever is already listening there.
- Server (standalone, e.g. for headless/API-only use): `./gradlew :server:run`

### Database

MySQL needs to be running and reachable. The schema (`batalha_navalbd` database + `jogadores`/`partidas` tables) is
created automatically the first time the server starts — you don't need to run
[sql/batalha_naval.sql](./sql/batalha_naval.sql) by hand, though it's kept in sync as documentation of the schema.

Connection defaults to `root` with no password on `localhost`. If your MySQL needs different credentials, set these
environment variables before launching (desktop app or server, either one):

- `BATALHA_NAVAL_DB_URL` (default: `jdbc:mysql://localhost/batalha_navalbd`)
- `BATALHA_NAVAL_DB_USER` (default: `root`)
- `BATALHA_NAVAL_DB_PASSWORD` (default: empty)

If the database is unreachable, the game still runs — you just won't be able to save scores or load the ranking
until the connection is fixed.

### Running tests

Use the run button in your IDE's editor gutter, or run tests using Gradle tasks:

- Desktop tests: `./gradlew :app:shared:jvmTest`
- Server tests: `./gradlew :server:test`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…