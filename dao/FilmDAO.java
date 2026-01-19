package projekPBO.projek.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import projekPBO.projek.Film;
import projekPBO.projek.db.DBConnection;
import projekPBO.projek.util.AppLogger;

public class FilmDAO {

    public static List<Film> getAllFilms() {
        List<Film> list = new ArrayList<>();
        String sql = "SELECT * FROM films";

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Film(
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getInt("stock")
                ));
            }
            AppLogger.database("getAllFilms", "Loaded " + list.size() + " films");
        } catch (Exception e) {
            AppLogger.databaseError("getAllFilms", e);
        }
        return list;
    }

    public static void updateFilm(int id, String name, int price, int stock) {
        String sql = "UPDATE films SET name=?, price=?, stock=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, price);
            ps.setInt(3, stock);
            ps.setInt(4, id);
            ps.executeUpdate();
            AppLogger.database("updateFilm", "Film ID=" + id + " updated");
        } catch (Exception e) {
            AppLogger.databaseError("updateFilm", e);
        }
    }

    public static void deleteFilm(int id) {
        String sql = "DELETE FROM films WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            AppLogger.database("deleteFilm", "Film ID=" + id + " deleted");
        } catch (Exception e) {
            AppLogger.databaseError("deleteFilm", e);
        }
    }
}

