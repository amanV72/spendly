package org.example.consumer;

import lombok.RequiredArgsConstructor;
import org.example.dto.ExpenseDto;
import org.example.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ExpenseConsumer {

    @Autowired
    private ExpenseService expenseService;



    @KafkaListener(topics = "${spring.kafka.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ExpenseDto expenseDto){
        try{
            expenseService.createExpense(expenseDto);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
