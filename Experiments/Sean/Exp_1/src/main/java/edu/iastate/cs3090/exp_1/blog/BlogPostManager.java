package edu.iastate.cs3090.exp_1.blog;

import edu.iastate.cs3090.exp_1.model.BlogPost;
import lombok.Getter;

import java.security.KeyException;
import java.util.HashMap;

public class BlogPostManager {
    @Getter
    private static final HashMap<Integer, BlogPost> posts = new HashMap<Integer, BlogPost>();

    public static BlogPost addPost(BlogPost post) {
        posts.put(post.getId(), post);
        return post;
    }

    public static BlogPost getPost(Integer id) throws KeyException {
        if (!posts.containsKey(id)) {
            throw new KeyException("Blogpost with that ID does not exist");
        }
        return posts.get(id);
    }

    public static void replacePost(Integer id, BlogPost post) throws KeyException {
        if (!posts.containsKey(id)) {
            throw new KeyException("Blogpost does not exist");
        }
        posts.replace(post.getId(), post);
    }

    public static void deletePost(Integer id) throws KeyException {
        if (!posts.containsKey(id)) {
            throw new KeyException("Blogpost does not exist");
        }
        posts.remove(id);
    }
}