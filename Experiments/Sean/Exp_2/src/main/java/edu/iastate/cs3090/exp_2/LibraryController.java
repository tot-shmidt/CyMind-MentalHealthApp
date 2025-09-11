package edu.iastate.cs3090.exp_2;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class LibraryController {
    private final HashMap<Long, Book> books = new HashMap<Long, Book>();

    // LIST
    @GetMapping("/books")
    public HashMap<Long, Book> getBooks() {
        return books;
    }

    // CREATE
    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    public Book addBook(@RequestBody Book book) {
        books.put(book.getIsbn(), book);
        return book;
    }

    // UPDATE
    @PutMapping("/books")
    public Book updateBook(@RequestBody Book book) {
        books.replace(book.getIsbn(), book);
        return book;
    }

    // READ
    @GetMapping("/books/{isbn}")
    public Book getBook(@PathVariable Long isbn) {
        return books.get(isbn);
    }

    // DELETE
    @DeleteMapping("/books/{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long isbn) {
        books.remove(isbn);
    }

    @PostMapping("/books/{isbn}/checkout")
    public Book checkoutBook(@PathVariable Long isbn) {
        Book book = books.get(isbn);
        book.setCheckedOut(true);
        return book;
    }

    @PostMapping("/books/{isbn}/return")
    public Book returnBook(@PathVariable Long isbn) {
        Book book = books.get(isbn);
        book.setCheckedOut(false);
        return book;
    }
}
