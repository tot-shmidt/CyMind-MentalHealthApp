package cymind.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import cymind.model.AbstractUser;

/**
 * This repository is responsible for managin the parent abstract class and child classes.
 */
public interface AbstractUserRepository extends JpaRepository<AbstractUser, Long> {
	AbstractUser findByEmail(String email);
	AbstractUser findById(long id);
}
