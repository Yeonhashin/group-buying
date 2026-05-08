-- KEYS[1] = count key
-- ARGV[1] = quantity

local current = redis.call('INCR', KEYS[1])

if tonumber(current) > tonumber(ARGV[1]) then
    redis.call('DECR', KEYS[1])
    return -1
end

return current