package com.chuan.ad.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * SpringBoot 确实为我们做了很多事情， 但有时候我们想要自己定义一些Handler，Interceptor，ViewResolver，MessageConverter，该怎么做呢。
 * 在Spring Boot 1.5版本都是靠重写WebMvcConfigurerAdapter的方法来添加自定义拦截器，消息转换器等。SpringBoot 2.0 后，该类被标记为@Deprecated。因此我们只能靠实现WebMvcConfigurer接口来实现。
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    /**
     * 配置消息转换器
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>>
                                                       converters) {

        converters.clear();
        converters.add(new MappingJackson2HttpMessageConverter());
    }
}
