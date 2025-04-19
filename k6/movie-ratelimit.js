import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 1,
    duration: '1m',
};

export default function () {
    const res = http.get('http://localhost:8080/api/v4/movies');
    console.log(res.status);
    sleep(2); // 2초마다 요청 → 총 30회 시도 가능
}
