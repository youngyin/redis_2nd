package yin.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import yin.adapter.out.persistence.entity.MovieEntity

interface MovieRepository : JpaRepository<MovieEntity, Long>, MovieCustom {
}