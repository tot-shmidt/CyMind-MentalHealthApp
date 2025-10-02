package cymind.security;

import cymind.model.AbstractUser;
import cymind.repository.AbstractUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AbstractUserDetailsService implements UserDetailsService {
    @Autowired
    AbstractUserRepository abstractUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AbstractUser abstractUser = abstractUserRepository.findByEmail(username);
        if (abstractUser == null) {
            throw new UsernameNotFoundException(username);
        }

        return abstractUser;
    }
}
