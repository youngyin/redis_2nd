package yin.servicemovie

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServiceMovieApplication

fun main(args: Array<String>) {
    runApplication<ServiceMovieApplication>(*args)
}
