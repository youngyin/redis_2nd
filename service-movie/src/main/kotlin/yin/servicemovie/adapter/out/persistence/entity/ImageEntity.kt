package yin.servicemovie.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "images")
class ImageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    val movie: MovieEntity,

    @Column(nullable = false)
    val url: String
)
