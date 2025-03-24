## 2주차 요구사항 기반 캐싱 설계

### **영화 목록 캐싱 (Movie List Cache)**
- **대상**: 상영 중인 영화 전체 목록 (제목, 등급, 개봉일, 이미지, 러닝타임, 장르, 상태)
- **키**: `movies:all`
- **특징**:
    - 메인 화면 진입 시마다 호출되는 API
    - 페이징 없이 모든 목록 반환 → 트래픽 크기 큼
    - 캐시 적중률이 높을 것으로 예상됨
    - TTL: 짧게 (예: 10분) 설정 후 주기적 갱신
    - 영화 데이터 변경(등록/수정/삭제) 시 인밸리데이션 트리거

### **검색 조건별 영화 목록 캐싱 (Filtered Movie List Cache)**
- **대상**: 제목 검색, 장르 필터가 적용된 결과 목록
- **키**: `movies:search:{title}:{genre_list_hash}`
    - `genre_list_hash`는 리스트 정렬 후 SHA256 해시 적용해서 사용
    - 예) `movies:search:스파이더맨:ae8f9d...`
- **특징**:
    - 동적 파라미터 기반 캐싱
    - 캐시 크기 제한 필요 (LRU 정책 + TTL)
    - TTL: 5분 이하 (짧게 유지)
    - 요청이 많거나 반복되는 검색 키워드에 효과적

### 캐싱 전략
- **Local Cache**
    - Caffeine 사용
    - 애플리케이션 인스턴스 내부에 캐시 보관
    - LRU 기반 최대 사이즈 제한 및 TTL(Time To Live) 설정

- **Distributed Cache**
    - Redisson (Redis) 사용
    - 다중 인스턴스 환경에서 캐시 일관성을 보장
    - Local Cache Miss 시 Redis 조회 → 없으면 DB 조회 후 Local + Redis에 저장

- **DB → Redis → Local Cache 계층화 전략**
    - Local Cache Miss → Redis Cache 조회
    - Redis Cache Miss → DB 조회 후 Local + Redis 에 캐싱
