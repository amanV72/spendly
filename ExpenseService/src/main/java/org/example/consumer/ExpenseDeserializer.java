package org.example.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.example.dto.ExpenseDto;

public class ExpenseDeserializer implements Deserializer<ExpenseDto> {


    @Override
    public ExpenseDto deserialize(String topic, byte[] data) {
        ObjectMapper mapper= new ObjectMapper();
        ExpenseDto expense=null;
        try{
            expense= mapper.readValue(data, ExpenseDto.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  expense;
    }
}
