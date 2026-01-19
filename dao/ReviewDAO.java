package projekPBO.projek.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import projekPBO.projek.Review;
import projekPBO.projek.db.DBConnection;
import projekPBO.projek.util.AppLogger;

public class ReviewDAO {

    // Get all reviews
    public static List<Review> getAllReviews() {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT id, transaction_id, film_name, rating, comment, created_at FROM reviews ORDER BY created_at DESC";

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Review(
                        rs.getInt("id"),
                        rs.getInt("transaction_id"),
                        rs.getString("film_name"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
            AppLogger.database("getAllReviews", "Loaded " + list.size() + " reviews");
        } catch (Exception e) {
            AppLogger.databaseError("getAllReviews", e);
        }
        return list;
    }

    // Get reviews by film name
    public static List<Review> getReviewsByFilm(String filmName) {
        List<Review> list = new ArrayList<>();
        String sql = "SELECT id, transaction_id, film_name, rating, comment, created_at FROM reviews WHERE film_name=? ORDER BY created_at DESC";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, filmName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Review(
                            rs.getInt("id"),
                            rs.getInt("transaction_id"),
                            rs.getString("film_name"),
                            rs.getInt("rating"),
                            rs.getString("comment"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    ));
                }
            }
            AppLogger.database("getReviewsByFilm", "Loaded " + list.size() + " reviews for " + filmName);
        } catch (Exception e) {
            AppLogger.databaseError("getReviewsByFilm", e);
        }
        return list;
    }

    // Insert new review
    public static int insertReview(int transactionId, String filmName, int rating, String comment) {
        String sql = "INSERT INTO reviews (transaction_id, film_name, rating, comment, created_at) VALUES (?, ?, ?, ?, NOW()) RETURNING id";
        int reviewId = -1;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, transactionId);
            ps.setString(2, filmName);
            ps.setInt(3, rating);
            ps.setString(4, comment);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    reviewId = rs.getInt(1);
                    AppLogger.database("insertReview", "Review saved: ID=" + reviewId + ", Film=" + filmName + ", Rating=" + rating);
                }
            }
        } catch (Exception e) {
            AppLogger.databaseError("insertReview", e);
        }
        return reviewId;
    }

    // Update review
    public static void updateReview(int id, int rating, String comment) {
        String sql = "UPDATE reviews SET rating=?, comment=? WHERE id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, id);
            ps.executeUpdate();
            AppLogger.database("updateReview", "Review ID=" + id + " updated");

        } catch (Exception e) {
            AppLogger.databaseError("updateReview", e);
        }
    }

    // Delete review
    public static void deleteReview(int id) {
        String sql = "DELETE FROM reviews WHERE id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            AppLogger.database("deleteReview", "Review ID=" + id + " deleted");

        } catch (Exception e) {
            AppLogger.databaseError("deleteReview", e);
        }
    }

    // Get average rating for a film
    public static double getAverageRating(String filmName) {
        String sql = "SELECT AVG(rating) as avg_rating FROM reviews WHERE film_name=?";
        double average = 0.0;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, filmName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    average = rs.getDouble("avg_rating");
                }
            }
        } catch (Exception e) {
            AppLogger.databaseError("getAverageRating", e);
        }
        return average;
    }

    // Get review count for a film
    public static int getReviewCount(String filmName) {
        String sql = "SELECT COUNT(*) as count FROM reviews WHERE film_name=?";
        int count = 0;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, filmName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("count");
                }
            }
        } catch (Exception e) {
            AppLogger.databaseError("getReviewCount", e);
        }
        return count;
    }

    // Get all unique film names that have reviews
    public static List<String> getAllFilmNamesWithReviews() {
        List<String> films = new ArrayList<>();
        String sql = "SELECT DISTINCT film_name FROM reviews ORDER BY film_name";

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                films.add(rs.getString("film_name"));
            }
            AppLogger.database("getAllFilmNamesWithReviews", "Loaded " + films.size() + " films with reviews");
        } catch (Exception e) {
            AppLogger.databaseError("getAllFilmNamesWithReviews", e);
        }
        return films;
    }

    // Get review by ID
    public static Review getReviewById(int reviewId) {
        String sql = "SELECT id, transaction_id, film_name, rating, comment, created_at FROM reviews WHERE id=?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, reviewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Review(
                            rs.getInt("id"),
                            rs.getInt("transaction_id"),
                            rs.getString("film_name"),
                            rs.getInt("rating"),
                            rs.getString("comment"),
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                }
            }
        } catch (Exception e) {
            AppLogger.databaseError("getReviewById", e);
        }
        return null;
    }
}
