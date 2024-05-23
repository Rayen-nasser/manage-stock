package repository;

import entity.Client;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {
    public void addClient(Client client) {
        String sql = "INSERT INTO client (nom, email, adresse) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getAdresse());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM client";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setNom(rs.getString("nom"));
                client.setEmail(rs.getString("email"));
                client.setAdresse(rs.getString("adresse"));
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }
    public void updateClient(Client client) {
        String sql = "UPDATE client SET nom = ?, email = ?, adresse = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, client.getNom());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getAdresse());
            stmt.setInt(4, client.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void deleteClient(int id) {
        String sql = "DELETE FROM client WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
