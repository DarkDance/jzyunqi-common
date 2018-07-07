package cn.jzyunqi.common.helper;

import cn.jzyunqi.common.utils.CollectionUtilPlus;
import cn.jzyunqi.common.utils.MapUtilPlus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author wiiyaya
 * @date 2018/5/3.
 */
public class RedisHelper {

    private RedisTemplate<String, Object> redisTemplate;

    /**
     * String（字符串）操作类
     */
    private ValueOperations<String, Object> valueOps;

    /**
     * List（列表）操作类
     */
    private ListOperations<String, Object> listOps;

    /**
     * Set（集合）操作类
     */
    private SetOperations<String, Object> setOps;

    /**
     * SortedSet（有序集合）操作类
     */
    private ZSetOperations<String, Object> zSetOps;

    /**
     * Hash（哈希表）操作类
     */
    private HashOperations<String, String, Object> hashOps;

    public RedisHelper(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOps = redisTemplate.opsForValue();
        this.listOps = redisTemplate.opsForList();
        this.setOps = redisTemplate.opsForSet();
        this.zSetOps = redisTemplate.opsForZSet();
        this.hashOps = redisTemplate.opsForHash();
    }

    /**
     * 从redis获取String类型数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return key对应value
     */
    public Object vGet(Cache cache, String key) {
        if (cache.getType() != Cache.CacheType.V) {
            throw new IllegalArgumentException("expected V found " + cache.getType());
        }
        return valueOps.get(cache.getPrefix().concat(key));
    }

    /**
     * 获取指定缓存名称的String类型数据
     *
     * @param cache 缓存名称
     * @param keys  组成key的变量
     * @return value列表
     */
    public List<Object> vMultiGet(Cache cache, String... keys) {
        if (cache.getType() != Cache.CacheType.V) {
            throw new IllegalArgumentException("expected V found " + cache.getType());
        }
        if (CollectionUtilPlus.isEmptyArray(keys)) {
            return null;
        }
        Set<String> cacheKeySet = new HashSet<>();
        for (String key : keys) {
            cacheKeySet.add(cache.getPrefix().concat(key));
        }
        List<Object> result = valueOps.multiGet(cacheKeySet);
        return result;
    }

    /**
     * 往redis中存入String类型数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param value 要存入的数据
     */
    public void vPut(Cache cache, String key, Object value) {
        if (cache.getType() != Cache.CacheType.V) {
            throw new IllegalArgumentException("expected V found " + cache.getType());
        }
        if (cache.getExpiration() > 0) {
            valueOps.set(cache.getPrefix().concat(key), value, cache.getExpiration(), TimeUnit.SECONDS);
        } else {
            valueOps.set(cache.getPrefix().concat(key), value);
        }
    }

    /**
     * 批量往redis中存入String类型数据
     *
     * @param cache       缓存名称
     * @param keyValueMap 要存入的数据
     */
    public void vPutAll(Cache cache, Map<String, Object> keyValueMap) {
        if (cache.getType() != Cache.CacheType.V) {
            throw new IllegalArgumentException("expected V found " + cache.getType());
        }
        if (MapUtilPlus.isEmpty(keyValueMap)) {
            return;
        }
        for (Map.Entry<String, Object> entry : keyValueMap.entrySet()) {
            vPut(cache, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 增加缓存中储存的数字值
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param value 要增加的数值
     * @return 增加后的数值
     */
    public Long vIncrement(Cache cache, String key, Long value) {
        if (cache.getType() != Cache.CacheType.V) {
            throw new IllegalArgumentException("expected V found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        Long result = valueOps.increment(cacheKey, value == null ? 1L : value);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 减少缓存中储存的数字值
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param value 要减少的数值
     * @return 减少后的数值
     */
    public Long vDecrement(Cache cache, String key, Long value) {
        if (cache.getType() != Cache.CacheType.V) {
            throw new IllegalArgumentException("expected V found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        Long result = valueOps.increment(cacheKey, value == null ? -1L : -value);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 往redis中存入Set类型数据
     *
     * @param cache  缓存名称
     * @param key    组成key的变量
     * @param values 要存入的数据
     * @return 添加成功的数量
     */
    public Long sPut(Cache cache, String key, Object... values) {
        if (cache.getType() != Cache.CacheType.S) {
            throw new IllegalArgumentException("expected S found " + cache.getType());
        }
        if (CollectionUtilPlus.isEmptyArray(values)) {
            return 0L;
        }
        String cacheKey = cache.getPrefix().concat(key);
        Long result = setOps.add(cacheKey, values);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 查询redis的Set类型中是否有指定对象
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return true 存在，false不存在
     */
    public Boolean sExist(Cache cache, String key, Object value) {
        if (cache.getType() != Cache.CacheType.S) {
            throw new IllegalArgumentException("expected S found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        return setOps.isMember(cacheKey, value);
    }

    /**
     * 从redis的Set类型中删除数据
     *
     * @param cache  缓存名称
     * @param key    组成key的变量
     * @param values 要删除的数据
     * @return 删除成功的数量
     */
    public Long sRemove(Cache cache, String key, Object... values) {
        if (cache.getType() != Cache.CacheType.S) {
            throw new IllegalArgumentException("expected S found " + cache.getType());
        }
        if (CollectionUtilPlus.isEmptyArray(values)) {
            return null;
        }
        String cacheKey = cache.getPrefix().concat(key);
        Long result = setOps.remove(cacheKey, values);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 将 member元素从 source 集合移动到destination集合
     *
     * @param sourceCache      源缓存名称
     * @param sourceKey        组成源key的变量
     * @param destinationCache 目标缓存名称
     * @param destinationKey   组成目标key的变量
     * @param value            要移动的数据
     * @return 是否移动成功
     */
    public Boolean sMove(Cache sourceCache, String sourceKey, Cache destinationCache, String destinationKey, Object value) {
        if (sourceCache.getType() != Cache.CacheType.S) {
            throw new IllegalArgumentException("source expected S found " + sourceCache.getType());
        }
        if (destinationCache.getType() != Cache.CacheType.S) {
            throw new IllegalArgumentException("dest expected S found " + destinationCache.getType());
        }
        String sourceCacheKey = sourceCache.getPrefix().concat(sourceKey);
        String destinationCacheKey = destinationCache.getPrefix().concat(destinationKey);
        Boolean result = setOps.move(sourceCacheKey, value, destinationCacheKey);
        if (destinationCache.getExpiration() > 0) {
            redisTemplate.expire(destinationCacheKey, destinationCache.getExpiration(), TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 查询redis的Set类型中值的数量
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return set中值的数量
     */
    public Long sSize(Cache cache, String key) {
        if (cache.getType() != Cache.CacheType.S) {
            throw new IllegalArgumentException("expected S found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        return setOps.size(cacheKey);
    }

    /**
     * 随机获取Set集合类指定的元素集合
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param count 获取数量
     * @return Set集合
     */
    public List<Object> sRandomMembers(Cache cache, String key, Long count) {
        if (cache.getType() != Cache.CacheType.S) {
            throw new IllegalArgumentException("expected S found " + cache.getType());
        }
        if (count == null) {
            return null;
        }
        String cacheKey = cache.getPrefix().concat(key);
        return setOps.randomMembers(cacheKey, count);
    }

    /**
     * 查询redis的Set类型中所有数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return 数据列表
     */
    public Set<Object> sGetAll(Cache cache, String key) {
        if (cache.getType() != Cache.CacheType.S) {
            throw new IllegalArgumentException("expected S found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        return setOps.members(cacheKey);
    }

    /**
     * 无条件分页查询SortedSet中的全部数据
     *
     * @param cache    缓存名称
     * @param key      组成key的变量
     * @param pageable 分页参数
     * @param isAsc    true:升序 false:降序
     * @return 成员列表
     */
    public Set<Object> zGet(Cache cache, String key, Pageable pageable, boolean isAsc) {
        if (cache.getType() != Cache.CacheType.Z) {
            throw new IllegalArgumentException("expected Z found " + cache.getType());
        }
        long start = pageable.getOffset();
        long end = pageable.getOffset() + pageable.getPageSize() - 1;
        if (isAsc) {
            return zSetOps.range(cache.getPrefix().concat(key), start, end);
        } else {
            return zSetOps.reverseRange(cache.getPrefix().concat(key), start, end);
        }
    }

    /**
     * 按照分数条件分页查询SortedSet中的数据
     *
     * @param cache      缓存名称
     * @param key        组成key的变量
     * @param includeMin 是否包含最小值
     * @param minScore   最小值
     * @param includeMax 是否包含最大值
     * @param maxScore   最大值
     * @param pageable   分页参数
     * @param isAsc      true:升序 false:降序
     * @return 成员列表
     */
    public Set<Object> zGetByScore(Cache cache, String key, boolean includeMin, Double minScore, boolean includeMax, Double maxScore, Pageable pageable, boolean isAsc) {
        if (cache.getType() != Cache.CacheType.Z) {
            throw new IllegalArgumentException("expected Z found " + cache.getType());
        }

        RedisZSetCommands.Range range = RedisZSetCommands.Range.range();
        if (minScore != null) {
            if (includeMin) {
                range.gte(minScore);
            } else {
                range.gt(minScore);
            }
        }

        if (maxScore != null) {
            if (includeMax) {
                range.lte(maxScore);
            } else {
                range.lt(maxScore);
            }
        }

        RedisZSetCommands.Limit limit = RedisZSetCommands.Limit.limit();
        limit.offset((int) pageable.getOffset());
        limit.count(pageable.getPageSize());

        RedisSerializer keySerializer = redisTemplate.getKeySerializer();
        byte[] rawKey = keySerializer.serialize(cache.getPrefix().concat(key));
        Set<byte[]> rawValues;
        if (isAsc) {
            rawValues = redisTemplate.execute(new RedisCallback<Set<byte[]>>() {
                public Set<byte[]> doInRedis(RedisConnection connection) {
                    return connection.zRangeByScore(rawKey, range, limit);
                }
            }, true);
        } else {
            rawValues = redisTemplate.execute(new RedisCallback<Set<byte[]>>() {
                public Set<byte[]> doInRedis(RedisConnection connection) {
                    return connection.zRevRangeByScore(rawKey, range, limit);
                }
            }, true);
        }
        RedisSerializer valueSerialize = redisTemplate.getValueSerializer();
        return SerializationUtils.deserialize(rawValues, valueSerialize);
    }

    /**
     * 获取SortedSet中所有数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param isAsc true:升序 false:降序
     * @return 成员列表
     */
    public Set<Object> zGetAll(Cache cache, String key, boolean isAsc) {
        if (cache.getType() != Cache.CacheType.Z) {
            throw new IllegalArgumentException("expected Z found " + cache.getType());
        }
        if (isAsc) {
            return zSetOps.range(cache.getPrefix().concat(key), 0, -1);
        } else {
            return zSetOps.reverseRange(cache.getPrefix().concat(key), 0, -1);
        }
    }

    /**
     * 从SortedSet中获取一条数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param isAsc true:升序 false:降序
     * @return 成员信息
     */
    public Object zGetFirst(Cache cache, String key, boolean isAsc) {
        if (cache.getType() != Cache.CacheType.Z) {
            throw new IllegalArgumentException("expected Z found " + cache.getType());
        }
        Set<Object> result = null;
        if (isAsc) {
            result = zSetOps.range(cache.getPrefix().concat(key), 0, 0);
        } else {
            result = zSetOps.reverseRange(cache.getPrefix().concat(key), 0, 0);
        }
        if (CollectionUtilPlus.isNotEmpty(result)) {
            return result.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * 往SortedSet中存入数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param value 要存入的数据
     * @param score 分值
     * @return true 添加成功， false 添加失败
     */
    public Boolean zPut(Cache cache, String key, Object value, double score) {
        if (cache.getType() != Cache.CacheType.Z) {
            throw new IllegalArgumentException("expected Z found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        Boolean result = zSetOps.add(cacheKey, value, score);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 获取SortedSet中数据数量
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return 有序集合内成员数量
     */
    public Long zSize(Cache cache, String key) {
        String cacheKey = cache.getPrefix().concat(key);
        return zSetOps.zCard(cacheKey);
    }

    /**
     * 根据分值删除SortedSet中数据
     *
     * @param cache    缓存名称
     * @param key      组成key的变量
     * @param minScore 开始分值
     * @param maxScore 结束分值
     */
    public void zRemoveByScore(Cache cache, String key, double minScore, double maxScore) {
        String cacheKey = cache.getPrefix().concat(key);
        zSetOps.removeRangeByScore(cacheKey, minScore, maxScore);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
    }

    /**
     * 根据分值删除SortedSet中数据
     *
     * @param cache  缓存名称
     * @param key    组成key的变量
     * @param values 值
     */
    public void zRemoveByValue(Cache cache, String key, Object... values) {
        String cacheKey = cache.getPrefix().concat(key);
        zSetOps.remove(cacheKey, values);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
    }

    /**
     * 往redis中存入List类型数据 从右边填入
     *
     * @param cache  缓存名称
     * @param key    组成key的变量
     * @param values 要存入的数据
     * @return 添加成功的数量
     */
    public Long lRightPush(Cache cache, String key, Object... values) {
        if (Cache.CacheType.L != cache.getType()) {
            throw new IllegalArgumentException("expected L found " + cache.getType());
        }
        if (CollectionUtilPlus.isEmptyArray(values)) {
            return null;
        }
        String cacheKey = cache.getPrefix().concat(key);
        Long result = listOps.rightPushAll(cacheKey, values);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 往redis中存入List类型数据 从左边填入
     *
     * @param cache  缓存名称
     * @param key    组成key的变量
     * @param values 要存入的数据
     */
    public Long lLeftPush(Cache cache, String key, Object... values) {
        if (Cache.CacheType.L != cache.getType()) {
            throw new IllegalArgumentException("expected L found " + cache.getType());
        }
        if (CollectionUtilPlus.isEmptyArray(values)) {
            return 0L;
        }
        String cacheKey = cache.getPrefix().concat(key);
        Long result = listOps.leftPushAll(cacheKey, values);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 从redis的List类型中删除数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param count 删除个数，可以大于0，小于0，等于0
     * @param value 要删除的数据
     */
    public Long lRemove(Cache cache, String key, long count, Object value) {
        if (Cache.CacheType.L != cache.getType()) {
            throw new IllegalArgumentException("expected L found " + cache.getType());
        }

        String cacheKey = cache.getPrefix().concat(key);
        Long result = listOps.remove(cacheKey, count, value);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
        return result;
    }

    /**
     * 分页查询List中的数据
     *
     * @param cache    缓存名称
     * @param key      组成key的变量
     * @param pageable 分页参数
     * @return 成员列表
     */
    public List<Object> lGet(Cache cache, String key, Pageable pageable) {
        if (cache.getType() != Cache.CacheType.L) {
            throw new IllegalArgumentException("expected L found " + cache.getType());
        }
        long start = pageable.getOffset();
        long end = pageable.getOffset() + pageable.getPageSize() - 1;
        String cacheKey = cache.getPrefix().concat(key);
        List<Object> result = listOps.range(cacheKey, start, end);
        return result;
    }

    /**
     * 查询List中所有数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return 成员列表
     */
    public List<Object> lGetAll(Cache cache, String key) {
        if (cache.getType() != Cache.CacheType.L) {
            throw new IllegalArgumentException("expected L found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        List<Object> result = listOps.range(cacheKey, 0, -1);
        return result;
    }

    /**
     * 从列表左阻塞式弹出对象
     *
     * @param cache   缓存名称
     * @param key     组成key的变量
     * @param timeout 阻塞时间
     * @param unit    阻塞时间单位
     * @return 取出的对象
     */
    public Object lLeftBlockPop(Cache cache, String key, long timeout, TimeUnit unit) {
        if (cache.getType() != Cache.CacheType.L) {
            throw new IllegalArgumentException("expected L found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        return listOps.leftPop(cacheKey, timeout, unit);
    }

    /**
     * 移除并返回列表 key 的头元素
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return 取出的对象
     */
    public Object lLeftPop(Cache cache, String key) {
        String cacheKey = cache.getPrefix().concat(key);
        Object value = listOps.leftPop(cacheKey);
        return value;
    }

    /**
     * 移除list尾部的元素
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     */
    public void lRightPop(Cache cache, String key) {
        String cacheKey = cache.getPrefix().concat(key);
        listOps.rightPop(cacheKey);
    }

    /**
     * 查询List中元素数量
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return 数量
     */
    public Long lSize(Cache cache, String key) {
        if (cache.getType() != Cache.CacheType.L) {
            return null;
        }
        String cacheKey = cache.getPrefix().concat(key);
        Long size = listOps.size(cacheKey);
        return size;
    }

    /**
     * 删除key
     *
     * @param cache 缓存名称
     * @param keys  组成key的变量
     */
    public void removeKey(Cache cache, String... keys) {
        List<String> cacheKeys = new ArrayList<>();
        for (String key : keys) {
            cacheKeys.add(cache.getPrefix().concat(key));
        }
        redisTemplate.delete(cacheKeys);
    }

    /**
     * 删除key
     *
     * @param keys 要删除的key
     */
    public void removeKey(String... keys) {
        redisTemplate.delete(CollectionUtilPlus.asList(keys));
    }

    /**
     * 检查给定key是否存在
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return true 存在， false 不存在
     */
    public Boolean existsKey(Cache cache, String key) {
        String cacheKey = cache.getPrefix().concat(key);
        return redisTemplate.hasKey(cacheKey);
    }

    /**
     * 检查给定key是否存在
     *
     * @param key redis中key
     * @return true 存在， false 不存在
     */
    public Boolean existsKey(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return result;
    }

    /**
     * 从Hash中取值
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param field hash数据类型的键
     * @return hash数据类型的值
     */
    public Object hGet(Cache cache, String key, String field) {
        if (cache.getType() != Cache.CacheType.H) {
            throw new IllegalArgumentException("expected H found " + cache.getType());
        }
        return hashOps.get(cache.getPrefix().concat(key), field);
    }

    /**
     * 往redis中存入Hash类型数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param field hash数据类型的键
     * @param value hash数据类型的值
     */
    public void hPut(Cache cache, String key, String field, Object value) {
        if (cache.getType() != Cache.CacheType.H) {
            throw new IllegalArgumentException("expected H found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        hashOps.put(cacheKey, field, value);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
    }

    /**
     * 往redis中批量存入Hash类型数据
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param map   hash数据类型的键值对
     */
    public void hPutAll(Cache cache, String key, Map<String, Object> map) {
        if (cache.getType() != Cache.CacheType.H) {
            throw new IllegalArgumentException("expected H found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        hashOps.putAll(cacheKey, map);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
    }

    /**
     * 从Hash中取值
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return hash数据类型的值
     */
    public Map<String, Object> hGetAll(Cache cache, String key) {
        if (cache.getType() != Cache.CacheType.H) {
            throw new IllegalArgumentException("expected H found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        return hashOps.entries(cacheKey);
    }

    /**
     * 查询redis的Set类型中是否有指定对象
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @param field hash数据类型的键
     * @return true 存在，false不存在
     */
    public Boolean hExists(Cache cache, String key, String field) {
        if (cache.getType() != Cache.CacheType.H) {
            throw new IllegalArgumentException("expected H found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        return hashOps.hasKey(cacheKey, field);
    }

    /**
     * 删除hash中的键
     *
     * @param cache  缓存名称
     * @param key    组成key的变量
     * @param fields hash数据类型的键
     */
    public void hRemove(Cache cache, String key, String... fields) {
        if (cache.getType() != Cache.CacheType.H) {
            throw new IllegalArgumentException("expected H found " + cache.getType());
        }
        if (CollectionUtilPlus.isEmptyArray(fields)) {
            return;
        }
        String cacheKey = cache.getPrefix().concat(key);
        hashOps.delete(cacheKey, fields);
        if (cache.getExpiration() > 0) {
            redisTemplate.expire(cacheKey, cache.getExpiration(), TimeUnit.SECONDS);
        }
    }

    /**
     * 查询redis的Set类型中值的数量
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return set中值的数量
     */
    public Long hSize(Cache cache, String key) {
        if (cache.getType() != Cache.CacheType.H) {
            throw new IllegalArgumentException("expected H found " + cache.getType());
        }
        String cacheKey = cache.getPrefix().concat(key);
        return hashOps.size(cacheKey);
    }

    /**
     * 获取指定key剩余时间
     *
     * @param cache 缓存名称
     * @param key   组成key的变量
     * @return 剩余时间（秒）
     */
    public Long getRemainSecond(Cache cache, String key) {
        String cacheKey = cache.getPrefix().concat(key);
        return redisTemplate.getExpire(cacheKey);
    }
}
