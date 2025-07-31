package com.aibles.okragent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OkrAgentApplication

fun main(args: Array<String>) {
    runApplication<OkrAgentApplication>(*args)
}