package fames.gestionner;

import entity.Client;
import entity.Produit;
import entity.Vente;
import repository.ClientRepository;
import repository.ProduitRepository;
import repository.VenteRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenteFrame extends JFrame {
    private JComboBox<Client> clientComboBox;
    private JComboBox<Produit> produitComboBox;
    private JTextField quantiteField;
    private JTable produitsTable;
    private JButton ajouterButton;
    private JButton validerButton;

    private Vente vente;
    private Map<Produit, Integer> produitsMap = new HashMap<>();

    private ProduitRepository produitRepository;
    private VenteRepository venteRepository;

    public VenteFrame() {
        produitRepository = new ProduitRepository();
        venteRepository = new VenteRepository();

        setTitle("Nouvelle Vente");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel clientLabel = new JLabel("Client:");
        clientLabel.setBounds(10, 10, 80, 25);
        panel.add(clientLabel);

        List<Client> clients = new ClientRepository().getAllClients();
        clientComboBox = new JComboBox<>(clients.toArray(new Client[0]));
        clientComboBox.setBounds(100, 10, 160, 25);
        panel.add(clientComboBox);

        JLabel produitLabel = new JLabel("Produit:");
        produitLabel.setBounds(10, 40, 80, 25);
        panel.add(produitLabel);

        List<Produit> produits = produitRepository.getAllProduits();
        produitComboBox = new JComboBox<>(produits.toArray(new Produit[0]));
        produitComboBox.setBounds(100, 40, 160, 25);
        panel.add(produitComboBox);

        JLabel quantiteLabel = new JLabel("Quantité:");
        quantiteLabel.setBounds(10, 70, 80, 25);
        panel.add(quantiteLabel);

        quantiteField = new JTextField(20);
        quantiteField.setBounds(100, 70, 160, 25);
        panel.add(quantiteField);

        ajouterButton = new JButton("Ajouter");
        ajouterButton.setBounds(270, 70, 80, 25);
        panel.add(ajouterButton);

        produitsMap = new HashMap<>();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Produit");
        model.addColumn("Quantité");
        produitsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(produitsTable);
        scrollPane.setBounds(10, 100, 400, 200);
        panel.add(scrollPane);

        validerButton = new JButton("Valider Vente");
        validerButton.setBounds(420, 310, 150, 25);
        panel.add(validerButton);

        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajouterProduit();
            }
        });

        validerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validerVente();
            }
        });
    }

    private void ajouterProduit() {
        Produit produit = (Produit) produitComboBox.getSelectedItem();
        int quantite = Integer.parseInt(quantiteField.getText());

        // Update quantité in produitsMap
        produitsMap.put(produit, quantite);

        // Refresh table
        refreshTable();
    }


    private void refreshTable() {
        DefaultTableModel model = (DefaultTableModel) produitsTable.getModel();
        model.setRowCount(0); // Clear table

        for (Map.Entry<Produit, Integer> entry : produitsMap.entrySet()) {
            Produit produit = entry.getKey();
            int quantite = entry.getValue();
            model.addRow(new Object[]{produit.getNom(), quantite});
        }
    }

    private void validerVente() {
        // Create new Vente object
        vente = new Vente();
        vente.setClient((Client) clientComboBox.getSelectedItem());
        vente.setDate(new Date());
        Map<Produit, Integer> produitIntegerMap = new HashMap<>();

        double montantTotal = 0;

        for (Map.Entry<Produit, Integer> entry : produitsMap.entrySet()) {
            Produit produit = entry.getKey();
            int quantite = entry.getValue();
            montantTotal += produit.getPrix() * quantite;
            produitIntegerMap.put(produit, quantite);
        }

        vente.setProduits(produitIntegerMap);
        vente.setMontant(montantTotal);

        // Save the vente to database
        venteRepository.addVente(vente);

        // Save produits for this vente
        venteRepository.saveProduitsForVente(vente);

        // Show success message
        JOptionPane.showMessageDialog(this, "Vente enregistrée avec succès!");

        // Clear fields and table
        produitsMap.clear();
        refreshTable();
    }
}
