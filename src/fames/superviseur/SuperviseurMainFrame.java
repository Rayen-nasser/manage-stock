package fames.superviseur;

import entity.Utilisateur;
import repository.UtilisateurRepository;
import repository.VenteRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SuperviseurMainFrame extends JFrame {
    private UtilisateurRepository utilisateurRepository;
    private JTable userTable;
    private VenteRepository venteRepository;

    public SuperviseurMainFrame(Utilisateur utilisateur) {
        utilisateurRepository = new UtilisateurRepository();
        venteRepository = new VenteRepository();

        setTitle("Supervisor Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        createMenuBar();
        createUI();

        displayUserInfo(utilisateur);

        loadUsers();
    }

    private void displayUserInfo(Utilisateur utilisateur) {
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new GridLayout(3, 1));

        JLabel usernameLabel = new JLabel("Username: " + utilisateur.getUsername());
        JLabel roleLabel = new JLabel("Role: " + utilisateur.getRole());

        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(roleLabel);

        add(userInfoPanel, BorderLayout.NORTH);
    }
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu gestionnaireMenu = new JMenu("Gestionnaire");
        menuBar.add(gestionnaireMenu);

        JMenuItem addManagerMenuItem = new JMenuItem("Add");
        JMenuItem editManagerMenuItem = new JMenuItem("Edit");
        JMenuItem deleteManagerMenuItem = new JMenuItem("Delete");
        gestionnaireMenu.add(addManagerMenuItem);
        gestionnaireMenu.add(editManagerMenuItem);
        gestionnaireMenu.add(deleteManagerMenuItem);

        JMenu statistiqueMenu = new JMenu("Statistique");
        menuBar.add(statistiqueMenu);

        JMenuItem salesTodayMenuItem = new JMenuItem("Ventes Aujourd'hui");
        JMenuItem stockBudgetMenuItem = new JMenuItem("Budget de Stock");
        JMenuItem top5ProductsMenuItem = new JMenuItem("Top 5 Produits par Ventes");
        statistiqueMenu.add(salesTodayMenuItem);
        statistiqueMenu.add(stockBudgetMenuItem);
        statistiqueMenu.add(top5ProductsMenuItem);

        addManagerMenuItem.addActionListener(this::addUtilisateur);
        editManagerMenuItem.addActionListener(this::editUtilisateur);
        deleteManagerMenuItem.addActionListener(this::deleteUtilisateur);
        salesTodayMenuItem.addActionListener(this::showSalesToday);
        stockBudgetMenuItem.addActionListener(this::showStockBudget);
        top5ProductsMenuItem.addActionListener(this::showTop5Products);
    }

    private void createUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);

        userTable = new JTable(new DefaultTableModel(new Object[]{"ID", "Username", "Role"}, 0));
        userTable.setRowHeight(30);
        userTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {{
            setHorizontalAlignment(JLabel.CENTER);
        }});
        JScrollPane scrollPane = new JScrollPane(userTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private void loadUsers() {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);

        List<Utilisateur> utilisateurs = utilisateurRepository.getAllUtilisateurs();
        for (Utilisateur utilisateur : utilisateurs) {
            model.addRow(new Object[]{utilisateur.getId(), utilisateur.getUsername(), utilisateur.getRole()});
        }
    }

    private void addUtilisateur(ActionEvent e) {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JTextField();

        String[] roles = {"Gestionnaire"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleComboBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setUsername(usernameField.getText());
            utilisateur.setPassword(passwordField.getText());
            utilisateur.setRole((String) roleComboBox.getSelectedItem());
            utilisateurRepository.addUtilisateur(utilisateur);
            loadUsers();
        }
    }

    private void editUtilisateur(ActionEvent e) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) userTable.getValueAt(selectedRow, 0);

            Utilisateur utilisateur = utilisateurRepository.getUtilisateurById(userId);
            if (utilisateur != null) {
                // Display a dialog to input new information
                JTextField usernameField = new JTextField(utilisateur.getUsername());
                JTextField passwordField = new JTextField(utilisateur.getPassword());
                JTextField roleField = new JTextField(utilisateur.getRole());

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Username:"));
                panel.add(usernameField);
                panel.add(new JLabel("Password:"));
                panel.add(passwordField);
                panel.add(new JLabel("Role:"));
                panel.add(roleField);

                int result = JOptionPane.showConfirmDialog(null, panel, "Edit User",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    // Update the utilisateur object with new information
                    utilisateur.setUsername(usernameField.getText());
                    utilisateur.setPassword(passwordField.getText());
                    utilisateur.setRole(roleField.getText());

                    // Update the utilisateur in the database
                    utilisateurRepository.updateUtilisateur(utilisateur);

                    // Refresh user list
                    loadUsers();
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.");
        }
    }

    private void deleteUtilisateur(ActionEvent e) {
        // Implement delete user functionality
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) userTable.getValueAt(selectedRow, 0);
            utilisateurRepository.deleteUtilisateur(userId);
            loadUsers(); // Refresh user list
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.");
        }
    }

    private void showSalesToday(ActionEvent e) {
        BigDecimal totalSalesToday = venteRepository.calculateTotalSalesToday();
        JOptionPane.showMessageDialog(this, "Ventes aujourd'hui : " + totalSalesToday);
    }

    private void showStockBudget(ActionEvent e) {
        BigDecimal stockBudget = venteRepository.calculateStockBudget();
        JOptionPane.showMessageDialog(this, "Budget de Stock : " + stockBudget);
    }

    private void showTop5Products(ActionEvent e) {
        // Call the method from VenteRepository to get top 5 products by sales
        List<Map.Entry<String, Long>> top5Products = venteRepository.getTop5ProductsBySales();

        // Display the statistics
        StringBuilder message = new StringBuilder("Top 5 Produits par Ventes:\n");
        int rank = 1;
        for (Map.Entry<String, Long> entry : top5Products) {
            message.append(rank++).append(". ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        JOptionPane.showMessageDialog(this, message.toString());
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            Utilisateur supervisor = new Utilisateur();
//            supervisor.setUsername("Supervisor");
//            supervisor.setRole("Superviseur");
//            new SuperviseurMainFrame(supervisor).setVisible(true);
//        });
//    }
}
