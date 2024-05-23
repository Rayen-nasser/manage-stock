package fames.gestionner;

import entity.Utilisateur;
import entity.Vente;
import repository.VenteRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

public class GestionnaireMainFrame extends JFrame {
    private Utilisateur utilisateur;
    private VenteRepository venteRepository;

    public GestionnaireMainFrame(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.venteRepository = new VenteRepository();

        setTitle("Gestion de produits, clients et ventes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu produitMenu = new JMenu("Produits");
        menuBar.add(produitMenu);

        produitMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ProduitFrame().setVisible(true);
            }
        });

        JMenu categorieMenu = new JMenu("Categories");
        menuBar.add(categorieMenu);

        categorieMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new CategorieFrame().setVisible(true);
            }
        });


        JMenu clientMenu = new JMenu("Clients");
        menuBar.add(clientMenu);

        clientMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new ClientFrame().setVisible(true);
            }
        });


        JMenu venteMenu = new JMenu("Ventes");
        menuBar.add(venteMenu);

        venteMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new VenteFrame().setVisible(true);
            }
        });


        displayVentes();
    }
    private void displayVentes() {
        List<Vente> ventesLast24Hours = venteRepository.getSalesInLast24Hours();

        JPanel ventePanel = createVentePanel(ventesLast24Hours);

        getContentPane().setLayout(new GridLayout(2, 1));
        getContentPane().add(createTitledPanel(ventePanel, "Ventes des dernieres 24 heures"));
        pack();
    }

    private JPanel createTitledPanel(JPanel panel, String title) {
        TitledBorder border = BorderFactory.createTitledBorder(title);
        border.setTitleJustification(TitledBorder.CENTER);
        panel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(20, 20, 20, 20), border));
        return panel;
    }

    private JPanel createVentePanel(List<Vente> ventes) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        String[] columnNames = {"Client", "Date", "Total"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Vente vente : ventes) {
            Object[] rowData = {vente.getClient().getNom(), vente.getDate(), vente.getMontant()};
            model.addRow(rowData);
        }

        JTable table = new JTable(model);
        table.setRowHeight(20);
        table.setPreferredScrollableViewportSize(new Dimension(380, 150));

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
