package edu.iastate.cs3090.exp_1.blog;

import edu.iastate.cs3090.exp_1.model.Comment;
import lombok.Getter;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CommentManager {
    @Getter
    private static final HashMap<Integer, Comment> comments = new HashMap<Integer, Comment>();

    public static Comment addComment(Comment comment) {
        comments.put(comment.getId(), comment);
        return comment;
    }

    public static Comment getComment(Integer id) throws KeyException {
        if (!comments.containsKey(id)) {
            throw new KeyException("Blogpost with that ID does not exist");
        }
        return comments.get(id);
    }

    public static ArrayList<Comment> getCommentsByPost(Integer blogpostId) throws KeyException {
        ArrayList<Comment> postComments = new ArrayList<>();

        for (Comment comment : comments.values()) {
            if (Objects.equals(comment.getBlogId(), blogpostId)) {
                postComments.add(comment);
            }
        }

        return postComments;
    }

    public static void replacePost(Integer id, Comment comment) throws KeyException {
        if (!comments.containsKey(id)) {
            throw new KeyException("Blogpost does not exist");
        }
        comments.replace(comment.getId(), comment);
    }

    public static void deletePost(Integer id) throws KeyException {

    }
}
