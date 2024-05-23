package fames.gestionner;

import entity.Categorie;
import repository.CategorieRepository;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CategorieFrame extends JFrame {
    private JTextField nomField;
    private JTextField descriptionField;
    private JTable categorieTable;
    private CategorieRepository categorieRepository;

    public CategorieFrame() {
        categorieRepository = new CategorieRepository();

        setTitle("Gérer Catégories");
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

        JButton ajouterButton = new JButton("Ajouter");
        ajouterButton.setBounds(10, 80, 100, 25);
        panel.add(ajouterButton);

        JButton modifierButton = new JButton("Modifier");
        modifierButton.setBounds(120, 80, 100, 25);
        panel.add(modifierButton);

        JButton supprimerButton = new JButton("Supprimer");
        supprimerButton.setBounds(230, 80, 100, 25);
        panel.add(supprimerButton);

        List<Categorie> categories = categorieRepository.getAllCategories();
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Nom");
        model.addColumn("Description");
        for (Categorie categorie : categories) {
            model.addRow(new Object[]{
                    categorie.getId(),
                    categorie.getNom(),
                    categorie.getDescription()
            });
        }
        categorieTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(categorieTable);
        scrollPane.setBounds(10, 120, 560, 200);
        panel.add(scrollPane);


        categorieTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && categorieTable.getSelectedRow() != -1) {
                    int selectedRow = categorieTable.getSelectedRow();
                    int categorieId = (int) categorieTable.getValueAt(selectedRow, 0);
                    String nom = (String) categorieTable.getValueAt(selectedRow, 1);
                    String description = (String) categorieTable.getValueAt(selectedRow, 2);

                    nomField.setText(nom);
                    descriptionField.setText(description);
                }
            }
        });

        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajouterCategorie();
            }
        });

        modifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifierCategorie();
            }
        });

        supprimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supprimerCategorie();
            }
        });
    }

    private void ajouterCategorie() {
        String nom = nomField.getText();
        String description = descriptionField.getText();

        Categorie categorie = new Categorie();
        categorie.setNom(nom);
        categorie.setDescription(description);

        categorieRepository.addCategorie(categorie);

        // Update JTable with new category
        DefaultTableModel model = (DefaultTableModel) categorieTable.getModel();
        model.addRow(new Object[]{
                categorie.getId(),
                categorie.getNom(),
                categorie.getDescription()
        });

        JOptionPane.showMessageDialog(this, "Catégorie ajoutée avec succès!");
        loadCategories();
    }

    private void modifierCategorie() {
        int selectedRow = categorieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une catégorie à modifier.");
            return;
        }

        int categorieId = (int) categorieTable.getValueAt(selectedRow, 0);
        String nom = nomField.getText();
        String description = descriptionField.getText();

        Categorie categorie = new Categorie();
        categorie.setId(categorieId);
        categorie.setNom(nom);
        categorie.setDescription(description);

        categorieRepository.updateCategorie(categorie);

        // Update the corresponding row in the JTable
        categorieTable.setValueAt(nom, selectedRow, 1);
        categorieTable.setValueAt(description, selectedRow, 2);

        JOptionPane.showMessageDialog(this, "Catégorie modifiée avec succès!");
        loadCategories();
    }

    private void supprimerCategorie() {
        int selectedRow = categorieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une catégorie à supprimer.");
            return;
        }

        int categorieId = (int) categorieTable.getValueAt(selectedRow, 0);
        categorieRepository.deleteCategorie(categorieId);

        DefaultTableModel model = (DefaultTableModel) categorieTable.getModel();
        model.removeRow(selectedRow);

        JOptionPane.showMessageDialog(this, "Catégorie supprimée avec succès!");
        loadCategories();
    }

    private void loadCategories() {
        List<Categorie> categories = categorieRepository.getAllCategories();

        DefaultTableModel model = (DefaultTableModel) categorieTable.getModel();
        model.setRowCount(0);

        // Add categories to the table
        for (Categorie categorie : categories) {
            model.addRow(new Object[]{
                    categorie.getId(),
                    categorie.getNom(),
                    categorie.getDescription()
            });
        }

        // Clear input fields
        nomField.setText("");
        descriptionField.setText("");
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new CategorieFrame().setVisible(true);
//            }
//        });
//    }
}
