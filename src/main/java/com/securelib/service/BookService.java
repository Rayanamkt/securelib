package com.securelib.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Business logic for looking up books.
 *
 * NOTE: searchByTitle() below contains an INTENTIONAL SQL injection flaw
 * (string concatenation of untrusted input into a SQL query). It is used
 * to demonstrate the SAST gate (Semgrep). The remediation using a
 * parameterized query is documented in docs/REMEDIATION.md.
 */
@Service
public class BookService {

    private final JdbcTemplate jdbcTemplate;

    public BookService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // VULNERABLE: user input is concatenated directly into the SQL string.
    // A request like  /search?title=' OR '1'='1  returns the whole table.
    public List<Map<String, Object>> searchByTitle(String title) {
        String sql = "SELECT id, title, author FROM books WHERE title = '" + title + "'";
        return jdbcTemplate.queryForList(sql);
    }
}
