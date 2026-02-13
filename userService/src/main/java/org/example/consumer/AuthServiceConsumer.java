package org.example.consumer;

import lombok.RequiredArgsConstructor;
import org.example.Services.UserService;
import org.example.dto.UserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceConsumer {

    @Autowired
    private UserService userService;



    @KafkaListener(topics = "${spring.kafka.topic.name}",groupId = "${spring.kafka.consumer.group-id}")
    public void listen(UserInfoDto eventData){
        try{
            userService.createOrUpdateUser(eventData);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
