package org.example.controller;

import jakarta.websocket.server.PathParam;
import lombok.NonNull;
import org.apache.kafka.common.protocol.types.Field;
import org.example.dto.ExpenseDto;
import org.example.entities.Expense;
import org.example.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@RestController
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    ExpenseController(ExpenseService expenseService){
        this.expenseService=expenseService;
    }

    @GetMapping("expense/v1/getExpenseInBetween")
    public ResponseEntity<List<ExpenseDto>> getExpenses(
            @RequestHeader("user-id") @NonNull String userId,
            @RequestParam("start_date")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam("end_date")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end

            ){
        try{
            Timestamp startDate = Timestamp.from(start);
            Timestamp endDate = Timestamp.from(end);

            List<ExpenseDto> expenseDtos= expenseService.getExpensesInBetween(userId,startDate,endDate);
            return new ResponseEntity<>(expenseDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("expense/v1/create")
    public ResponseEntity<String> createExpense(@RequestHeader("user-id") String userId,@RequestBody ExpenseDto expenseDto){
       // System.out.println("#########Received DTO : "+ expenseDto.toString());
        try{
            expenseDto.setUserId(userId);
            boolean isCreated= expenseService.createExpense(expenseDto);
            if(isCreated){
                return new ResponseEntity<>("Expense created",HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Error Occurred",HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e){
           e.printStackTrace();
            return new ResponseEntity<>("Error Occurred",HttpStatus.NOT_FOUND);

        }

    }


}
