package com.example.reactive.websocket.configuration;

import com.example.reactive.websocket.model.Quote;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Configuration
public class QuoteProperties {

    private static final String path = "classpath:quotes.json";


    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    public List<Quote> quotes() {
        try {
            return objectMapper.readValue(readJsonFile(), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("parse error", e);
        }
    }

    private File readJsonFile() {
        try {
            return resourceLoader.getResource(path).getFile();
        } catch (IOException e) {
            throw new IllegalStateException("ファイルの読み込みに失敗しました。", e);
        }
    }

}
