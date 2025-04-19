import http from 'k6/http';
import { sleep } from 'k6';

export default function () {
    const url = 'http://localhost:8080/api/v4/reservations';

    const payload = JSON.stringify({
        scheduleId: 304083,
        seatId: 200001
    });

    const headers = {
        headers: {
            'Content-Type': 'application/json',
            'X-USER-ID': '51'
        }
    };

    for (let i = 0; i < 3; i++) {
        const res = http.post(url, payload, headers);
        console.log(`응답 코드: ${res.status}`);
        sleep(5); // 5초 간격으로 요청 (10초 제한에 걸릴 것)
    }
}
