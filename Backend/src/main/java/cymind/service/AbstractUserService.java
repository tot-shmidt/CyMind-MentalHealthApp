package cymind.service;

import cymind.model.AbstractUser;
import cymind.repository.AbstractUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AbstractUserService {
    @Autowired
    private AbstractUserRepository abstractUserRepository;

    public AbstractUser createUser(AbstractUser abstractUser) {
        return abstractUserRepository.save(abstractUser);
    }
}
