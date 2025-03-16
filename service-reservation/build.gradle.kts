plugins {
	id("org.springframework.boot")
	kotlin("jvm")
	kotlin("plugin.spring")
	kotlin("plugin.jpa")
	kotlin("kapt")
}

dependencies {
	// 상위에서 공통으로 가져오므로 추가 필요 없음
	implementation(project(":common"))
}
