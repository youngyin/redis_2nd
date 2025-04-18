package yin.adapter.`in`.controller

import org.springframework.web.bind.annotation.*
import yin.application.port.`in`.UserUseCase
import yin.domain.User

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userUserCase: UserUseCase
) {
    /**
     * Handles the retrieval of a user by their ID.
     */
    @GetMapping("/me")
    fun getUser(@RequestHeader("X-USER-ID") userId: Long): User {
        return userUserCase.findById(userId)
    }

    /**
     * Handles the registration of a new user.
     */
    @PostMapping
    fun registerUser(
        @RequestBody user: User
    ): User {
        return userUserCase.register(user)
    }
}