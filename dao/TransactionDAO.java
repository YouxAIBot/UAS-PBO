package projekPBO.projek.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projekPBO.projek.db.DBConnection;
import projekPBO.projek.util.AppLogger;

public class TransactionDAO {

    // Insert transaction
    public static int insertTransaction(
            String film,
            int qty,
            String snack,
            int total,
            String payment
    ) {
        String sql = """
            INSERT INTO transactions
            (film_nama, ticket_qty, snack_detail, total, payment_method)
            VALUES (?, ?, ?, ?, ?) RETURNING id
        """;
        int transactionId = -1;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, film);
            ps.setInt(2, qty);
            ps.setString(3, snack);
            ps.setInt(4, total);
            ps.setString(5, payment);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    transactionId = rs.getInt(1);
                    AppLogger.database("insertTransaction", "Transaction saved: ID=" + transactionId);
                }
            }

        } catch (Exception e) {
            AppLogger.databaseError("insertTransaction", e);
        }
        return transactionId;
    }

    // Get all transactions
    public static List<Map<String, Object>> getAllTransactions() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, film_nama as film_name, ticket_qty, snack_detail, total, payment_method, created_at FROM transactions ORDER BY created_at DESC";

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> transaction = new HashMap<>();
                transaction.put("id", rs.getInt("id"));
                transaction.put("film_name", rs.getString("film_name"));
                transaction.put("ticket_qty", rs.getInt("ticket_qty"));
                transaction.put("snack_detail", rs.getString("snack_detail"));
                transaction.put("total", rs.getInt("total"));
                transaction.put("payment_method", rs.getString("payment_method"));
                transaction.put("created_at", rs.getTimestamp("created_at"));
                list.add(transaction);
            }
        } catch (Exception e) {
            AppLogger.databaseError("getAllTransactions", e);
        }
        return list;
    }

    // Update transaction
    public static void updateTransaction(
            int id,
            String film,
            int qty,
            String snack,
            int total,
            String payment
    ) {
        String sql = """
            UPDATE transactions
            SET film_nama=?, ticket_qty=?, snack_detail=?, total=?, payment_method=?
            WHERE id=?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, film);
            ps.setInt(2, qty);
            ps.setString(3, snack);
            ps.setInt(4, total);
            ps.setString(5, payment);
            ps.setInt(6, id);
            ps.executeUpdate();
            AppLogger.database("updateTransaction", "Transaction ID=" + id + " updated");

        } catch (Exception e) {
            AppLogger.databaseError("updateTransaction", e);
        }
    }

    // Delete transaction
    public static void deleteTransaction(int id) {
        String sql = "DELETE FROM transactions WHERE id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            AppLogger.database("deleteTransaction", "Transaction ID=" + id + " deleted");

        } catch (Exception e) {
            AppLogger.databaseError("deleteTransaction", e);
        }
    }

    // Get transaction by ID
    public static Map<String, Object> getTransactionById(int id) {
        String sql = "SELECT id, film_nama as film_name, ticket_qty, snack_detail, total, payment_method, created_at FROM transactions WHERE id=?";
        Map<String, Object> transaction = new HashMap<>();

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    transaction.put("id", rs.getInt("id"));
                    transaction.put("film_name", rs.getString("film_name"));
                    transaction.put("ticket_qty", rs.getInt("ticket_qty"));
                    transaction.put("snack_detail", rs.getString("snack_detail"));
                    transaction.put("total", rs.getInt("total"));
                    transaction.put("payment_method", rs.getString("payment_method"));
                    transaction.put("created_at", rs.getTimestamp("created_at"));
                }
            }

        } catch (Exception e) {
            AppLogger.databaseError("getTransactionById", e);
        }
        return transaction;
    }
}

