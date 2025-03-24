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

