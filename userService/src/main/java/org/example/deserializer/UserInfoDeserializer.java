package org.example.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.dto.UserInfoDto;

public class UserInfoDeserializer implements Deserializer<UserInfoDto> {

    @Override
    public UserInfoDto deserialize(String topic, byte[] data) {
        ObjectMapper mapper=new ObjectMapper();
        UserInfoDto user=null;

        try{
            user=mapper.readValue(data, UserInfoDto.class);
        }catch (Exception e){
            System.out.println("can not deserialize");
        }

        return user;
    }

}
