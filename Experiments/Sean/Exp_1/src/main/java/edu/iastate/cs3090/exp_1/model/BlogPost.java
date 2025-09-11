package edu.iastate.cs3090.exp_1.model;

import edu.iastate.cs3090.exp_1.blog.UserManager;
import lombok.Getter;
import lombok.Setter;

import java.security.KeyException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class BlogPost {
    private static final AtomicInteger counter = new AtomicInteger(0);

    private final Integer id = counter.incrementAndGet();
    private final Date date = new Date();
    private String title;
    private String content;
    private User author;

    public BlogPost(String title, String content, UUID userId) throws KeyException {
        this.title = title;
        this.content = content;
        this.author = UserManager.getUser(userId);
    }
}