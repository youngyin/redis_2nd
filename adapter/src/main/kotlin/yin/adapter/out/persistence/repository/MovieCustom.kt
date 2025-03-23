package yin.adapter.out.persistence.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import yin.application.command.QueryMovieCommand
import yin.application.dto.QueryMovieResponse

interface MovieCustom {
    fun findAllMovies(command: QueryMovieCommand, pageable: Pageable): Page<QueryMovieResponse>
}