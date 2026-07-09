# Remediation guide

Each vulnerability below was intentionally introduced to be caught by a
pipeline gate. This file shows the **before** (vulnerable) and **after**
(fixed) version so the fixes can be applied and the pipeline turned green.

---

## 1. SQL injection — detected by the SAST gate (Semgrep)

**File:** `src/main/java/com/securelib/service/BookService.java`
**Rule:** `jdbctemplate-sql-injection` · CWE-89 · OWASP A03:2021 – Injection

The user-controlled `title` is concatenated straight into the SQL string, so
`/search?title=' OR '1'='1` returns the whole table (and worse queries are
possible).

**Before (vulnerable):**
```java
public List<Map<String, Object>> searchByTitle(String title) {
    String sql = "SELECT id, title, author FROM books WHERE title = '" + title + "'";
    return jdbcTemplate.queryForList(sql);
}
```

**After (fixed) — parameterized query with a `?` placeholder:**
```java
public List<Map<String, Object>> searchByTitle(String title) {
    String sql = "SELECT id, title, author FROM books WHERE title = ?";
    return jdbcTemplate.queryForList(sql, title);
}
```

The input is now sent to the database as a bound parameter, never as
executable SQL, which removes the injection entirely.

---

## 2. Hardcoded credentials — detected by the secret-scanning gate (gitleaks) and SAST

**File:** `src/main/java/com/securelib/config/BackupConfig.java`
**Rules:** gitleaks `aws-access-token` / `generic-api-key` · CWE-798 · OWASP A07:2021

**Before (vulnerable):**
```java
public static final String AWS_ACCESS_KEY_ID = "AKIA3SG5HZ4PLM9QWERT";
public static final String AWS_SECRET_ACCESS_KEY = "aB9x2LmNp0QrS3tUvWx4yZ5aBcDeFgHiJkLmNoPq";
```

**After (fixed) — read from the environment, never committed:**
```java
public static final String AWS_ACCESS_KEY_ID = System.getenv("AWS_ACCESS_KEY_ID");
public static final String AWS_SECRET_ACCESS_KEY = System.getenv("AWS_SECRET_ACCESS_KEY");
```

The same applies to `spring.datasource.password` in `application.properties`,
which should become `spring.datasource.password=${DB_PASSWORD}`. Any secret
previously committed must also be **rotated**, because it stays in git history.

---

## 3. Vulnerable dependency (Log4Shell) — detected by the SCA gate (Trivy)

**File:** `pom.xml`
**CVEs:** CVE-2021-44228, CVE-2021-45046 (Log4Shell) · OWASP A06:2021 – Vulnerable and Outdated Components

**Before (vulnerable):**
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.14.1</version>
</dependency>
```

**After (fixed) — upgrade to a patched release:**
```xml
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>2.24.1</version>
</dependency>
```

Upgrading to a fixed version removes the remote-code-execution risk introduced
by the JNDI lookup feature of the older library.
