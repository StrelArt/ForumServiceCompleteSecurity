package telran.java58.accounting.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import telran.java58.accounting.model.UserAccount;

@EnableMongoRepositories
public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
}
