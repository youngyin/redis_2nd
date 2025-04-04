## 캐시 동기화 기준: 왜 "5번 이상" 조회 시 Redis에 저장하도록 했는가?

### ✅ 목적
Redis는 중앙 캐시로 모든 인스턴스가 공유하며, **자원이 제한된 인프라에서 메모리 사용량이 곧 비용**이 된다. 모든 데이터를 Redis에 넣는 방식은 비효율적이며, 실제 서비스에서는 "자주 조회되는 인기 데이터"만 Redis에 남도록 설계하는 것이 바람직하다.

---

## 🚩 기준: 5번 이상 조회된 데이터만 Redis에 저장

### 이유 1. **Redis는 인기 데이터 전용**
- Redis는 Caffeine과 달리 네트워크 IO 비용이 발생하고, 모든 인스턴스가 접근하므로 불필요하게 많은 데이터를 저장하면 병목이 발생할 수 있다.
- 반면, Caffeine은 각 서버에 독립적으로 존재하므로 부담 없이 저장할 수 있다.
- 따라서 "많이 호출된 데이터"만 Redis로 승격시키는 전략이 적합하다.

### 이유 2. **트래픽 적응형 설계**
- 캐시 구조는 트래픽에 민감하게 반응해야 한다.
- 1~2번 요청된 데이터는 일시적인 사용자 요청일 수 있지만, 5회 이상 요청되었다면 일정 수준 이상의 수요가 있다고 판단할 수 있다.
- 이 수치는 실 서비스에서 조정 가능하며, 운영 중 모니터링을 통해 `threshold`를 10 이상으로 높이는 것도 고려할 수 있다.

---

## ⚙️ 구현 방식: Redis에 hit count 저장

### 구조 요약:
- Redis의 별도 공간(e.g., `movie:hitcount:{key}`)에 각 캐시 키별 hit count를 저장
- 각 요청마다 Caffeine/Redis hit 여부와 관계없이 count를 증가
- DB에서 데이터를 조회한 후, `count >= 5`인 경우에만 Redis에 동기화

```kotlin
// 호출 시 hit count 증가
cacheHitCounterPort.increment(cacheKey)

// 캐시 저장 전 Redis 저장 여부 판단
val hitCount = cacheHitCounterPort.getHitCount(cacheKey)
if (redisSyncPolicy.shouldSync(cacheKey, hitCount.toInt())) {
    redisRepositoryPort.put(cacheKey, responseList)
}
```

### 이점:
- 로컬 메모리를 사용하지 않으므로 서버 재시작에도 hit count가 유지됨
- 인스턴스 수가 많아져도 count가 통합되어 보다 정확한 인기 판단 가능
- Redis 내 메모리 사용을 제한하면서 효과적인 캐싱 전략 구현 가능

---

## ✨ 결론

단순히 캐시 적중률을 높이는 것에서 나아가, **효율적인 메모리 사용과 인프라 부담 완화**를 동시에 달성하기 위해 “5회 이상” 조회된 데이터만 Redis에 저장하도록 하였다.  
이 기준은 Redis를 “프리미엄 캐시 계층”으로 사용하여 고성능과 확장성을 동시에 달성하기 위한 전략적 선택이다.

운영 환경에 따라 이 기준은 `동적으로 조정 가능`하며, 장기적으로는 **Hot Key 분석 및 자동 조정 정책**으로 진화시킬 수 있다.
