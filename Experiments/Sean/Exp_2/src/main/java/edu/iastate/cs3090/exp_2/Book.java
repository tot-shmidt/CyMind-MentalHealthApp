package edu.iastate.cs3090.exp_2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Book {
    private String title;
    private String author;
    private Long isbn;
    @Setter private boolean isCheckedOut;
}
