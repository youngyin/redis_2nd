import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 100,
    duration: '5s',
};

const scheduleId = 304897;
const seatId = 200001;
const theaterId = 137001;
const endpoints = [
    `http://localhost:8082/api/v3/reservations`,
    `http://localhost:8081/api/v3/reservations`,
    `http://localhost:8080/api/v3/reservations`,
];

const userId = 51; // 51번 유저로 예약을 시도합니다. 이 유저는 100명의 VU가 동시에 예약을 시도할 때, 충돌이 발생하는 유저입니다.

const payload = JSON.stringify({
    scheduleId: scheduleId,
    seatId: seatId
});

export default function () {
    const endpoint = endpoints[Math.floor(Math.random() * endpoints.length)];

    const headers = {
        'Content-Type': 'application/json',
        'X-USER-ID': `${userId}`,
    };

    const res = http.post(endpoint, payload, { headers });

    check(res, {
        '응답 코드가 200 또는 409인지': (r) => r.status === 200 || r.status === 409,
    });
}
