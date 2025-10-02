package cymind.repository;

import cymind.model.AbstractUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository is responsible for managin the parent abstract class and child classes.
 */
@Repository
public interface AbstractUserRepository extends JpaRepository<AbstractUser, Long> {
	AbstractUser findByEmail(String email);
	AbstractUser findById(long id);
}
