package yin.servicemembership.adapter.out.persistence

import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false, unique = true)
    val nickname: String,

    @Column(nullable = false)
    val password: String // 해싱된 비밀번호 저장
)
