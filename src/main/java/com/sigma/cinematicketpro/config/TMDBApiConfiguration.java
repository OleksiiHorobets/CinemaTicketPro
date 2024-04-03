package com.sigma.cinematicketpro.config;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class TMDBApiConfiguration {

  @Value("${external-api.tmdb.token}")
  private String tmdbToken;

  @Value("${external-api.tmdb.base-url}")
  private String tmdbBaseUrl;

  @Bean
  public RestTemplate tmdbRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
      HttpHeaders headers = request.getHeaders();
      headers.setBearerAuth(tmdbToken);
      headers.setContentType(MediaType.APPLICATION_JSON);
      return execution.execute(request, body);
    };

    return restTemplateBuilder
        .uriTemplateHandler(new DefaultUriBuilderFactory(tmdbBaseUrl))
        .interceptors(Collections.singletonList(interceptor))
        .build();
  }
}