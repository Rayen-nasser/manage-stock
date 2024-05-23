package repository;

import entity.Utilisateur;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurRepository {
    public void addUtilisateur(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateur (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getUsername());
            stmt.setString(2, utilisateur.getPassword());
            stmt.setString(3, utilisateur.getRole());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Utilisateur> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setId(rs.getInt("id"));
                utilisateur.setUsername(rs.getString("username"));
                utilisateur.setPassword(rs.getString("password"));
                utilisateur.setRole(rs.getString("role"));
                utilisateurs.add(utilisateur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }
    public void updateUtilisateur(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET username = ?, password = ?, role = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getUsername());
            stmt.setString(2, utilisateur.getPassword());
            stmt.setString(3, utilisateur.getRole());
            stmt.setInt(4, utilisateur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteUtilisateur(int id) {
        String sql = "DELETE FROM utilisateur WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Utilisateur getUtilisateurByUsername(String username) {
        String sql = "SELECT * FROM utilisateur WHERE username = ?";
        Utilisateur utilisateur = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    utilisateur = new Utilisateur();
                    utilisateur.setId(rs.getInt("id"));
                    utilisateur.setUsername(rs.getString("username"));
                    utilisateur.setPassword(rs.getString("password"));
                    utilisateur.setRole(rs.getString("role"));
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateur;
    }
    public Utilisateur getUtilisateurById(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        Utilisateur utilisateur = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    utilisateur = new Utilisateur();
                    utilisateur.setId(rs.getInt("id"));
                    utilisateur.setUsername(rs.getString("username"));
                    utilisateur.setPassword(rs.getString("password"));
                    utilisateur.setRole(rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateur;
    }
}
