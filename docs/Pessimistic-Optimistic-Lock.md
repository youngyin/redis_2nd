
## 🔒 분산 락 (Distributed Lock)

| 항목 | 내용 |
|------|------|
| 🔧 구현 위치 | **애플리케이션 외부** (ex. Redis, Zookeeper, DB 등 별도 저장소 이용) |
| 🌍 적용 대상 | **멀티 인스턴스 환경에서 공유 자원 보호** |
| 📦 대표 구현 | Redisson, ZooKeeper, Hazelcast, DB-based Lock 등 |
| 🧠 작동 방식 | 공유 자원을 사용할 때, 중앙 저장소에 락 정보를 등록하여 동시에 접근 못 하도록 막음 |
| 💡 장점 | 인스턴스 간 동기화가 가능 → MSA, 스케일 아웃 환경에서 유용 |
| ⚠️ 단점 | 외부 시스템 의존도 ↑, 네트워크 지연 가능성, TTL, 해제 실패 리스크 |

### 예시
```kotlin
val lock = redissonClient.getLock("seat:1")
if (lock.tryLock(2, 1, TimeUnit.SECONDS)) {
    try {
        // 좌석 예약 처리
    } finally {
        lock.unlock()
    }
}
```

---

## 🔒 JPA 비관적 락 (Pessimistic Lock)

| 항목 | 내용 |
|------|------|
| 🔧 구현 위치 | **DB 레벨**에서 직접 락을 설정 (SELECT FOR UPDATE) |
| 🌍 적용 대상 | **단일 인스턴스 or 트랜잭션 기반 제어가 가능할 때** |
| 🧠 작동 방식 | 데이터를 읽을 때 바로 락을 건다 → 다른 트랜잭션은 접근 못함 |
| 💡 장점 | **충돌 자체를 원천 차단** → 강력한 제어 |
| ⚠️ 단점 | 락 경쟁 많으면 대기 or Deadlock 가능성 ↑, 성능 저하 |

### 예시
```kotlin
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM SeatEntity s WHERE s.id = :id")
fun findByIdWithLock(@Param("id") id: Long): SeatEntity?
```

---

## 🔒 JPA 낙관적 락 (Optimistic Lock)

| 항목 | 내용 |
|------|------|
| 🔧 구현 위치 | **엔티티 버전 필드 (@Version)** 를 통한 충돌 감지 |
| 🌍 적용 대상 | **충돌 가능성 낮은 환경, 읽기 위주 트래픽** |
| 🧠 작동 방식 | 저장 시점에 `version` 값을 비교해서 충돌 발생 시 예외 발생 |
| 💡 장점 | 락 자체는 없음 → **성능 우수** |
| ⚠️ 단점 | **충돌이 실제 발생하면 예외 처리 필요**, 트랜잭션 재시도 전략 필요

### 예시
```kotlin
@Entity
class SeatEntity(
    // ...
    @Version
    var version: Long? = null
)
```

---

## 📊 비교 표

| 항목 | 분산 락 | JPA 비관적 락 | JPA 낙관적 락 |
|------|----------|----------------|----------------|
| 적용 위치 | 애플리케이션 외부 (ex. Redis) | DB 레벨 | 엔티티 필드 |
| 동작 시점 | 락 획득 후 처리 | 조회 시 락 | 저장 시 충돌 감지 |
| 적합한 상황 | 다중 인스턴스/분산 환경 | 강한 동시성 제어 필요 | 충돌 가능성 낮음 |
| 성능 | 중간 | 낮음 (락 대기 존재) | 높음 |
| 실패 시 대처 | 락 획득 실패 → 재시도 | Deadlock 가능 | 예외 발생 → 재시도 필요 |
| 제어 범위 | 전체 시스템 (인스턴스 간) | 현재 DB 세션 | 현재 DB 세션 |

---

## 🎯 정리하면

- **멀티 인스턴스 환경 + 고강도 동시성 제어 필요** → ✅ **분산 락**
- **단일 인스턴스 + 데이터 충돌 우려 큼** → ✅ **비관적 락**
- **충돌 가능성 낮음 + 성능 중요** → ✅ **낙관적 락**
