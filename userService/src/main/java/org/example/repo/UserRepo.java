package org.example.repo;

import org.example.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<UserInfo,String> {

    Optional<UserInfo> findByUserID(String userID);
}
