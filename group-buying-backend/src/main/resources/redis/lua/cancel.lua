-- KEYS[1] = count key

local current = redis.call('GET', KEYS[1])

if not current then
    return 0
end

if tonumber(current) <= 0 then
    return 0
end

return redis.call('DECR', KEYS[1])