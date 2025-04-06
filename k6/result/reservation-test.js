import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100, // 가상 사용자 수
    duration: '3s', // 테스트 시간
};

const BASE_URL = 'http://localhost:8081'; // 여러 포트면 나눠서 테스트 가능

// 예약 요청 정보
const scheduleId = 10;
const seatId = 100;

export default function () {
    const userId = Math.floor(Math.random() * 10000); // 중복되지 않게 userId 생성

    const payload = JSON.stringify({
        userId: userId,
        scheduleId: scheduleId,
        seatId: seatId,
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post(`${BASE_URL}/api/reservations`, payload, params);

    check(res, {
        'is status 200 or 409': (r) => r.status === 200 || r.status === 409,
    });

    if (res.status !== 200) {
        console.log(`요청 실패: status=${res.status} - body=${res.body}`);
    }

    sleep(0.1);
}
