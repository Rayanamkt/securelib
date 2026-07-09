package com.securelib.controller;

import com.securelib.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Exposes the single business action of the app: searching books by title.
 * The "title" query parameter is the untrusted user input.
 *
 * Example:  GET /search?title=Dune
 */
@RestController
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(@RequestParam("title") String title) {
        return bookService.searchByTitle(title);
    }
}
