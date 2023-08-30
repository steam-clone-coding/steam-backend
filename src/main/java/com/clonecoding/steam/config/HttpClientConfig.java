package com.clonecoding.steam.config;


import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    @Value("${static.timeout}")
    private Integer timeout;

    @Bean
    public RequestConfig requestConfig(){
        return  RequestConfig.custom()
                .setSocketTimeout(timeout)
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .build();
    }

    @Bean
    public CloseableHttpClient httpClient(RequestConfig requestConfig){
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

}
