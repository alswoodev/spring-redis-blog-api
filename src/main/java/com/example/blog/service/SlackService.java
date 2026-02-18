package com.example.blog.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SlackService {
    @Autowired
    Environment env;

    Slack slack = Slack.getInstance();

    public void sendMessage(String message){
        String token = env.getProperty("SLACK_BOT_KEY");
        String channelId = env.getProperty("SLACK_CHANNEL_ID");

        MethodsClient methods = slack.methods(token);
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channelId)
                .text(message)
                .build();
        
        try {
            ChatPostMessageResponse response = methods.chatPostMessage(request);

            if (!response.isOk()) {
                log.error("Slack API error: {}", response.getError());
            }
        } catch (IOException e) {
            log.error("IO error while sending Slack message", e);
        } catch (SlackApiException e) {
            log.error("Slack API exception: {}", e.getMessage(), e);
        }
            }
}
