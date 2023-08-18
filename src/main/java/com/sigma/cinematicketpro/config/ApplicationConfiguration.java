package com.sigma.cinematicketpro.config;

import com.sigma.cinematicketpro.mapper.MovieMapper;
import com.sigma.cinematicketpro.mapper.impl.MovieMapperImpl;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
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
}