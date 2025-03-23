### 조회 시 단일 쿼리로 불가능한 이유

영화 리스트를 조회할 때 영화 정보뿐 아니라 스케줄 정보까지 모두 한 번에 가져오려 하면, 조인으로 인해 데이터가 중복되어 반환되거나, 페이징 쿼리가 깨질 수 있다. 특히 JPA + QueryDSL 환경에서는 fetch join 또는 다중 조인 사용 시 페이징 처리가 어려워진다.

### 쿼리 실행 흐름

#### 1. 영화 총 개수 조회
```sql
select
    count(me1_0.id) 
from
    movies me1_0 
where
    lower(me1_0.title) like ? escape '!'
```

#### 2. 페이징 처리된 영화 목록 조회 (영화 + 이미지 조인)
```sql
select
    me1_0.id,
    me1_0.title,
    me1_0.genre,
    me1_0.rating,
    me1_0.release_date,
    me1_0.running_time,
    me1_0.status,
    ie1_0.url 
from
    movies me1_0 
left join
    images ie1_0 
        on ie1_0.movie_id=me1_0.id 
where
    lower(me1_0.title) like ? escape '!' 
offset
    ? rows 
fetch
    first ? rows only
```

#### 3. 해당 영화 ID 목록으로 스케줄을 한 번에 조회
```sql
select
    se1_0.id,
    m1_0.id as movie_id,
    t1_0.name as theater_name,
    se1_0.start_time,
    m1_0.running_time 
from
    schedules se1_0 
left join
    theaters t1_0 
        on t1_0.id=se1_0.theater_id 
left join
    movies m1_0 
        on m1_0.id=se1_0.movie_id 
where
    m1_0.id in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
```

### 쿼리 횟수를 줄이는 이유
- 불필요한 쿼리 호출은 DB 부하로 이어지고, 대규모 트래픽 상황에서 시스템 응답성을 저하시킨다.
- 네트워크 오버헤드 증가와 커넥션 풀 점유가 높아져 장애 가능성이 증가한다.
- N+1 문제를 방지하고, 한 번의 조회로 필요한 데이터를 묶어 가져오는 패턴을 잘 사용하면 성능을 크게 개선할 수 있다.