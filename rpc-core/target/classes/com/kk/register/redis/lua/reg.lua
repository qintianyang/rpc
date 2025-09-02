local key = KEYS[1];
local argsValue = cjson.decode(ARGV[1]);
local redisValue = redis.call('get', key);

if not argsValue then return end
if redisValue then
    local redisServiceArr = (cjson.decode(redisValue))['value'];
    for k, v in pairs(redisServiceArr) do
        if v['port'] .. v['domain'] == argsValue['port'] .. argsValue['domain'] then
            return
        end
    end
    table.insert(redisServiceArr, argsValue);
    redis.call('set', key, cjson.encode({ value = redisServiceArr }));
else
    redis.call('set', key, cjson.encode({ value = { argsValue } }));
end