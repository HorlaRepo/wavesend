package com.shizzy.moneytransfer.config;

import com.shizzy.moneytransfer.util.CacheNames;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${cache.config.entryTtl:60}")
    private int entryTtl;

    @Value("${cache.config.transactions.entryTtl:60}")
    private int transactionsTtl;

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(entryTtl))
                .disableCachingNullValues()
                .serializeValuesWith( RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> {
            var transactionsConf = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(transactionsTtl));
            builder.withCacheConfiguration(CacheNames.TRANSACTIONS, transactionsConf);
            builder.withCacheConfiguration(CacheNames.SINGLE_TRANSACTION, transactionsConf);
            builder.withCacheConfiguration(CacheNames.ALL_USER_TRANSACTION, transactionsConf);
            builder.withCacheConfiguration(CacheNames.SEARCH_RESULT, transactionsConf);
        };
    }
}