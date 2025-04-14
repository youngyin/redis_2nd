package yin.adapter.out.persistence.entity

import jakarta.persistence.*

/**
 * For example, on a detail page,
 * multiple images such as still cuts showing various angles or scenes,and teaser posters may be required.
 * Considering this scalability, the images table has been separated.
 */
@Entity
@Table(name = "images")
open class ImageEntity public constructor(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    var movie: MovieEntity,
    var url: String
) :BaseTimeEntity()