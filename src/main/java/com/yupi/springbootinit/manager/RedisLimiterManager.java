package com.yupi.springbootinit.manager;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
/**
 * @author : wangshanjie
 * @date : 15:45 2023/8/7
 *  * 专门提供 RedisLimiter 限流基础服务的（提供了通用的能力）
 */

@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;


    /**

     * 限流操作

     *

     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计

     */

    public void doRateLimit(String key) {

        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次

        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);

        //设置trySetRate限流器的统计规则
        // OVERALL 无论是多少服务器，放在一起统计
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);

        // 每当一个操作来了后，请求一个令牌，每个操作请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);

        if (!canOp) {

            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);

        }

    }

}