package yin.adapter.out

import org.springframework.stereotype.Component
import yin.application.command.QueryMovieCommand
import yin.application.port.out.CacheKeyGeneratorPort
import java.security.MessageDigest

@Component
class MovieCacheKeyGeneratorAdapter : CacheKeyGeneratorPort {

    /**
     * 영화 목록 조회 캐시 키 생성
     * - title, genreList가 모두 비어있을 경우: movies:all
     * - title, genreList가 모두 비어있지 않을 경우: movies:search:{title}:{genreHash}
     */
    override fun generateMovieKey(command: QueryMovieCommand): String {
        return if (command.title.isNullOrBlank() && command.genreList.isEmpty()) {
            "movies:all"
        } else {
            val genreHash = command.genreList
                .sorted()
                .joinToString(",")
                .toByteArray()
                .let { MessageDigest.getInstance("SHA-256").digest(it) }
                .joinToString("") { "%02x".format(it) }

            "movies:search:${command.title}:${genreHash}"
        }
    }
}