package com.sigma.cinematicketpro.config;

import com.sigma.cinematicketpro.mapper.MovieMapper;
import com.sigma.cinematicketpro.mapper.impl.MovieMapperImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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