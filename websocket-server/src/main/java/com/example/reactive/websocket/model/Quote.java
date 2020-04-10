package com.example.reactive.websocket.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Quote {

    @NotNull
    private String name;

    @NotNull
    private Long value;

}
