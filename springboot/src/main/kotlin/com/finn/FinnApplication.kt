package com.finn

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FinnApplication

fun main(args: Array<String>) {
    runApplication<FinnApplication>(*args)
}

