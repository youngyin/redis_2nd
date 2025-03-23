package yin.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = ["yin.adapter", "yin.application", "yin.domain", "yin.infrastructure"])
@EnableJpaRepositories(basePackages = ["yin.adapter.out.persistence.repository"])
@EntityScan(basePackages = ["yin.adapter.out.persistence.entity"])
class BootstrapApplication

fun main(args: Array<String>) {
	runApplication<BootstrapApplication>(*args)
}
