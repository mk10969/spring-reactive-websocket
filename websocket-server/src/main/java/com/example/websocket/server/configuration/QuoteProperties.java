package com.example.websocket.server.configuration;

import com.example.websocket.server.model.Quote;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class QuoteProperties {

    @Value("classpath:quotes.json")
    private Resource resourceFile;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public List<Quote> quotes(ObjectMapper objectMapper) {
        // AutoClosable
        try (InputStream inputStream = resourceFile.getInputStream()) {

            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });

        } catch (JsonParseException | JsonMappingException e) {
            throw new IllegalArgumentException("Jsonのパースに失敗しました。", e);

        } catch (IOException e) {
            throw new IllegalStateException("ファイルの読み込みに失敗しました。", e);
        }
    }

//    private File readJsonFile() {
//        try {
//            // File Systemとして読み込めないみたい。
//            // https://stackoverflow.com/questions/14876836/file-inside-jar-is-not-visible-for-spring
//            return resourceFile.getFile();
//        } catch (IOException e) {
//            throw new IllegalStateException("ファイルの読み込みに失敗しました。", e);
//        }
//    }

}
