package repository;

import entity.Categorie;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategorieRepository {
    public void addCategorie(Categorie categorie) {
        String sql = "INSERT INTO categorie (nom, description) VALUES (?, ?)";

        // Using a try-with-resources statement to ensure the Connection and PreparedStatement are closed automatically
        try (Connection conn = DatabaseUtil.getConnection();
             // Preparing the SQL statement with placeholders for the values
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Categorie> getAllCategories() {
        List<Categorie> categories = new ArrayList<>();
        String sql = "SELECT * FROM categorie";
        // Using a try-with-resources statement to ensure the Connection, Statement, and ResultSet are closed automatically
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("id"));
                categorie.setNom(rs.getString("nom"));
                categorie.setDescription(rs.getString("description"));
                categories.add(categorie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }
    public void updateCategorie(Categorie categorie) {
        String sql = "UPDATE categorie SET nom = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categorie.getNom());
            stmt.setString(2, categorie.getDescription());
            stmt.setInt(3, categorie.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteCategorie(int id) {
        String sql = "DELETE FROM categorie WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
