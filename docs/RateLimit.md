# RateLimit 구현 설계 및 결과

## 1. 목표

- **서버 안정성과 악의적 요청 방지**를 위해 RateLimit 기능을 구현한다.
- 요청의 성격에 따라 **기술적인 RateLimit**과 **비즈니스 정책 RateLimit**을 구분하여 설계하고 구현한다.

---

## 2. RateLimit 구분 및 설계 방향

### ✅ 조회 API (`GET /api/v4/movies`)
- **목적**: 서버에 과도한 트래픽이 몰리는 것을 방지
- **정책**: 1분 내 50회 초과 요청 시 → 1시간 동안 해당 IP 차단
- **관점**: **기술적 관심사 (Cross-cutting Concern)**
- **적용 방식**:
  - `@RateLimited` 어노테이션 생성
  - `RateLimitAspect` AOP를 통해 요청 진입 시점에 제한 검사
  - Redis에 Lua 스크립트를 이용해 제한 상태 저장 및 체크

### ✅ 예약 API (`POST /api/v2/reservations`)
- **목적**: 동일 유저가 동일 시간대 영화에 반복 예약하는 것을 방지
- **정책**: 같은 유저는 같은 시간대 영화에 대해 **5분(300초)에 1회**만 예약 가능
- **관점**: **비즈니스 도메인 정책**
- **적용 방식**:
  - `DsRateLimitReserveSeatUseCase` 유즈케이스 흐름 내에서 제한 검사 로직 명시적 호출
  - Redis Lua 스크립트에서 TTL을 ARGV로 받아 처리 (동적 TTL 허용)
  - 예약 로직이 정상 수행되기 직전에 제한 체크

---

## 3. Redis Lua 스크립트 사용

- Lua 스크립트를 통해 Redis에 원자적으로 제어 (SET + EXPIRE)
- 조회 제한 스크립트, 예약 제한 스크립트 별도 구성
- TTL은 하드코딩 대신 ARGV를 통해 외부에서 주입하도록 구성

---

## 4. 클래스 및 계층 구조 요약

| 계층 | 파일 | 역할 |
|------|------|------|
| `adapter.in.aop` | `RateLimitAspect`, `@RateLimited` | 조회 요청 제한 AOP 처리 |
| `application.port.in` | `DsRateLimitReserveSeatUseCase` | 예약 흐름 유즈케이스 인터페이스 |
| `application.service` | `DsRateLimitReserveSeatService` | 예약 흐름 구현체, 비즈니스 정책 포함 |
| `application.port.out` | `RateLimitPort` | Redis와 연결되는 포트 인터페이스 |
| `adapter.out.redis` | `RedisRateLimitAdapter` | 실제 Redis + Lua로 제한 체크 수행 |
