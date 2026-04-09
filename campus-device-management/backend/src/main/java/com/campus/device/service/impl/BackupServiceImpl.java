package com.campus.device.service.impl;

import com.campus.device.service.BackupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class BackupServiceImpl implements BackupService {

    private static final Pattern SAFE_IDENTIFIER = Pattern.compile("^[a-zA-Z0-9_\\-\\.]+$");

    @Value("${backup.dir}")
    private String backupDir;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public String backupDatabase() throws Exception {
        File dir = new File(backupDir);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("Failed to create backup directory: " + backupDir);
        }

        String dbName = extractDatabaseName(datasourceUrl);
        String dbHost = extractHost(datasourceUrl);
        String dbPort = extractPort(datasourceUrl);
        validateIdentifier(dbName, "database name");
        validateIdentifier(dbHost, "database host");
        validateIdentifier(dbPort, "database port");

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = dbName + "_" + timestamp + ".sql";
        String filePath = new File(backupDir, fileName).getAbsolutePath();

        // Write credentials to temp file to avoid password in process args
        File credFile = File.createTempFile("mysqldump_", ".cnf");
        credFile.deleteOnExit();
        try {
            String credContent = "[mysqldump]\nuser=" + dbUsername + "\npassword=" + dbPassword + "\n";
            Files.write(credFile.toPath(), credContent.getBytes(StandardCharsets.UTF_8));

            String[] command = {
                    "mysqldump",
                    "--defaults-extra-file=" + credFile.getAbsolutePath(),
                    "-h", dbHost,
                    "-P", dbPort,
                    "--single-transaction",
                    "--routines",
                    "--triggers",
                    dbName,
                    "--result-file=" + filePath
            };

            log.info("Running mysqldump to: {}", filePath);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("mysqldump failed (exit " + exitCode + "): " + output);
            }
        } finally {
            Files.deleteIfExists(credFile.toPath());
        }

        log.info("Backup created: {}", filePath);
        return fileName;
    }

    @Override
    public List<String> listBackups() {
        File dir = new File(backupDir);
        if (!dir.exists()) {
            return new ArrayList<>();
        }
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sql"));
        if (files == null) {
            return new ArrayList<>();
        }
        List<String> names = new ArrayList<>();
        Arrays.sort(files, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        for (File f : files) {
            names.add(f.getName());
        }
        return names;
    }

    private void validateIdentifier(String value, String label) {
        if (!SAFE_IDENTIFIER.matcher(value).matches()) {
            throw new IllegalArgumentException("Unsafe " + label + ": " + value);
        }
    }

    private String extractDatabaseName(String url) {
        try {
            String withoutJdbc = url.replace("jdbc:mysql://", "");
            String path = withoutJdbc.contains("/") ? withoutJdbc.substring(withoutJdbc.indexOf('/') + 1) : withoutJdbc;
            String dbName = path.contains("?") ? path.substring(0, path.indexOf('?')) : path;
            return dbName.isEmpty() ? "campus_device" : dbName;
        } catch (Exception e) {
            return "campus_device";
        }
    }

    private String extractHost(String url) {
        try {
            String withoutJdbc = url.replace("jdbc:mysql://", "");
            String hostPort = withoutJdbc.substring(0, withoutJdbc.indexOf('/'));
            String host = hostPort.contains(":") ? hostPort.substring(0, hostPort.indexOf(':')) : hostPort;
            return host.isEmpty() ? "localhost" : host;
        } catch (Exception e) {
            return "localhost";
        }
    }

    private String extractPort(String url) {
        try {
            String withoutJdbc = url.replace("jdbc:mysql://", "");
            String hostPort = withoutJdbc.substring(0, withoutJdbc.indexOf('/'));
            return hostPort.contains(":") ? hostPort.substring(hostPort.indexOf(':') + 1) : "3306";
        } catch (Exception e) {
            return "3306";
        }
    }
}

