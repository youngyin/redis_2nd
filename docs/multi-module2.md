기존에는 `service-membership`, `service-movie`, `service-reservation` 같은 도메인 단위의 서비스 중심 구조였으나,
더 명확한 책임 분리와 유지보수성을 고려해 계층 중심 + 기능 단위로 모듈을 재설계했다.

## 새로운 프로젝트 구조

```plaintext
platform (루트 프로젝트)
├── bootstrap                    # 애플리케이션 실행 모듈 (모든 모듈 통합 실행)
├── adapter                      # Inbound / Outbound 어댑터 모듈
│   ├── in                       # 외부 요청 진입점 (Controller 등)
│   └── out                      # 외부 시스템 연동 (Persistence, API Client 등)
├── application                  # Application Layer (UseCase, Service, Command, DTO 등)
├── domain                       # 핵심 도메인 로직 (엔티티, 밸류, 도메인 서비스 등)
├── infrastructure               # 인프라 구성 (DB, Redis, Kafka 설정 등)
└── docs                         # 프로젝트 문서 및 ERD
```

---

## 각 모듈 역할

### 1️⃣ `bootstrap`
- 전체 애플리케이션 실행 진입점
- 스프링 부트 메인 클래스 및 설정 포함
- 로컬 통합 테스트와 실행 환경 구성

### 2️⃣ `adapter`
- Inbound 와 Outbound 어댑터 분리
- `in` 폴더에는 RestController, Request DTO 등을 위치시킴
- `out` 폴더에는 Persistence 어댑터 및 외부 API 연동 코드
- 테스트 시 모킹 포인트로 활용

### 3️⃣ `application`
- 비즈니스 유즈케이스의 중심 계층
- 서비스, 유스케이스, 커맨드 및 응답 DTO를 포함
- 도메인 계층과 어댑터 계층을 중재하는 역할

### 4️⃣ `domain`
- 비즈니스 규칙 및 핵심 로직 보관
- 엔티티, 밸류 객체, Enum, 도메인 서비스 등을 위치
- 순수 Java/Kotlin 코드로, 외부 프레임워크에 의존하지 않음

### 5️⃣ `infrastructure`
- DB, Redis, Kafka 등 인프라 관련 설정과 구현 포함
- JPA, MyBatis, KafkaProducer 등의 구현체가 위치