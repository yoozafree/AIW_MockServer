package com.aiw.backend.infra.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

  @Value("${spring.data.redis.port:6379}")
  private int port;

  @Value("${spring.data.redis.host:localhost}")
  private String host;

  @Value("${spring.data.redis.username:}")
  private String username;

  @Value("${spring.data.redis.password:}")
  private String password;

  @Value("${spring.data.redis.ssl.enabled:false}")
  private boolean sslEnabled;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {

    log.info("Redis host={}, port={}, username={}, sslEnabled={}",
        host, port, username, sslEnabled);

    RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
    conf.setHostName(host);
    conf.setPort(port);

    if (username != null && !username.isBlank()) conf.setUsername(username);
    if (password != null && !password.isBlank()) conf.setPassword(password);

    LettuceClientConfiguration.LettuceClientConfigurationBuilder builder =
        LettuceClientConfiguration.builder();

    if (sslEnabled) builder.useSsl();

    return new LettuceConnectionFactory(conf, builder.build());
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(redisConnectionFactory);

    StringRedisSerializer keySer = new StringRedisSerializer();
    template.setKeySerializer(keySer);
    template.setHashKeySerializer(keySer);

    template.setValueSerializer(RedisSerializer.json());
    template.setHashValueSerializer(RedisSerializer.json());

    template.afterPropertiesSet();
    return template;
  }
}

