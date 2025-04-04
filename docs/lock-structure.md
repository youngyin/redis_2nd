# 분산락(Distributed Lock) 

## 1. 도입 배경

예약 시스템은 동시 요청이 발생할 수 있는 대표적인 시나리오를 가지고 있다. 예를 들어, 동일한 좌석에 대해 여러 사용자가 동시에 예약을 시도할 경우, 중복 예약이 발생할 수 있다. 이를 방지하기 위해 **동시성 제어**가 필요하며, 단일 서버 환경에서는 `synchronized`, `ReentrantLock`과 같은 JVM 기반 락으로 충분하지만, 다중 서버 환경에서는 **분산 환경에서도 동작 가능한 락 시스템**이 필요하다.

이에 따라 **Redisson 기반의 Redis 분산락**을 도입하였다.

---

## 2. 설계 목표

- 동일 좌석에 대한 중복 예약 방지
- 멀티 인스턴스 환경에서도 일관된 락 보장
- 락 점유 실패 시 예외 처리 가능
- 테스트 가능한 구조 (Testable Architecture)
- 확장 가능한 구조 (e.g. ZooKeeper, etcd 등으로 교체 가능)

---

## 3. 아키텍처 구성

### 3.1 핵심 개념

- **도메인 서비스:** `ReserveSeatService`
- **분산락 포트:** `DistributedLockPort`
- **Redisson 구현체:** `RedissonLockAdapter`

### 3.2 클래스 구성도

```plaintext
ReserveSeatService
  └── uses DistributedLockPort
         └── implemented by RedissonLockAdapter (via RedissonClient)
```

---

## 4. 핵심 클래스 및 역할

### 4.1 DistributedLockPort (Port)

```kotlin
interface DistributedLockPort {
    fun <T> runWithLock(key: String, leaseTime: Long = 3L, block: () -> T): T
}
```

- **Port 역할**을 하는 인터페이스
- 도메인 서비스는 이 인터페이스에 의존
- 분산락 처리 방식을 외부 구현체로 분리하여 **헥사고날 아키텍처** 구조를 유지함

---

### 4.2 RedissonLockAdapter (Adapter)

```kotlin
@Component
class RedissonLockAdapter(
    private val redissonClient: RedissonClient
) : DistributedLockPort {

    override fun <T> runWithLock(key: String, leaseTime: Long, block: () -> T): T {
        val lock = redissonClient.getLock(key)
        if (!lock.tryLock(0, leaseTime, TimeUnit.SECONDS)) {
            throw IllegalStateException("이미 작업 중인 좌석입니다.")
        }

        try {
            return block()
        } finally {
            lock.unlock()
        }
    }
}
```

- **Redisson 기반의 실제 분산락 구현**
- 락 획득 실패 시 예외 발생
- `leaseTime`을 통해 일정 시간 후 자동 해제

---

### 4.3 ReserveSeatService (Application Service)

```kotlin
reserveSeatPort.reserveSeat(
    DistributedLockPort.runWithLock("lock:seat:${command.seatId}") {
        // 예약 존재 확인 및 저장
    }
)
```

- `DistributedLockPort`의 `runWithLock`을 통해 좌석 ID 기준으로 락 획득
- **락을 획득한 요청만 예약 진행**, 그렇지 않으면 예외 발생
- 멀티 인스턴스 환경에서도 **중복 방지 보장**

---

## 5. 락 키 전략

```text
lock:seat:{seatId}
```

- 좌석 단위로 고유한 락 키 생성
- 예약 충돌이 발생할 수 있는 단위(Seat)로 설계함

---

## 6. 테스트 전략

### 6.1 목적

- 동일한 좌석에 대해 여러 사용자가 동시에 예약 시도 시, **하나만 성공**하는지 검증

### 6.2 테스트 방법

- `@SpringBootTest` + `@Transactional` 환경 구성
- 실제 DB + Redisson 설정 기반 테스트
- 10개의 쓰레드가 동시에 예약 시도
- 성공 횟수가 **정확히 1**인지 검증

### 6.3 시나리오 요약

```kotlin
val successCount = AtomicInteger(0)

(1..10).map {
    executor.submit {
        try {
            reserveSeatService.reserve(command)
            successCount.incrementAndGet()
        } catch (e: Exception) { /* ignore */ }
    }
}
```

결과:
- **성공 1건**
- **실패 9건 (동시성 제어 성공)**

---

## 7. 향후 개선 방향

- **락 획득 대기시간 추가 (`tryLock(timeout)`)**
- **락 획득 실패시 재시도 로직 도입 (exponential backoff)**
- **분산락 구현체 교체 가능성 대비 인터페이스 고도화**

---

## 8. 참고 사항

- **Redisson**은 내부적으로 Lua 스크립트를 사용하여 락의 원자성을 보장
- Redis 서버 장애 시 락 해제 불가 문제에 대비하여 `leaseTime` 설정
- `runWithLock` 구조를 통해 락 획득/해제 영역을 명확히 구분
