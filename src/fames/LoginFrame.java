package fames;

import entity.Utilisateur;
import fames.gestionner.GestionnaireMainFrame;
import fames.superviseur.SuperviseurMainFrame;
import repository.UtilisateurRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame{
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UtilisateurRepository utilisateurDAO;

    public LoginFrame() {
        utilisateurDAO = new UtilisateurRepository();

        setTitle("Login");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 10, 80, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 10, 160, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 40, 160, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(100, 80, 80, 25);
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        Utilisateur utilisateur = utilisateurDAO.getUtilisateurByUsername(username);
        if (utilisateur != null && utilisateur.getPassword().equals(password)) {
            JOptionPane.showMessageDialog(this, "Login successful!");
            dispose();
            if (utilisateur.getRole().equals("Superviseur") ) {
                new SuperviseurMainFrame(utilisateur).setVisible(true);
            } else if (utilisateur.getRole().equals("Gestionnaire")) {
                new GestionnaireMainFrame(utilisateur).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }
}
