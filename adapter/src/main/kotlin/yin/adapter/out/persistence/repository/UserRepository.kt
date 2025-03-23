package yin.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import yin.adapter.out.persistence.entity.UserEntity

interface UserRepository : JpaRepository<UserEntity, Long> {
}