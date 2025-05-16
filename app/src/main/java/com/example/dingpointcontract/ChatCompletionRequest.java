package com.example.dingpointcontract;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.List;

public class ChatCompletionRequest {
    @JSONField(name = "model")
    private String model;
    @JSONField(name = "messages")
    private List<Message> messages;
    @JSONField(name = "stream")
    private boolean stream;

    public ChatCompletionRequest(String model, List<Message> messages, boolean stream) {
        this.model = model;
        this.messages = messages;
        this.stream = stream;
    }

    public static class Message {
        @JSONField(name = "role")
        private String role;
        @JSONField(name = "content")
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}