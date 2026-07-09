package com.securelib.config;

/**
 * Holds credentials for an (imaginary) cloud backup of the catalogue.
 *
 * NOTE: the credentials below are INTENTIONALLY hardcoded to demonstrate the
 * secret-scanning gate (gitleaks). Real credentials must never live in source
 * control. Remediation (environment variables / a secret manager) is described
 * in docs/REMEDIATION.md.
 */
public class BackupConfig {

    // VULNERABLE: hardcoded cloud access keys committed to the repository.
    public static final String AWS_ACCESS_KEY_ID = "AKIA3SG5HZ4PLM9QWERT";
    public static final String AWS_SECRET_ACCESS_KEY = "aB9x2LmNp0QrS3tUvWx4yZ5aBcDeFgHiJkLmNoPq";

    private BackupConfig() {
    }
}
