# **🎯 시스템 주요 기능 및 API 설계**

## ** 개요**
이 시스템은 멀티모듈 기반으로 동작하며, 각 서비스는 독립적으로 운영되면서 필요한 데이터를 API, 또는 이벤트 큐(kafka)를 통해 주고받는다.

### ** 주요 서비스 구성**
1. **`service-movie`** : 영화 및 상영 일정 관리
2. **`service-reservation`** : 좌석 예약 및 예약 상태 관리
3. **`service-membership`** : 사용자 정보 및 인증 관리

---

## ** 주요 기능 및 API 설계**

## **1️⃣ `service-movie` (영화 및 상영 일정 관리)**
> **영화 정보, 상영관, 좌석 및 일정 관리**

### **📍 API 목록**
| HTTP Method | Endpoint | 설명 |
|------------|---------|------|
| `GET` | `/api/v1/movies` | 전체 영화 목록 조회 |
| `GET` | `/api/v1/movies/{movieId}` | 특정 영화 상세 조회 |
| `GET` | `/api/v1/movies/schedules` | 전체 상영 일정 조회 |
| `GET` | `/api/v1/movies/schedules/{scheduleId}` | 특정 상영 일정 조회 |
| `GET` | `/api/v1/movies/schedules/{scheduleId}/seats` | 특정 일정의 좌석 배치 조회 |
| `GET` | `/api/v1/movies/schedules/{scheduleId}/seats/{seatId}/availability` | 특정 좌석의 예약 가능 여부 조회 |
| `POST` | `/api/v1/movies` | 새 영화 등록 (관리자) |
| `POST` | `/api/v1/movies/schedules` | 새 상영 일정 추가 (관리자) |

### **📍 API 예시 (좌석 예약 가능 여부 조회)**
```http
GET /movies/schedules/s001/seats/A1/availability
```
#### **📍 응답 예시**
```json
{
    "schedule_id": "s001",
    "seat_id": "A1",
    "available": true
}
```

---

## **2️⃣ `service-reservation` (좌석 예약 및 예약 상태 관리)**
> **사용자가 좌석을 예약하고, 예약 상태를 관리하는 서비스**

### **📍 API 목록**
| HTTP Method | Endpoint | 설명 |
|------------|---------|------|
| `POST` | `/api/v1/reservations` | 좌석 예약 요청 (토큰에서 사용자 ID 추출) |
| `GET` | `/api/v1/reservations/{reservationId}` | 특정 예약 상세 조회 (토큰에서 사용자 ID 추출) |
| `GET` | `/api/v1/reservations` | 사용자 예약 내역 조회 (토큰에서 사용자 ID 추출) |
| `POST` | `/api/v1/reservations/cancel/{reservationId}` | 예약 취소 (토큰에서 사용자 ID 추출) |
| `POST` | `/api/v1/reservations/payment/{reservationId}` | 예약 결제 확정 (토큰에서 사용자 ID 추출) |

### **📍 API 예시 (좌석 예약 요청 - 사용자 ID는 토큰에서 추출)**
```http
POST /reservations
Authorization: Bearer {token}
```
#### **📍 요청 예시**
```json
{
    "schedule_id": "s001",
    "seat_id": "A1"
}
```
#### **📍 응답 예시**
```json
{
    "reservation_id": "r001",
    "status": "PENDING"
}
```

---

## **3️⃣ `service-membership` (사용자 정보 및 인증 관리)**
> **사용자 로그인, 회원가입 및 정보 관리 서비스**

### **📍 API 목록**
| HTTP Method | Endpoint               | 설명 |
|------------|------------------------|------|
| `POST` | `/api/v1/users/signup` | 회원가입 |
| `POST` | `/api/v1/users/login`         | 로그인 (JWT 발급) |

### **📍 API 예시 (로그인 요청)**
```http
POST /users/login
```
#### **📍 요청 예시**
```json
{
    "email": "john@example.com",
    "password": "securepassword"
}
```
#### **📍 응답 예시**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5..."
}
```