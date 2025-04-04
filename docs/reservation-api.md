### 🎯 과제 목표
> 사용자가 상영 일정(`scheduleId`)과 좌석(`seatId`)을 선택하여 예매를 요청하면, 예약 정보가 저장되고 예약 상태(`PENDING`)로 응답하는 기능을 구현한다.

---

### ✅ 구현 범위 요약 (1차 과제 범위)

#### 1. **예약 생성 API**
- 엔드포인트: `POST /api/v1/reservations`
- 요청 헤더: `X-USER-ID` (현재는 JWT 없이 사용자 ID를 헤더로 전달)
- 요청 바디:
```json
{
  "scheduleId": 302852,
  "seatId": 175001
}
```
- 응답 바디:
```json
{
  "id": 1,
  "userId": 1,
  "scheduleId": 302852,
  "seatId": 175001,
  "status": "PENDING"
}
```

- 구현 포인트:
    - 인증은 생략하되 `X-USER-ID`를 통해 사용자 식별
    - 추후 JWT 도입 시 교체 가능하도록 유연하게 설계

---

#### 2. **예약 내역 조회 API**
- 엔드포인트: `GET /api/v1/reservations/me`
- 요청 헤더: `X-USER-ID`
- 응답 예시:
```json
[
  {
    "id": 1,
    "userId": 1,
    "scheduleId": 302852,
    "seatId": 175001,
    "status": "PENDING"
  }
]
```

- 구현 포인트:
    - 특정 사용자의 전체 예약 내역을 반환
    - 상태별 필터링은 현재 미포함

---

#### 3. **예약 상태 Enum**
- `ReservationStatus`: `PENDING`, `CONFIRMED`, `CANCELED`, `FAILED`
- 예약 생성 시 기본값: `PENDING`

---

### ✅ 유연한 인증 구조 설계

- 현재 JWT 인증은 적용하지 않음
- 대신, `X-USER-ID`를 통해 테스트 가능하도록 처리
- 추후 Spring Security + JWT 도입 시, 헤더에서 사용자 ID 추출 방식만 교체하면 기존 로직 재사용 가능

---

### ✅ 테스트 결과 예시

#### 📌 예약 요청 성공

```bash
curl -X POST http://localhost:8080/api/v1/reservations \
  -H "Content-Type: application/json" \
  -H "X-USER-ID: 1" \
  -d '{
    "scheduleId": 302852,
    "seatId": 175001
  }'
```

📎 응답:
```json
{
  "id": 1,
  "userId": 1,
  "scheduleId": 302852,
  "seatId": 175001,
  "status": "PENDING"
}
```

#### 📌 사용자 예약 목록 조회

```bash
curl -X GET http://localhost:8080/api/v1/reservations/me \
  -H "X-USER-ID: 1"
```

📎 응답:
```json
[
  {
    "id": 1,
    "userId": 1,
    "scheduleId": 302852,
    "seatId": 175001,
    "status": "PENDING"
  }
]
```
