--[[
    KEYS[1] = 요청 카운트 저장용 ZSET 키
    KEYS[2] = 차단 상태 표시용 키
    ARGV[1] = 요청 허용 횟수 (limit)
    ARGV[2] = 현재 시간 (now, 밀리초 단위)
    ARGV[3] = 제한 시간 (expireTime, 밀리초 단위)
]]

local key = KEYS[1]
local blockedKey = KEYS[2]
local limit = tonumber(ARGV[1])
local now = tonumber(ARGV[2])
local expireTime = tonumber(ARGV[3])

-- 요청 기록을 ZSET에 추가 (score와 member 모두 now 사용)
redis.call('ZADD', key, now, tostring(now))

-- 유효 시간 범위를 벗어난 요청 제거
redis.call('ZREMRANGEBYSCORE', key, 0, now - expireTime)

-- 현재 남아있는 요청 수 확인
local count = redis.call('ZCARD', key)

-- 요청 수가 허용치 초과 시 차단 키 설정 및 TTL 지정
if count > limit then
    redis.call('SET', blockedKey, '1')
    redis.call('EXPIRE', blockedKey, math.ceil(expireTime / 1000))
    return 0
end

-- 요청 허용: 카운트 키에 TTL 설정 후 통과
redis.call('EXPIRE', key, math.ceil(expireTime / 1000))
return 1