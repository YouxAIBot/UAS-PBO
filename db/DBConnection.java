package projekPBO.projek.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import projekPBO.projek.util.AppLogger;

public class DBConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/bioskop_db";
    private static final String USER = "postgres";
    private static final String PASS = "YouxSQL485";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (java.sql.SQLException e) {
            AppLogger.databaseError("DBConnection.getConnection()", e);
            return null;
        }
    }

    /**
     * Diagnostic helper: returns a short summary of DB connectivity and schema.
     * Logs list of tables and first few columns for troubleshooting.
     */
    public static void logDatabaseDiagnostics() {
        try (Connection c = getConnection()) {
            if (c == null) {
                AppLogger.database("diagnostics", "Connection returned null - cannot inspect database");
                return;
            }
            DatabaseMetaData md = c.getMetaData();
            String product = md.getDatabaseProductName();
            String version = md.getDatabaseProductVersion();
            AppLogger.database("diagnostics", "Connected to " + product + " - " + version);

            try (ResultSet tables = md.getTables(null, null, "%", new String[]{"TABLE"})) {
                StringBuilder sb = new StringBuilder();
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    sb.append(tableName).append(',');
                    // list up to 5 columns
                    try (ResultSet cols = md.getColumns(null, null, tableName, "%")) {
                        int i=0; StringBuilder colsBuf = new StringBuilder();
                        while (cols.next() && i<5) {
                            colsBuf.append(cols.getString("COLUMN_NAME")).append(','); i++;
                        }
                        AppLogger.database("diagnostics", "Table: " + tableName + " Columns: " + colsBuf.toString());
                    } catch (SQLException ce) {
                        AppLogger.databaseError("diagnostics.getColumns("+tableName+")", ce);
                    }
                }
                AppLogger.database("diagnostics", "Tables found: " + sb.toString());
            }

        } catch (SQLException e) {
            AppLogger.databaseError("DBConnection.logDatabaseDiagnostics", e);
        }
    }
}
