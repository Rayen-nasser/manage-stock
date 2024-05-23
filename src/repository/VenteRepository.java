package repository;

import entity.Client;
import entity.Produit;
import entity.Vente;
import util.DatabaseUtil;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VenteRepository {
    private List<Vente> ventes;

    public VenteRepository() {
        ventes = new ArrayList<>();
        loadVentesFromDatabase();
    }
    private void loadVentesFromDatabase() {
        String sql = "SELECT * FROM vente";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int venteId = rs.getInt("id");
                int clientId = rs.getInt("client_id");
                Timestamp dateTimestamp = rs.getTimestamp("date");
                double montant = rs.getDouble("montant");

                // Assuming you have a method to retrieve a Client by ID from the database
                Client client = getClientById(clientId);

                // Assuming you have a method to retrieve all produits for this vente from the database
                Map<Produit, Integer> produits = getProduitsForVente(venteId);

                Vente vente = new Vente();
                vente.setId(venteId);
                vente.setClient(client);
                vente.setDate(dateTimestamp);
                vente.setMontant(montant);
                vente.setProduits(produits);

                ventes.add(vente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void addVente(Vente vente) {
        String venteSql = "INSERT INTO vente (client_id, date, montant) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmtVente = conn.prepareStatement(venteSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmtVente.setInt(1, vente.getClient().getId());
            stmtVente.setDate(2, new java.sql.Date(vente.getDate().getTime()));
            stmtVente.setDouble(3, vente.getMontant());
            stmtVente.executeUpdate();

            try (ResultSet rs = stmtVente.getGeneratedKeys()) {
                if (rs.next()) {
                    vente.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void saveProduitsForVente(Vente vente) {
        String sql = "INSERT INTO vente_produit (vente_id, produit_id, quantite) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            Map<Produit, Integer> produits = vente.getProduits();
            System.out.println("Number of produits in vente: " + produits.size());
            for (Map.Entry<Produit, Integer> entry : produits.entrySet()) {
                Produit produit = entry.getKey();
                int quantite = entry.getValue();
                stmt.setInt(1, vente.getId());
                stmt.setInt(2, produit.getId());
                stmt.setInt(3, quantite);
                stmt.executeUpdate();

                // Update stock for each produit
                updateProduitStock(produit.getId(), quantite);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void updateProduitStock(int produitId, int quantite) {
        String sql = "UPDATE produit SET quantite_en_stock = quantite_en_stock - ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantite);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Map.Entry<String, Long>> getTop5ProductsBySales() {
        Map<String, Long> productSalesCount = new HashMap<>();

        for (Vente vente : ventes) {
            for (Map.Entry<Produit, Integer> entry : vente.getProduits().entrySet()) {
                Produit produit = entry.getKey();
                String productName = produit.getNom();
                long quantity = entry.getValue();

                productSalesCount.put(productName, productSalesCount.getOrDefault(productName, 0L) + quantity);
            }
        }

        return productSalesCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
    public List<Vente> getSalesInLast24Hours() {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);

        return ventes.stream()
                .filter(vente -> vente.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isAfter(twentyFourHoursAgo))
                .collect(Collectors.toList());
    }
    private Client getClientById(int clientId) {
        Client client = null;
        String sql = "SELECT * FROM client WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    client = new Client();
                    client.setId(rs.getInt("id"));
                    client.setNom(rs.getString("nom"));
                    client.setEmail(rs.getString("email"));
                    client.setAdresse(rs.getString("adresse"));
                    // Set other client properties as needed
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return client;
    }
    private Map<Produit, Integer> getProduitsForVente(int venteId) {
        Map<Produit, Integer> produitsMap = new HashMap<>();
        String sql = "SELECT produit_id, quantite FROM vente_produit WHERE vente_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, venteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int produitId = rs.getInt("produit_id");
                    int quantite = rs.getInt("quantite");
                    Produit produit = getProduitById(produitId);
                    if (produit != null) {
                        produitsMap.put(produit, quantite);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produitsMap;
    }
    private Produit getProduitById(int produitId) {
        Produit produit = null;
        String sql = "SELECT * FROM produit WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produitId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    produit = new Produit();
                    produit.setId(rs.getInt("id"));
                    produit.setNom(rs.getString("nom"));
                    produit.setDescription(rs.getString("description"));
                    produit.setPrix(rs.getDouble("prix"));
                    // Set other produit properties as needed
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produit;
    }
    public BigDecimal calculateStockBudget() {
        BigDecimal totalBudget = BigDecimal.ZERO;
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT SUM(prix * quantite_en_stock) AS total_sum FROM Produit";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                BigDecimal totalSum = resultSet.getBigDecimal("total_sum");
                totalBudget = totalSum;
            }


            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalBudget;
    }
    public BigDecimal calculateTotalSalesToday() {
        BigDecimal totalSales = BigDecimal.ZERO;
        LocalDate today = LocalDate.now();

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT SUM(montant) AS total_sales FROM Vente WHERE date = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setDate(1, java.sql.Date.valueOf(today));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                totalSales = resultSet.getBigDecimal("total_sales");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalSales;
    }
}
