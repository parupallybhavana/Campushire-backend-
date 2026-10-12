package com.example.campushire.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class DatabaseConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    @Value("${spring.datasource.url:}")
    private String defaultUrl;

    @Value("${spring.datasource.username:}")
    private String defaultUsername;

    @Value("${spring.datasource.password:}")
    private String defaultPassword;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Priority 1: DATABASE_URL (Render standard environment variable)
        String dbUrl = System.getenv("DATABASE_URL");
        if (!StringUtils.hasText(dbUrl)) {
            // Priority 2: SPRING_DATASOURCE_URL
            dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        }
        if (!StringUtils.hasText(dbUrl)) {
            // Priority 3: defaultUrl from application.properties
            dbUrl = defaultUrl;
        }

        HikariConfig config = new HikariConfig();

        if (StringUtils.hasText(dbUrl)) {
            String cleanUrl = dbUrl.trim();
            // Handle postgres://, postgresql://, or jdbc:postgres[ql]:// containing credentials
            if (cleanUrl.startsWith("postgres://") || cleanUrl.startsWith("postgresql://") ||
                (cleanUrl.startsWith("jdbc:postgres") && cleanUrl.contains("@"))) {
                configurePostgres(config, cleanUrl);
            } else {
                configureStandardJdbc(config, cleanUrl);
            }
        } else {
            throw new IllegalStateException("No database URL configured! Please set DATABASE_URL or SPRING_DATASOURCE_URL.");
        }

        // Connection pool optimization for cloud & Render free tier
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(300000); // 5 mins
        config.setMaxLifetime(1800000); // 30 mins
        config.setConnectionTimeout(30000); // 30s
        config.setValidationTimeout(5000); // 5s

        return new HikariDataSource(config);
    }

    private void configurePostgres(HikariConfig config, String rawUrl) {
        try {
            // Normalize prefix
            String url = rawUrl;
            if (url.startsWith("jdbc:")) {
                url = url.substring(5);
            }

            // Pattern: (postgres|postgresql)://[username[:password]@]host[:port][/database][?params]
            Pattern pattern = Pattern.compile("^(?:postgres|postgresql)://(?:([^:@]+)(?::([^@]*))?@)?([^/:?#]+)(?::(\\d+))?(?:/([^?#]*))?(?:\\?(.*))?$");
            Matcher matcher = pattern.matcher(url);

            if (matcher.matches()) {
                String rawUser = matcher.group(1);
                String rawPass = matcher.group(2);
                String host = matcher.group(3);
                String port = matcher.group(4) != null ? matcher.group(4) : "5432";
                String dbName = matcher.group(5) != null ? matcher.group(5) : "";
                String query = matcher.group(6);

                String username = rawUser != null ? URLDecoder.decode(rawUser, StandardCharsets.UTF_8) : null;
                String password = rawPass != null ? URLDecoder.decode(rawPass, StandardCharsets.UTF_8) : null;

                StringBuilder jdbcUrl = new StringBuilder("jdbc:postgresql://")
                        .append(host)
                        .append(":")
                        .append(port)
                        .append("/")
                        .append(dbName);

                if (StringUtils.hasText(query)) {
                    jdbcUrl.append("?").append(query);
                } else if (host.contains(".render.com")) {
                    // Render external connection requires SSL
                    jdbcUrl.append("?sslmode=require");
                }

                config.setJdbcUrl(jdbcUrl.toString());
                if (username != null) {
                    config.setUsername(username);
                }
                if (password != null) {
                    config.setPassword(password);
                }
                config.setDriverClassName("org.postgresql.Driver");

                log.info("Configured PostgreSQL DataSource for host: {}:{} db: {}", host, port, dbName);
                return;
            }
        } catch (Exception e) {
            log.warn("Regex parsing of PostgreSQL URL failed, attempting standard URI fallback: {}", e.getMessage());
        }

        // Fallback using java.net.URI if regex did not match
        try {
            String sanitized = rawUrl.replace("postgresql://", "postgres://");
            URI uri = new URI(sanitized);
            String host = uri.getHost();
            int port = uri.getPort() > 0 ? uri.getPort() : 5432;
            String path = uri.getPath() != null ? uri.getPath() : "/campushire";
            String query = uri.getQuery();

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
            if (StringUtils.hasText(query)) {
                jdbcUrl += "?" + query;
            } else if (host != null && host.contains(".render.com")) {
                jdbcUrl += "?sslmode=require";
            }

            config.setJdbcUrl(jdbcUrl);
            if (uri.getUserInfo() != null) {
                String[] userInfo = uri.getUserInfo().split(":", 2);
                config.setUsername(URLDecoder.decode(userInfo[0], StandardCharsets.UTF_8));
                if (userInfo.length > 1) {
                    config.setPassword(URLDecoder.decode(userInfo[1], StandardCharsets.UTF_8));
                }
            }
            config.setDriverClassName("org.postgresql.Driver");
            log.info("Fallback URI configured PostgreSQL DataSource successfully.");
        } catch (Exception e) {
            log.error("Failed to parse PostgreSQL URL: {}", e.getMessage(), e);
            config.setJdbcUrl(rawUrl);
        }
    }

    private void configureStandardJdbc(HikariConfig config, String dbUrl) {
        config.setJdbcUrl(dbUrl);

        String envUser = System.getenv("SPRING_DATASOURCE_USERNAME");
        if (StringUtils.hasText(envUser)) {
            config.setUsername(envUser);
        } else if (StringUtils.hasText(defaultUsername)) {
            config.setUsername(defaultUsername);
        }

        String envPass = System.getenv("SPRING_DATASOURCE_PASSWORD");
        if (StringUtils.hasText(envPass)) {
            config.setPassword(envPass);
        } else if (StringUtils.hasText(defaultPassword)) {
            config.setPassword(defaultPassword);
        }

        if (dbUrl.contains("postgresql")) {
            config.setDriverClassName("org.postgresql.Driver");
        } else if (dbUrl.contains("mysql")) {
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        } else if (dbUrl.contains("h2")) {
            config.setDriverClassName("org.h2.Driver");
        }

        log.info("Configured standard JDBC DataSource for URL: {}", dbUrl.replaceAll(":[^/@:]+@", ":***@"));
    }
}
