package fames.gestionner;

import entity.Categorie;
import entity.Produit;
import repository.CategorieRepository;
import repository.ProduitRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ProduitFrame extends JFrame {
    private JTextField nomField;
    private JTextField descriptionField;
    private JTextField prixField;
    private JTextField quantiteField;
    private JComboBox<Categorie> categorieComboBox;
    private JTable produitTable;
    private ProduitRepository produitRepository;
    private CategorieRepository categorieRepository;

    public ProduitFrame() {
        produitRepository = new ProduitRepository();
        categorieRepository = new CategorieRepository();

        setTitle("Gérer Produits");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel nomLabel = new JLabel("Nom:");
        nomLabel.setBounds(10, 10, 80, 25);
        panel.add(nomLabel);

        nomField = new JTextField(20);
        nomField.setBounds(100, 10, 160, 25);
        panel.add(nomField);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(10, 40, 80, 25);
        panel.add(descriptionLabel);

        descriptionField = new JTextField(20);
        descriptionField.setBounds(100, 40, 160, 25);
        panel.add(descriptionField);

        JLabel prixLabel = new JLabel("Prix:");
        prixLabel.setBounds(10, 70, 80, 25);
        panel.add(prixLabel);

        prixField = new JTextField(20);
        prixField.setBounds(100, 70, 160, 25);
        panel.add(prixField);

        JLabel quantiteLabel = new JLabel("Quantité:");
        quantiteLabel.setBounds(10, 100, 80, 25);
        panel.add(quantiteLabel);

        quantiteField = new JTextField(20);
        quantiteField.setBounds(100, 100, 160, 25);
        panel.add(quantiteField);

        JLabel categorieLabel = new JLabel("Catégorie:");
        categorieLabel.setBounds(10, 130, 80, 25);
        panel.add(categorieLabel);

        List<Categorie> categories = categorieRepository.getAllCategories();
        categorieComboBox = new JComboBox<>(categories.toArray(new Categorie[0]));
        categorieComboBox.setBounds(100, 130, 160, 25);
        panel.add(categorieComboBox);

        JButton ajouterButton = new JButton("Ajouter");
        ajouterButton.setBounds(100, 170, 80, 25);
        panel.add(ajouterButton);

        List<Produit> produits = produitRepository.getAllProduits();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nom");
        model.addColumn("Description");
        model.addColumn("Prix");
        model.addColumn("Quantité");
        model.addColumn("Catégorie");
        for (Produit produit : produits) {
            model.addRow(new Object[]{
                    produit.getId(),
                    produit.getNom(),
                    produit.getDescription(),
                    produit.getPrix(),
                    produit.getQuantiteEnStock(),
                    produit.getCategorie().getNom()
            });
        }
        produitTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(produitTable);
        scrollPane.setBounds(10, 200, 560, 150);
        panel.add(scrollPane);


        // Add a selection listener to populate fields when a row is selected
        produitTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && produitTable.getSelectedRow() != -1) {
                int selectedRow = produitTable.getSelectedRow();
                int produitId = (int) produitTable.getValueAt(selectedRow, 0);
                String nom = (String) produitTable.getValueAt(selectedRow, 1);
                String description = (String) produitTable.getValueAt(selectedRow, 2);
                Double prix = (Double) produitTable.getValueAt(selectedRow, 3);
                Integer quantity = (Integer) produitTable.getValueAt(selectedRow, 4);
                String categorieNom = (String) produitTable.getValueAt(selectedRow, 5);

                // Populate fields with the selected product's data
                nomField.setText(nom);
                descriptionField.setText(description);
                prixField.setText(String.valueOf(prix));
                quantiteField.setText(String.valueOf(quantity));
                categorieComboBox.setSelectedItem(categorieNom);
            }
        });

        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajouterProduit();
            }
        });

        JButton updateButton = new JButton("Modifier");
        updateButton.setBounds(200, 170, 80, 25);
        panel.add(updateButton);

        JButton deleteButton = new JButton("Supprimer");
        deleteButton.setBounds(300, 170, 100, 25);
        panel.add(deleteButton);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProduit();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProduit();
            }
        });
    }

    private void ajouterProduit() {
        String nom = nomField.getText();
        String description = descriptionField.getText();
        double prix = Double.parseDouble(prixField.getText());
        int quantite = Integer.parseInt(quantiteField.getText());
        Categorie categorie = (Categorie) categorieComboBox.getSelectedItem();

        Produit produit = new Produit();
        produit.setNom(nom);
        produit.setDescription(description);
        produit.setPrix(prix);
        produit.setQuantiteEnStock(quantite);
        produit.setCategorie(categorie);

        produitRepository.addProduit(produit);

        // Update JTable with new product
        DefaultTableModel model = (DefaultTableModel) produitTable.getModel();
        model.addRow(new Object[]{
                produit.getId(),
                produit.getNom(),
                produit.getDescription(),
                produit.getPrix(),
                produit.getQuantiteEnStock(),
                produit.getCategorie().getNom()
        });

        JOptionPane.showMessageDialog(this, "Produit ajouté avec succès!");
        loadProduits();
    }

    private void updateProduit() {
        int selectedRow = produitTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à mettre à jour.");
            return;
        }

        Produit produit = new Produit();
        produit.setId((int) produitTable.getValueAt(selectedRow, 0)); // ID is in the first column
        produit.setNom(nomField.getText());
        produit.setDescription(descriptionField.getText());
        produit.setPrix(Double.parseDouble(prixField.getText()));
        produit.setQuantiteEnStock(Integer.parseInt(quantiteField.getText()));
        produit.setCategorie((Categorie) categorieComboBox.getSelectedItem());

        produitRepository.updateProduit(produit);

        // Update the corresponding row in the JTable
        produitTable.setValueAt(produit.getNom(), selectedRow, 1); // Update Nom column
        produitTable.setValueAt(produit.getDescription(), selectedRow, 2); // Update Description column
        produitTable.setValueAt(produit.getPrix(), selectedRow, 3); // Update Prix column
        produitTable.setValueAt(produit.getQuantiteEnStock(), selectedRow, 4); // Update Quantité column
        produitTable.setValueAt(produit.getCategorie().getNom(), selectedRow, 5); // Update Catégorie column

        JOptionPane.showMessageDialog(this, "Produit mis à jour avec succès!");
        loadProduits();
    }

    private void deleteProduit() {
        int selectedRow = produitTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit à supprimer.");
            return;
        }

        int id = (int) produitTable.getValueAt(selectedRow, 0); // ID is in the first column

        produitRepository.deleteProduit(id);

        // Remove the corresponding row from the JTable
        DefaultTableModel model = (DefaultTableModel) produitTable.getModel();
        model.removeRow(selectedRow);

        JOptionPane.showMessageDialog(this, "Produit supprimé avec succès!");
        loadProduits();
    }
    private void loadProduits() {
        List<Produit> produits = produitRepository.getAllProduits();

        DefaultTableModel model = (DefaultTableModel) produitTable.getModel();
        model.setRowCount(0);

        // Add products to the table
        for (Produit produit : produits) {
            model.addRow(new Object[]{
                    produit.getId(),
                    produit.getNom(),
                    produit.getDescription(),
                    produit.getPrix(),
                    produit.getQuantiteEnStock(),
                    produit.getCategorie().getNom()
            });
        }

        // Clear input fields
        nomField.setText("");
        descriptionField.setText("");
        prixField.setText("");
        quantiteField.setText("");
        categorieComboBox.setSelectedIndex(-1);
    }

}

