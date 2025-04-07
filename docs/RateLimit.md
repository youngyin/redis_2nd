
## ✅ 1. **조회 API - IP 기반 RateLimit**
### 대상 서비스: `service-movie`
### 적용 이유: 비정상적인 트래픽 유입 방지 (스크래퍼, 봇, DDoS 등)
### 적용 기준: **1분 내 50회 초과 시, 1시간 차단**

### 적용 대상 엔드포인트:

| HTTP Method | Endpoint | 설명 |
|-------------|----------|------|
| `GET` | `/api/v1/movies` | 영화 목록 조회 |

### 적용 전략:
- 요청자의 **IP 기준으로 Redis Key** 관리:  
  예시: `ratelimit:ip:{ip}`
- `Filter` 또는 `Aspect`에서 체크 후, 초과 시 `429 Too Many Requests` 응답

---

## ✅ 2. **예약 API - 유저 기반 RateLimit**
### 대상 서비스: `service-reservation`
### 적용 이유: 유저가 동일 시간대 영화에 대해 무분별하게 예약 시도하는 것 방지

### 적용 기준:
- **같은 유저**가 **같은 시간대 영화**를 **5분에 1번**만 예약 가능

### 적용 대상 엔드포인트:

| HTTP Method | Endpoint | 설명 |
|-------------|----------|------|
| `POST` | `/api/v1/reservations` | 좌석 예약 요청 |

### 적용 전략:
- 사용자 ID + 상영 일정 ID를 기준으로 Redis Key 관리  
  예시: `ratelimit:user:{userId}:schedule:{scheduleId}`
- 예약 시도 시 Lua 스크립트를 통해 TTL 존재 여부 판단
