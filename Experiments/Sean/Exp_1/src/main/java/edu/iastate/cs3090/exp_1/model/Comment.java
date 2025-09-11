package edu.iastate.cs3090.exp_1.model;

import edu.iastate.cs3090.exp_1.blog.UserManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.security.KeyException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Comment {
    private static final AtomicInteger counter = new AtomicInteger(0);

    private final Integer id = counter.incrementAndGet();
    @Setter private String content;
    private final Integer blogId;
    private final User author;

    public Comment(String content, Integer blogId, UUID userId) throws KeyException {
        this.content = content;
        this.blogId = blogId;
        this.author = UserManager.getUser(userId);
    }
}
