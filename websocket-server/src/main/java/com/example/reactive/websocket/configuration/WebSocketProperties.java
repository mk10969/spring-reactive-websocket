package com.example.reactive.websocket.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;

@ConfigurationProperties("once.websocket")
@Valid
public class WebSocketProperties {

    /**
     * 配信頻度。
     */
    private Long interval = 1000L;

    /**
     * メッセージをログ出力するかどうか。
     */
    private Boolean messageDebug = false;


    public void setInterval(Long interval) {
        this.interval = interval;
    }


    public Long getInterval() {
        return this.interval;
    }

    public void setMessageDebug(Boolean messageDebug) {
        this.messageDebug = messageDebug;
    }

    public Boolean isMessageDebug() {
        return this.messageDebug;
    }
}
