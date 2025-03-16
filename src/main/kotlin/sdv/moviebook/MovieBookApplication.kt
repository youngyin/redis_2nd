package sdv.moviebook

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MovieBookApplication

fun main(args: Array<String>) {
    runApplication<MovieBookApplication>(*args)
}
