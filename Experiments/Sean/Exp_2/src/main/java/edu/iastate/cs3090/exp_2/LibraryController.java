package edu.iastate.cs3090.exp_2;

import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@RestController
public class LibraryController {
    private final HashMap<Long, Book> books = new HashMap<Long, Book>();

    @GetMapping(value = "/books")
    public ArrayList<Book> getBooks(@RequestParam(required = false) MultiValueMap<String, String> params) {
        ArrayList<Book> filteredBooks = new ArrayList<>(books.values());
        ArrayList<Book> tempFilteredBooks = new ArrayList<>();

        if (params.isEmpty()) {
            return filteredBooks;
        }

        if (params.containsKey("filter")) {
            List<String> filterList = (List<String>) params.get("filter");
            for (String filter : filterList) {
                filter = filter.toLowerCase();

                if (filter.startsWith("author:")) {
                    filter = filter.substring("author:".length());
                    for (Book book : filteredBooks) {
                        if (book.getAuthor().toLowerCase().contains(filter)) {
                            tempFilteredBooks.add(book);
                        }
                    }
                } else if (filter.startsWith("title:")) {
                    filter = filter.substring("title:".length());
                    for (Book book : filteredBooks) {
                        if (book.getTitle().toLowerCase().contains(filter)) {
                            tempFilteredBooks.add(book);
                        }
                    }
                } else if (filter.startsWith("available")) {
                    for (Book book : filteredBooks) {
                        if (!book.isCheckedOut()) {
                            tempFilteredBooks.add(book);
                        }
                    }
                }

                filteredBooks = tempFilteredBooks;
                tempFilteredBooks = new ArrayList<>();
            }
        }


        if (params.containsKey("sort")) {
            List<String> sortList = (List<String>) params.get("sort");
            for (String sort : sortList) {
                sort = sort.toLowerCase();
                switch (sort) {
                    case "isbn" -> filteredBooks.sort(Comparator.comparing(Book::getIsbn));
                    case "title" -> filteredBooks.sort(Comparator.comparing(Book::getTitle));
                    case "author" -> filteredBooks.sort(Comparator.comparing(Book::getAuthor));
                }
            }
        }

        return filteredBooks;
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
