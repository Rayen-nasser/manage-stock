package fames.gestionner;

import entity.Client;
import repository.ClientRepository;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ClientFrame extends JFrame {
    private JTextField nomField;
    private JTextField emailField;
    private JTextField adresseField;
    private ClientRepository clientRepository;
    private JTable clientTable;

    public ClientFrame() {
        clientRepository = new ClientRepository();

        setTitle("Gérer Clients");
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

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 40, 80, 25);
        panel.add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(100, 40, 160, 25);
        panel.add(emailField);

        JLabel adresseLabel = new JLabel("Adresse:");
        adresseLabel.setBounds(10, 70, 80, 25);
        panel.add(adresseLabel);

        adresseField = new JTextField(20);
        adresseField.setBounds(100, 70, 160, 25);
        panel.add(adresseField);

        JButton ajouterButton = new JButton("Ajouter");
        ajouterButton.setBounds(100, 110, 80, 25);
        panel.add(ajouterButton);

        JButton modifierButton = new JButton("Modifier");
        modifierButton.setBounds(190, 110, 80, 25);
        panel.add(modifierButton);

        JButton supprimerButton = new JButton("Supprimer");
        supprimerButton.setBounds(100, 140, 160, 25);
        panel.add(supprimerButton);

        // Initialize clientTable
        DefaultTableModel model = new DefaultTableModel();
        List<Client> clients = clientRepository.getAllClients();
        model.addColumn("ID");
        model.addColumn("Nom");
        model.addColumn("Email");
        model.addColumn("Adresse");

        for (Client client : clients) {
            model.addRow(new Object[]{
                    client.getId(),
                    client.getNom(),
                    client.getEmail(),
                    client.getAdresse(),
            });
        }

        clientTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBounds(10, 200, 560, 150);
        panel.add(scrollPane);


        clientTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && clientTable.getSelectedRow() != -1) {
                    int selectedRow = clientTable.getSelectedRow();
                    int clientId = (int) clientTable.getValueAt(selectedRow, 0);
                    String nom = (String) clientTable.getValueAt(selectedRow, 1);
                    String email = (String) clientTable.getValueAt(selectedRow, 2);
                    String adresse = (String) clientTable.getValueAt(selectedRow, 3);

                    nomField.setText(nom);
                    emailField.setText(email);
                    adresseField.setText(adresse);
                }
            }
        });

        ajouterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ajouterClient();
            }
        });

        modifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifierClient();
            }
        });

        supprimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                supprimerClient();
            }
        });
    }

    private void loadClients() {
        List<Client> clients = clientRepository.getAllClients();

        DefaultTableModel model = (DefaultTableModel) clientTable.getModel();
        model.setRowCount(0);

        // Add clients to the table
        for (Client client : clients) {
            model.addRow(new Object[]{
                    client.getId(),
                    client.getNom(),
                    client.getEmail(),
                    client.getAdresse()
            });
        }

        nomField.setText("");
        emailField.setText("");
        adresseField.setText("");
    }

    private void ajouterClient() {
        String nom = nomField.getText();
        String email = emailField.getText();
        String adresse = adresseField.getText();

        Client client = new Client();
        client.setNom(nom);
        client.setEmail(email);
        client.setAdresse(adresse);

        clientRepository.addClient(client);
        JOptionPane.showMessageDialog(this, "Client ajouté avec succès!");
        loadClients();
    }

    private void modifierClient() {
        int selectedRow = clientTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client à modifier.");
            return;
        }

        int clientId = (int) clientTable.getValueAt(selectedRow, 0);

        String nom = nomField.getText();
        String email = emailField.getText();
        String adresse = adresseField.getText();

        Client client = new Client();
        client.setId(clientId);
        client.setNom(nom);
        client.setEmail(email);
        client.setAdresse(adresse);

        clientRepository.updateClient(client);

        JOptionPane.showMessageDialog(this, "Client modifié avec succès!");

        clientTable.setValueAt(nom, selectedRow, 1);
        clientTable.setValueAt(email, selectedRow, 2);
        clientTable.setValueAt(adresse, selectedRow, 3);
    }

    private void supprimerClient() {
        // Get the index of the selected row in the table
        int selectedRow = clientTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client à supprimer.");
            return;
        }

        // Retrieve the client ID from the selected row
        int clientId = (int) clientTable.getValueAt(selectedRow, 0);

        // Call the deleteClient method from the clientRepository
        clientRepository.deleteClient(clientId);

        loadClients();

        // Show a success message
        JOptionPane.showMessageDialog(this, "Client supprimé avec succès!");
    }

}
