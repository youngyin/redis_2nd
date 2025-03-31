import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 50,            // 동시에 실행할 가상 유저 수
    duration: '30s',    // 테스트 시간
};

export default function () {
    const url = "http://localhost:8080/api/v1/movies" +
        "?title=%ED%85%8C%EC%8A%A4%ED%8A%B8" +         // "테스트"
        "&genreList=ACTION" +
        "&movieStatusList=SHOWING" +
        "&page=0&size=10";

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response is not empty': (r) => r.body && r.body.length > 0,
    });
}
