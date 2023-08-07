package com.sigma.cinematicketpro.config;

import com.sigma.cinematicketpro.mapper.MovieMapper;
import com.sigma.cinematicketpro.mapper.impl.MovieMapperImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public MovieMapper movieMapper() {
        return new MovieMapperImpl(modelMapper());
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}