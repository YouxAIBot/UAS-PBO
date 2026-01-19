package projekPBO.projek.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import projekPBO.projek.Consumption;
import projekPBO.projek.db.DBConnection;
import projekPBO.projek.util.AppLogger;

public class ConsumptionDAO {

    public static List<Consumption> getAll() {
        List<Consumption> list = new ArrayList<>();
        String sql = "SELECT * FROM consumptions";

        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Consumption(
                        rs.getString("name"),
                        rs.getInt("price")
                ));
            }
            AppLogger.database("getAll", "Loaded " + list.size() + " consumptions");
        } catch (Exception e) {
            AppLogger.databaseError("getAll", e);
        }
        return list;
    }
}

