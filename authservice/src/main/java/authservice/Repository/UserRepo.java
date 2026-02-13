package authservice.Repository;

import authservice.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<UserInfo,Long> {

     UserInfo findByUsername(String username);

}
