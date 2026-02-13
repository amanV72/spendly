package authservice.serializer;

import authservice.eventProducer.UserInfoEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.kafka.common.serialization.Serializer;
import authservice.Model.UserInfoDto;

import java.util.Map;

public class UserInfoSerializer implements Serializer<UserInfoEvent> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String s, UserInfoEvent userInfoEvent) {
       byte[] retVal=null;
        ObjectMapper objectMapper= new ObjectMapper();
        try{
            retVal=objectMapper.writeValueAsString(userInfoEvent).getBytes();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return retVal;
    }



    @Override
    public void close() {
        Serializer.super.close();
    }
}
