import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '20s',
};

const baseUrls = ['http://localhost:8081', 'http://localhost:8082'];
let i = 0;

export default function () {
    const baseUrl = baseUrls[i % baseUrls.length];
    i++;

    const query = 'page=0&size=10000';
    const res = http.get(`${baseUrl}/api/v3/movies?${query}`);

    check(res, {
        [`${baseUrl} 응답 200`]: r => r.status === 200,
    });

    sleep(1);
}
