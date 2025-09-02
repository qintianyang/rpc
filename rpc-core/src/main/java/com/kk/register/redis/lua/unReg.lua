local key = KEYS[1];  -- 从KEYS数组获取第一个参数作为Redis键名
local argsValue = cjson.decode(ARGV[1]);  -- 解码第一个ARGV参数为JSON对象
local redisValue = redis.call('get', key);  -- 从Redis获取键对应的值

if not argsValue then return end  -- 如果参数无效则直接返回
if redisValue then  -- 如果Redis中存在该键的值
    local redisServiceArr = (cjson.decode(redisValue))['value'];  -- 解码Redis值并获取value数组
    for k, v in ipairs(redisServiceArr) do  -- 遍历服务数组
        if v['port'] .. v['domain'] == argsValue['port'] .. argsValue['domain'] then
            table.remove(redisServiceArr, k);  -- 找到匹配的服务并移除
            break;
        end
    end
    redis.call('set', key, cjson.encode({ value = redisServiceArr }));  -- 更新Redis中的值
end