package yin.servicemovie.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import yin.servicemovie.adapter.out.persistence.entity.MovieEntity

interface MovieRepository : JpaRepository<MovieEntity, Long>, MovieCustom {
}