import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 50,               // 동시 사용자 수
    duration: '30s',       // 테스트 시간
};

const baseUrl = 'http://localhost:8080';

// 공통 쿼리 파라미터
const noFilterQuery = 'page=0&size=10000';

const title = encodeURIComponent('테스트 영화');
const genreList = ['ACTION'].map(g => `genreList=${g}`).join('&');
const movieStatusList = ['SHOWING'].map(s => `movieStatusList=${s}`).join('&');
const filterQuery = `title=${title}&${genreList}&${movieStatusList}&page=0&size=10000`;

export default function () {
    // 1. 전체 목록 조회 (no condition)
    const v1_noCond = http.get(`${baseUrl}/api/v1/movies?${noFilterQuery}`);
    check(v1_noCond, { 'v1 전체 조회 200': res => res.status === 200 });

    const v2_noCond = http.get(`${baseUrl}/api/v2/movies?${noFilterQuery}`);
    check(v2_noCond, { 'v2 전체 조회 200': res => res.status === 200 });

    // 2. 필터 적용 검색
    const v1_filter = http.get(`${baseUrl}/api/v1/movies?${filterQuery}`);
    check(v1_filter, { 'v1 조건 검색 200': res => res.status === 200 });

    const v2_filter = http.get(`${baseUrl}/api/v2/movies?${filterQuery}`);
    check(v2_filter, { 'v2 조건 검색 200': res => res.status === 200 });

    sleep(2);
}
