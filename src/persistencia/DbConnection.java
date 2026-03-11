/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class DbConnection {
    private static DbConnection instance;
    private Connection conn;
    private String url;
    private String username;
    private String password;

    public DbConnection() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Reader input = new FileReader("dataBaseConfig.json");
            JsonNode json = mapper.readTree(input);
            String rawUrl = getText(json, "url");
            String host = getText(json, "host");
            String port = getText(json, "port");
            String database = getText(json, "database");
            if (database.isEmpty()) {
                database = getText(json, "dbname");
            }

            username = getText(json, "user");
            password = getText(json, "passwd");
            url = buildJdbcUrl(rawUrl, host, port, database, username);

            if (url.isEmpty()) {
                throw new SQLException("URL de conexao invalida em dataBaseConfig.json");
            }

            Class.forName("org.postgresql.Driver");
            DriverManager.setLoginTimeout(10);
            this.conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException ex) {
            System.out.println("Database Connection Creation Failed : " + ex.getMessage());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE,
                    "Falha ao conectar no banco. Verifique dataBaseConfig.json (url/host/database/user/passwd).", ex);
        } catch (IOException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public static Connection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DbConnection();
        } else if (instance.getConnection() == null || instance.getConnection().isClosed()) {
            instance = new DbConnection();
        }

        if (instance.getConnection() == null) {
            throw new SQLException("Conexao com banco nao foi estabelecida. Ajuste o dataBaseConfig.json");
        }

        return instance.getConnection();

    }

    private static String getText(JsonNode node, String key) {
        JsonNode value = node.get(key);
        if (value == null || value.isNull()) {
            return "";
        }
        return value.asText().trim();
    }

    private static String buildJdbcUrl(String rawUrl, String host, String port, String database, String username) {
        String source = rawUrl;
        if (source.isEmpty()) {
            source = host;
        }

        if (source.isEmpty()) {
            return "";
        }

        if (source.startsWith("jdbc:postgresql://")) {
            return source;
        }

        if (source.startsWith("postgresql://")) {
            return "jdbc:" + source;
        }

        String normalizedPort = port.isEmpty() ? "5432" : port;
        String normalizedDb = database.isEmpty() ? Objects.toString(username, "") : database;

        if (source.contains("/")) {
            return "jdbc:postgresql://" + source;
        }

        if (normalizedDb.isEmpty()) {
            return "";
        }

        return "jdbc:postgresql://" + source + ":" + normalizedPort + "/" + normalizedDb;

    }

}
