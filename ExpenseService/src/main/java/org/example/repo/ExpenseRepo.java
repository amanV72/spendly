package org.example.repo;


import org.apache.kafka.common.protocol.types.Field;
import org.example.entities.Expense;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
 public interface ExpenseRepo extends CrudRepository<Expense, Long> {

     List<Expense> findByUserId(String userID);


     List<Expense> findByUserIdAndCreatedAtBetween(String userId, Timestamp startTime,Timestamp endTime);

     Optional<Expense> findByUserIdAndExternalId(String userId,String externalId);




}
