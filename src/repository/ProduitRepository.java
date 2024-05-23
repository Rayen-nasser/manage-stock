package repository;

import entity.Categorie;
import entity.Produit;
import util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitRepository {
    public void addProduit(Produit produit) {
        String sql = "INSERT INTO produit (nom, description, prix, quantite_en_stock, categorie_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getQuantiteEnStock());
            stmt.setInt(5, produit.getCategorie().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Produit> getAllProduits() {
        List<Produit> produits = new ArrayList<>();
        String sql = "SELECT p.*, c.nom AS categorie_nom FROM produit p JOIN categorie c ON p.categorie_id = c.id";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produit produit = new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setQuantiteEnStock(rs.getInt("quantite_en_stock"));

                // Creating the associated Categorie object
                Categorie categorie = new Categorie();
                categorie.setId(rs.getInt("categorie_id"));
                categorie.setNom(rs.getString("categorie_nom"));

                produit.setCategorie(categorie);

                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    public void updateProduit(Produit produit) {
        String sql = "UPDATE produit SET nom = ?, description = ?, prix = ?, quantite_en_stock = ?, categorie_id = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, produit.getNom());
            stmt.setString(2, produit.getDescription());
            stmt.setDouble(3, produit.getPrix());
            stmt.setInt(4, produit.getQuantiteEnStock());
            stmt.setInt(5, produit.getCategorie().getId());
            stmt.setInt(6, produit.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduit(int id) {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
