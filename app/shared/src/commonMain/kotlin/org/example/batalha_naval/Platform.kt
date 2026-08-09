package org.example.batalha_naval

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform