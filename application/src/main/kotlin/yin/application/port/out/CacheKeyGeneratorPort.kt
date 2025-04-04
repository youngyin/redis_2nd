package yin.application.port.out

import yin.application.command.QueryMovieCommand

interface CacheKeyGeneratorPort {
    fun generateMovieKey(command: QueryMovieCommand): String
}