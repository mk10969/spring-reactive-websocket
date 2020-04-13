package com.example.websocket.server;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;

@ConfigurationProperties("once.websocket")
@Valid
public class WebSocketProperties {

    /**
     * 配信頻度。
     */
    @Getter
    @Setter
    private long interval = 100L;

    /**
     * メッセージをログ出力するかどうか。
     */
    @Getter
    @Setter
    private boolean messageDebug = false;

}
