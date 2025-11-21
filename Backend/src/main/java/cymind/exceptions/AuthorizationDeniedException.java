package cymind.exceptions;

import org.springframework.security.authorization.AuthorizationResult;

/**
 * Wrapper around Spring's AuthorizationDeniedException to match its signature from Springboot 3.4.3
 */
public class AuthorizationDeniedException extends org.springframework.security.authorization.AuthorizationDeniedException {
    public AuthorizationDeniedException(String msg) {
        super(msg, new AuthorizationResult() {
            @Override
            public boolean isGranted() {
                return false;
            }
        });
    }
}
