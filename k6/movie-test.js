import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    vus: 50,
    duration: '30s',
};

const baseUrl = 'http://localhost:8080';

// 전체 조회 (검색 조건 없음)
const noFilterQuery = 'page=0&size=10000';

// 고정 테스트 영화: "테스트 영화" + ACTION + SHOWING
const title = encodeURIComponent('테스트 영화');
const genreList = ['ACTION'].map(g => `genreList=${g}`).join('&');
const movieStatusList = ['SHOWING'].map(s => `movieStatusList=${s}`).join('&');
const filterQuery = `title=${title}&${genreList}&${movieStatusList}&page=0&size=10000`;

export default function () {
    // 1. 조건 없는 전체 조회
    const noCondV1 = http.get(`${baseUrl}/api/v1/movies?${noFilterQuery}`);
    check(noCondV1, { 'v1 no-condition is 200': res => res.status === 200 });

    const noCondV2 = http.get(`${baseUrl}/api/v2/movies?${noFilterQuery}`);
    check(noCondV2, { 'v2 no-condition is 200': res => res.status === 200 });

    // 2. 고정 영화 검색 조건 포함 조회
    const condV1 = http.get(`${baseUrl}/api/v1/movies?${filterQuery}`);
    check(condV1, { 'v1 with-condition is 200': res => res.status === 200 });

    const condV2 = http.get(`${baseUrl}/api/v2/movies?${filterQuery}`);
    check(condV2, { 'v2 with-condition is 200': res => res.status === 200 });

    sleep(2);
}
