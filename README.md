# SecureLib — DevSecOps pipeline demo

A deliberately tiny Spring Boot book-lookup API used to demonstrate how
security gates fit into a CI/CD pipeline (Lecture 4: threat modeling, secure
pipeline gates, SAST, SCA, DAST).

The application has **one user input** (the `title` search parameter) and
**one business action** (searching the catalogue), and ships with three
intentional flaws so each pipeline gate has something real to detect.

## The application

```
GET /search?title=Dune   ->  [{ "id": 1, "title": "Dune", "author": "Frank Herbert" }]
```

Stack: Java 21, Spring Boot 3.3, Spring JDBC, in-memory H2 database.

Run it locally:
```bash
mvn spring-boot:run
curl "http://localhost:8080/search?title=Dune"
```

## Intentional vulnerabilities

| # | Vulnerability | Where | Detected by |
|---|---------------|-------|-------------|
| 1 | SQL injection (string concatenation) | `service/BookService.java` | SAST (Semgrep) |
| 2 | Hardcoded credentials | `config/BackupConfig.java`, `application.properties` | Secret scanning (gitleaks) + SAST |
| 3 | Vulnerable dependency — Log4Shell (log4j 2.14.1) | `pom.xml` | SCA (Trivy) |

Fixes for all three are in [`docs/REMEDIATION.md`](docs/REMEDIATION.md).

## The pipeline — three security gates

Defined in [`.github/workflows/security.yml`](.github/workflows/security.yml).
All three gates are static, run on every push / pull request (shift-left), and
**fail the build** when they find something.

1. **Secret scanning — gitleaks.** Runs first on the raw source; cheapest check,
   stops a leaked credential from ever reaching a build.
2. **SAST — Semgrep.** Analyses our own source code for insecure patterns such
   as the SQL injection.
3. **SCA — Trivy.** Analyses third-party dependencies in `pom.xml` for known CVEs.

## Reproduce the scans locally

```bash
# Secret scanning
gitleaks detect --source . --no-git --config .gitleaks.toml --redact --verbose

# SAST (project rules; add --config p/java for the full registry pack)
semgrep scan --config .semgrep/ .

# SCA
trivy fs --scanners vuln --severity HIGH,CRITICAL .
```

Saved output from a real run is in [`docs/evidence/`](docs/evidence/).
