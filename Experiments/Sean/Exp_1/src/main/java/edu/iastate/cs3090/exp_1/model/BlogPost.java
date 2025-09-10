package edu.iastate.cs3090.exp_1.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BlogPost {
    @NotBlank private String title;
    @NotBlank private String content;
    private User author;
    private Date date;
}
