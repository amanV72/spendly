package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.util.Strings;
import org.example.dto.ExpenseDto;
import org.example.entities.Expense;
import org.example.repo.ExpenseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ExpenseService {

    private ExpenseRepo expenseRepo;
    private ObjectMapper objectMapper;

    @Autowired
    ExpenseService(ExpenseRepo expenseRepo){

        this.expenseRepo=expenseRepo;
        this.objectMapper=new ObjectMapper();
    }

    public boolean createExpense(ExpenseDto expenseDto){
        setCurrency(expenseDto);
        try{
            expenseRepo.save(objectMapper.convertValue(expenseDto, Expense.class));
            return true;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateExpense(ExpenseDto expenseDto){
        Optional<Expense> expenseFound = expenseRepo.findByUserIdAndExternalId(expenseDto.getUserId(),expenseDto.getExternalId());

        if(expenseFound.isEmpty()){
            return false;
        }
        Expense expense=expenseFound.get();
        expense.setCurrency(Strings.isNotBlank(expenseDto.getCurrency())? expenseDto.getCurrency() : expense.getCurrency());
        expense.setMerchant(Strings.isNotBlank(expenseDto.getMerchant())? expenseDto.getMerchant() : expense.getMerchant());
        expense.setAmount(expenseDto.getAmount());


        expenseRepo.save(expense);
        return true;
    }

    public List<ExpenseDto> getExpenses(String userId){
        List<Expense> expenses= expenseRepo.findByUserId(userId);

        return objectMapper.convertValue(expenses, new TypeReference<List<ExpenseDto>>() {});
    }

    public List<ExpenseDto> getExpensesInBetween(String userId,Timestamp startDate,Timestamp endDate){
        List<Expense> expenses= expenseRepo.findByUserIdAndCreatedAtBetween(userId,startDate,endDate);

        return objectMapper.convertValue(expenses, new TypeReference<List<ExpenseDto>>() {});
    }

    private void setCurrency(ExpenseDto expenseDto){
        if(Objects.isNull(expenseDto.getCurrency())){
            expenseDto.setCurrency("INR");
        }
    }

}
