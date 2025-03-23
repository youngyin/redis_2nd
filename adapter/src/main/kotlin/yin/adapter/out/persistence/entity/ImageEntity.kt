package yin.adapter.out.persistence.entity

import jakarta.persistence.*

@Entity
@Table(name = "images")
open class ImageEntity protected constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    var movie: MovieEntity,
    var url: String
) :BaseTimeEntity()