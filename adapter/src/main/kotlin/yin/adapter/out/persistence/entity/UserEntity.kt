package yin.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "users")
open class UserEntity protected constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var nickname: String,
    var password: String
) : BaseTimeEntity()