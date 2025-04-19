-- user_schedule_ratelimit.lua
-- KEYS[1]: 제한 대상 키 (예: ratelimit:user:123:schedule:abc)
-- ARGV[1]: 제한 시간 TTL(초) (예: 300)

local exists = redis.call('EXISTS', KEYS[1])
if exists == 1 then
  return 0
end
redis.call('SET', KEYS[1], 1)
redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1]))
return 1
