# Redis_project  

커머스의 핵심 프로세스인 상품 조회 및 주문 과정에서 발생할 수 있는 동시성 이슈 해결 및 성능 개선을 경험하고, 안정성을 높이기 위한 방법을 배웁니다.

## 1주차
- [멀티 모듈 디자인](docs/multi-module.md)
- [테이블 디자인](docs/erd.md)
- [아키텍처 디자인](docs/architecture.md)
- API 구성
  - 요구사항
    ```
    상영 중인 영화 조회 API
    - 메인 페이지를 구성하는 API 입니다.
    - 상영 시간표가 반영되어있습니다.
    - 상영 중인 모든 영화 목록을 반환해야 하며, 페이징을 적용하지 않습니다.
    - 이는 2주차의 성능 측정을 위해 설정 된 조건입니다. 
    ```
  - [주요 API 설계](docs/api.md)

## 2주차
- 1주차 피드백 반영
  - [멀티모듈 디자인 변경](docs/multi-module2.md)
  - baseTimeEntity 추가
  - @Getter 등 data class 에서 필요 없는 어노테이션 제거
  - Movie.runningTimeMin > 분 단위라는 게 드러나도록 컬럼명 변경
  
- [2주차 목표](docs/2nd.md)
  - 기존 조회 API: 영화 목록(제목, 등급, 개봉일, 이미지, 러닝타임, 장르, 상영관 이름, 상영시간표) 반환
  - 추가 요구사항: 제목 검색 + 장르 필터
    - 제목 like 검색,  장르는 List로 받아서 처리
    
  ```kotlin
  @GetMapping
  fun getMovies(
      @Validated request: QueryMovieRequest,
      pageable: Pageable
  ): ResponseEntity<Page<QueryMovieResponse>> {
      val command = QueryMovieCommand(
          title = request.title,
          genreList = request.genreList,
          movieStatusList = request.movieStatusList,
      )
      val findAllMovies = queryMovieUseCase.findAllMovies(command, pageable)
      return ResponseEntity.ok().body(findAllMovies)
  }
  ```
    
  - 쿼리는 QueryDSL로 작성하고, DTO Projection을 사용해서 꼭 필요한 필드만 반환하도록 수정
    - [MovieCustomImpl](adapter/src/main/kotlin/yin/adapter/out/persistence/repository/MovieCustomImpl.kt)
    - [영화 조회](docs/selectMovieApi.md)
    
  - 요청 파라미터 validation (ex. 제목 사이즈, 잘못된 장르 값 등)
    - 제목 사이즈 : 255 이하
    - 잘못된 장르 값 : Enum으로 받아서 잘못된 값인 경우 오류 발생
    - TODO : 예외 발생시 메세지를 핸들러에서 처이해야 함. 
    
  - 캐싱 전략
    - 테스트를 위한 더미 데이터 생성 
      - 스케줄 더미 데이터를 같이 생성 : DummyDataInitializer.kt
      ```
      극장 5개 생성
      영화 1개당 2~3개의 랜덤 스케줄 생성
      각 스케줄은 랜덤한 극장에서 생성
      ```
    - [캐시 설계](docs/cash-design.md)
      - 영화 목록 캐싱 (Movie List Cache)
      - 검색 조건별 영화 목록 캐싱 (Filtered Movie List Cache)
    - [Caffeine + Redis + DB 기반의 3단 캐시 구조 설계 및 구현](docs/test-cache.md)
    - [캐시 테스트](docs/K6-test-result.md)
    - [캐시 동기화 기준](docs/test-cache.md)
  
  ## 3주차
    - [좌석 예약 API 구현](docs/reserve-api.md)
      - [동시성 제어 (중복 예약 방지)](docs/lock-structure.md)
        - 이해하기 : 분산락의 다양한 방법
        - 이해하기 : Redisson 분산 락의 동작 원리
      - [이해하기 : 낙관적 락과 비관적 락, JPA락, Redis락](docs/Pessimistic-Optimistic-Lock.md)
        - 테스트 : 분산 환경에서 JPA 락을 사용한 동시성 제어
        ```shell
        ./gradlew :bootstrap:bootRun --args='--server.port=8081'
        ./gradlew :bootstrap:bootRun --args='--server.port=8082'
        ```
        
      - [invokeAll와 CountDownLatch를 사용한 동시성 제어 테스트](docs/invokeAll_vs_CountDownLatch.md)

 ## 4주차
  - [RateLimit 설계, 구현](docs/RateLimit.md)
  - (리팩토링) AOP로 기술적 관심사와 비즈니스 로직 분리
    ```
    기술 정책은 비즈니스 로직이 아니다.
    예를 들어, "동일 좌석은 동시에 예약되면 안 된다"는 요구는 얼핏 보면 도메인 정책처럼 보이지만, 실제 구현은 분산 락이나 JPA 락 같은 기술적 보호 장치로 해결된다. RateLimit 역시 마찬가지다. 서비스의 핵심 규칙이라기보다, 시스템을 안정적으로 보호하기 위한 기술 정책에 가깝다. 그래서 나는 이런 기술 정책들이 핵심 비즈니스 로직을 침범하지 않도록, 외곽에서 감싸는 구조로 구현하려고 했다.
    그 방법으로 AOP를 적극적으로 활용했다. 기술 정책을 어노테이션으로 선언만 하고, 실제 처리는 Aspect에서 감싸도록 구성했다. 핵심 흐름에서는 로직이 깔끔하게 유지되고, 기술 정책은 별도로 분리되어 관리된다. 처리 로직은 다시 어댑터(Out) 단으로 위임되며, 포트를 통해 Hexagonal 구조를 지키도록 했다.
  
      - `@RateLimited`: 조회 API에 IP 기반의 트래픽 제한을 적용했다. 서버 과부하나 DDoS를 방지하는 전형적인 기술 정책이므로 AOP로 처리했다.
      - 예약 API의 요청 제한은 비즈니스 로직에 가깝다고 판단해, 유즈케이스 흐름 내에서 명시적으로 Redis를 통해 제한을 검사하도록 구성했다. 도메인 규칙으로서 예약 제한은 서비스 로직 안에 있어야 의미가 있었다.
      - `@Cached3Layer`, `@LocalCached`: 캐시 역시 비즈니스 흐름에 침투하지 않도록 어노테이션과 AOP로 처리했다. Caffeine과 Redis를 활용해 3단계 캐시 구조를 구현했다.
      - `@DistributedLock`: Redisson 기반의 분산 락을 AOP로 처리하여 중복 예약을 방지했다.
      - JPA 기반의 로컬 락은 트랜잭션 경계와 밀접하게 맞물리기 때문에 AOP 대신 별도의 서비스로 분리해 명시적으로 처리했다.
     ```
  
  - 전체 API 테스트
