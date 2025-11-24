package com.proshop.sale_service.config;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverters;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
/**
 * Configuration cho Feign Client
 */
@Configuration
@EnableFeignClients(basePackages = "com.proshop.sale_service.client")
public class FeignConfig {
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    public FeignConfig(ObjectFactory<HttpMessageConverters> messageConverters) {
        this.messageConverters = messageConverters;
    }
    @Bean
    public Encoder feignFormEncoder(
            ObjectProvider<HttpMessageConverter<?>> messageConverters
    ) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

}
