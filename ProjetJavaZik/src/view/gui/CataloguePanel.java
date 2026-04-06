package view.gui;

import controller.GUIController;
import model.*;
import model.exceptions.LimiteEcoutesAtteinte;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Panel de navigation et de recherche dans le catalogue musical.
 * Réutilisable en mode visiteur (standalone) et intégré dans AbonnePanel.
 */
public class CataloguePanel extends JPanel {

    private final GUIController ctrl;
    private final MainFrame frame;
    private final boolean embedded; // true = intégré dans AbonnePanel (pas de bouton Retour)

    // Composants principaux
    private JTextField rechercheField;
    private JComboBox<String> filtreCombo;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    private JTextArea detailArea;
    private JLabel lectureLabel;
    private JProgressBar progressBar;
    private JButton btnLire;
    private JButton btnAvis;

    private Object selectionCourante; // Morceau, Album, Artiste ou Groupe sélectionné

    public CataloguePanel(GUIController ctrl, MainFrame frame, boolean embedded) {
        this.ctrl     = ctrl;
        this.frame    = frame;
        this.embedded = embedded;
        buildUI();
    }

    private void buildUI() {
        setBackground(WelcomePanel.BG);
        setLayout(new BorderLayout(0, 0));

        add(buildTopBar(),    BorderLayout.NORTH);
        add(buildCenter(),    BorderLayout.CENTER);
        add(buildPlayerBar(), BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------
    //  Barre de recherche (haut)
    // ---------------------------------------------------------------

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(new Color(38, 38, 50));

        if (!embedded) {
            JButton btnRetour = new JButton("← Accueil");
            styleBtn(btnRetour, WelcomePanel.FG_DIM);
            btnRetour.addActionListener(e -> { ctrl.deconnecter(); frame.showCard(MainFrame.CARD_WELCOME); });
            bar.add(btnRetour);
        }

        rechercheField = new JTextField(20);
        rechercheField.setBackground(new Color(55, 55, 70));
        rechercheField.setForeground(WelcomePanel.FG);
        rechercheField.setCaretColor(WelcomePanel.FG);
        rechercheField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 100)),
                new EmptyBorder(4, 8, 4, 8)));
        rechercheField.addActionListener(e -> rechercher());

        filtreCombo = new JComboBox<>(new String[]{"Tout", "Morceaux", "Albums", "Artistes", "Groupes"});
        filtreCombo.setBackground(new Color(55, 55, 70));
        filtreCombo.setForeground(WelcomePanel.FG);

        JButton btnSearch = new JButton("Rechercher");
        styleBtn(btnSearch, WelcomePanel.ACCENT);
        btnSearch.addActionListener(e -> rechercher());

        JButton btnAll = new JButton("Tout afficher");
        styleBtn(btnAll, WelcomePanel.FG_DIM);
        btnAll.addActionListener(e -> afficherTout());

        bar.add(colorLabel("Recherche :", WelcomePanel.FG_DIM));
        bar.add(rechercheField);
        bar.add(filtreCombo);
        bar.add(btnSearch);
        bar.add(btnAll);
        return bar;
    }

    // ---------------------------------------------------------------
    //  Zone centrale : liste + détail
    // ---------------------------------------------------------------

    private JSplitPane buildCenter() {
        // Table de résultats (gauche)
        tableModel = new DefaultTableModel(new String[]{"Type", "Titre / Nom", "Infos"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(tableModel);
        resultTable.setBackground(new Color(38, 38, 50));
        resultTable.setForeground(WelcomePanel.FG);
        resultTable.setGridColor(new Color(55, 55, 70));
        resultTable.setSelectionBackground(new Color(60, 100, 160));
        resultTable.setSelectionForeground(Color.WHITE);
        resultTable.setRowHeight(24);
        resultTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        resultTable.getTableHeader().setBackground(new Color(45, 45, 58));
        resultTable.getTableHeader().setForeground(WelcomePanel.FG_DIM);
        resultTable.getColumnModel().getColumn(0).setMaxWidth(80);
        resultTable.getColumnModel().getColumn(2).setMaxWidth(140);
        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = resultTable.getSelectedRow();
                if (row >= 0) selectionnerLigne(row);
                if (e.getClickCount() == 2 && row >= 0) actionDouble(row);
            }
        });

        JScrollPane scrollListe = new JScrollPane(resultTable);
        scrollListe.setBackground(new Color(38, 38, 50));
        scrollListe.getViewport().setBackground(new Color(38, 38, 50));
        scrollListe.setBorder(BorderFactory.createEmptyBorder());

        // Panel de détail (droite)
        JPanel detailPanel = new JPanel(new BorderLayout(0, 8));
        detailPanel.setBackground(new Color(38, 38, 50));
        detailPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setBackground(new Color(45, 45, 58));
        detailArea.setForeground(WelcomePanel.FG);
        detailArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        detailArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        JScrollPane scrollDetail = new JScrollPane(detailArea);
        scrollDetail.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80)));

        // Boutons action (visible seulement quand un morceau est sélectionné)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionPanel.setBackground(new Color(38, 38, 50));
        btnLire = new JButton("▶  Écouter");
        styleBtn(btnLire, WelcomePanel.ACCENT2);
        btnLire.setVisible(false);
        btnLire.addActionListener(e -> ecouterSelection());

        btnAvis = new JButton("★  Laisser un avis");
        styleBtn(btnAvis, WelcomePanel.ACCENT);
        btnAvis.setVisible(false);
        btnAvis.addActionListener(e -> dialogAvis());

        actionPanel.add(btnLire);
        actionPanel.add(btnAvis);

        detailPanel.add(scrollDetail, BorderLayout.CENTER);
        detailPanel.add(actionPanel,  BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollListe, detailPanel);
        split.setDividerLocation(450);
        split.setDividerSize(4);
        split.setBackground(WelcomePanel.BG);
        split.setBorder(BorderFactory.createEmptyBorder());
        return split;
    }

    // ---------------------------------------------------------------
    //  Barre de lecture (bas) — barre de progression
    // ---------------------------------------------------------------

    private JPanel buildPlayerBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(new Color(22, 22, 30));
        bar.setBorder(new EmptyBorder(6, 16, 6, 16));

        lectureLabel = new JLabel("Aucune lecture en cours");
        lectureLabel.setForeground(WelcomePanel.FG_DIM);
        lectureLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("");
        progressBar.setForeground(WelcomePanel.ACCENT2);
        progressBar.setBackground(new Color(45, 45, 58));

        bar.add(lectureLabel, BorderLayout.WEST);
        bar.add(progressBar,  BorderLayout.CENTER);
        return bar;
    }

    // ---------------------------------------------------------------
    //  Recherche et affichage
    // ---------------------------------------------------------------

    public void rafraichir() {
        afficherTout();
    }

    private void afficherTout() {
        tableModel.setRowCount(0);
        selectionCourante = null;
        detailArea.setText("");
        btnLire.setVisible(false);
        btnAvis.setVisible(false);

        Catalogue cat = ctrl.getCatalogue();
        String filtre = (String) filtreCombo.getSelectedItem();
        if ("Tout".equals(filtre) || "Morceaux".equals(filtre))
            cat.getMorceaux().forEach(m -> tableModel.addRow(new Object[]{"♪ Morceau", m.getTitre(), m.getAuteur() != null ? m.getAuteur().getNom() : ""}));
        if ("Tout".equals(filtre) || "Albums".equals(filtre))
            cat.getAlbums().forEach(a -> tableModel.addRow(new Object[]{"💿 Album", a.getTitre(), a.getAnnee()}));
        if ("Tout".equals(filtre) || "Artistes".equals(filtre))
            cat.getArtistes().forEach(a -> tableModel.addRow(new Object[]{"🎤 Artiste", a.getNom(), ""}));
        if ("Tout".equals(filtre) || "Groupes".equals(filtre))
            cat.getGroupes().forEach(g -> tableModel.addRow(new Object[]{"🎸 Groupe", g.getNom(), g.getMembres().size() + " membres"}));
    }

    private void rechercher() {
        String query = rechercheField.getText().trim();
        if (query.isEmpty()) { afficherTout(); return; }
        tableModel.setRowCount(0);
        selectionCourante = null;
        detailArea.setText("");
        btnLire.setVisible(false);
        btnAvis.setVisible(false);

        Catalogue cat = ctrl.getCatalogue();
        String filtre = (String) filtreCombo.getSelectedItem();
        if ("Tout".equals(filtre) || "Morceaux".equals(filtre))
            cat.rechercherMorceaux(query).forEach(m -> tableModel.addRow(new Object[]{"♪ Morceau", m.getTitre(), m.getAuteur() != null ? m.getAuteur().getNom() : ""}));
        if ("Tout".equals(filtre) || "Albums".equals(filtre))
            cat.rechercherAlbums(query).forEach(a -> tableModel.addRow(new Object[]{"💿 Album", a.getTitre(), a.getAnnee()}));
        if ("Tout".equals(filtre) || "Artistes".equals(filtre))
            cat.rechercherArtistes(query).forEach(a -> tableModel.addRow(new Object[]{"🎤 Artiste", a.getNom(), ""}));
        if ("Tout".equals(filtre) || "Groupes".equals(filtre))
            cat.rechercherGroupes(query).forEach(g -> tableModel.addRow(new Object[]{"🎸 Groupe", g.getNom(), ""}));
    }

    // ---------------------------------------------------------------
    //  Sélection et détails
    // ---------------------------------------------------------------

    private void selectionnerLigne(int row) {
        String type  = (String) tableModel.getValueAt(row, 0);
        String nom   = (String) tableModel.getValueAt(row, 1);
        Catalogue cat = ctrl.getCatalogue();

        btnLire.setVisible(false);
        btnAvis.setVisible(false);

        if (type.contains("Morceau")) {
            Morceau m = cat.getMorceaux().stream().filter(x -> x.getTitre().equals(nom)).findFirst().orElse(null);
            if (m == null) return;
            selectionCourante = m;
            StringBuilder sb = new StringBuilder();
            sb.append("♪  ").append(m.getTitre()).append("\n\n");
            sb.append("Auteur  : ").append(m.getAuteur() != null ? m.getAuteur().getNom() : "Inconnu").append("\n");
            sb.append("Durée   : ").append(m.getDureeFormatee()).append("\n");
            sb.append("Écoutes : ").append(m.getNbEcoutes()).append("\n");
            if (!m.getAlbums().isEmpty()) {
                sb.append("Albums  : ");
                m.getAlbums().forEach(a -> sb.append(a.getTitre()).append("  "));
                sb.append("\n");
            }
            if (!m.getAvis().isEmpty()) {
                sb.append(String.format("Note    : %.1f/5 (%d avis)\n", m.getNoteMoyenne(), m.getAvis().size()));
                sb.append("\nAvis :\n");
                m.getAvis().forEach(av -> sb.append("  • ").append(av).append("\n"));
            } else {
                sb.append("\nAucun avis pour ce morceau.");
            }
            detailArea.setText(sb.toString());
            btnLire.setVisible(true);
            if (ctrl.estAbonne()) btnAvis.setVisible(true);

        } else if (type.contains("Album")) {
            Album a = cat.getAlbums().stream().filter(x -> x.getTitre().equals(nom)).findFirst().orElse(null);
            if (a == null) return;
            selectionCourante = a;
            StringBuilder sb = new StringBuilder();
            sb.append("💿  ").append(a.getTitre()).append("\n\n");
            sb.append("Auteur : ").append(a.getAuteur() != null ? a.getAuteur().getNom() : "Inconnu").append("\n");
            sb.append("Année  : ").append(a.getAnnee()).append("\n");
            sb.append("Durée  : ").append(a.getDureeTotaleFormatee()).append("\n\n");
            sb.append("Titres :\n");
            a.getMorceaux().forEach(m -> sb.append("  ").append(m.getTitre()).append("  (").append(m.getDureeFormatee()).append(")\n"));
            detailArea.setText(sb.toString());

        } else if (type.contains("Artiste")) {
            Artiste ar = cat.getArtistes().stream().filter(x -> x.getNom().equals(nom)).findFirst().orElse(null);
            if (ar == null) return;
            selectionCourante = ar;
            StringBuilder sb = new StringBuilder();
            sb.append("🎤  ").append(ar.getNom()).append("\n\n");
            if (!ar.getBiographie().isEmpty()) sb.append(ar.getBiographie()).append("\n\n");
            if (ar.getGroupe() != null) sb.append("Groupe  : ").append(ar.getGroupe().getNom()).append("\n");
            sb.append("Albums  : ").append(ar.getAlbums().size()).append("\n");
            sb.append("Titres  : ").append(ar.getMorceaux().size()).append("\n\n");
            if (!ar.getAlbums().isEmpty()) {
                sb.append("Discographie :\n");
                ar.getAlbums().forEach(alb -> sb.append("  💿 ").append(alb.getTitre()).append(" (").append(alb.getAnnee()).append(")\n"));
            }
            detailArea.setText(sb.toString());

        } else if (type.contains("Groupe")) {
            Groupe g = cat.getGroupes().stream().filter(x -> x.getNom().equals(nom)).findFirst().orElse(null);
            if (g == null) return;
            selectionCourante = g;
            StringBuilder sb = new StringBuilder();
            sb.append("🎸  ").append(g.getNom()).append("\n\n");
            sb.append("Membres : ");
            g.getMembres().forEach(m -> sb.append(m.getNom()).append("  "));
            sb.append("\n\nAlbums : ").append(g.getAlbums().size()).append("\n\n");
            if (!g.getAlbums().isEmpty()) {
                sb.append("Discographie :\n");
                g.getAlbums().forEach(a -> sb.append("  💿 ").append(a.getTitre()).append(" (").append(a.getAnnee()).append(")\n"));
            }
            detailArea.setText(sb.toString());
        }
        detailArea.setCaretPosition(0);
    }

    private void actionDouble(int row) {
        if (selectionCourante instanceof Morceau) ecouterSelection();
    }

    // ---------------------------------------------------------------
    //  Lecture avec barre de progression
    // ---------------------------------------------------------------

    private void ecouterSelection() {
        if (!(selectionCourante instanceof Morceau)) return;
        Morceau m = (Morceau) selectionCourante;
        try {
            ctrl.ecouter(m);
        } catch (LimiteEcoutesAtteinte ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Limite atteinte", JOptionPane.WARNING_MESSAGE);
            return;
        }
        simulerLecture(m);
        // Rafraîchir le détail (nb écoutes mis à jour)
        selectionnerLigne(resultTable.getSelectedRow());
    }

    /** Simule la lecture avec une animation de JProgressBar via SwingWorker. */
    private void simulerLecture(Morceau m) {
        lectureLabel.setText("▶  " + m.getTitre() + "  —  " + m.getAuteur().getNom());
        progressBar.setValue(0);
        progressBar.setString("0%");
        btnLire.setEnabled(false);

        // Durée simulée : min(duree_réelle, 3) secondes à l'écran
        int msTotal = Math.min(m.getDuree(), 3) * 1000;

        new SwingWorker<Void, Integer>() {
            protected Void doInBackground() throws Exception {
                int steps = 50;
                int delay = msTotal / steps;
                for (int i = 1; i <= steps; i++) {
                    Thread.sleep(delay);
                    publish(i * 2); // 0→100
                }
                return null;
            }
            protected void process(java.util.List<Integer> chunks) {
                int val = chunks.get(chunks.size() - 1);
                progressBar.setValue(val);
                progressBar.setString(val + "%");
            }
            protected void done() {
                progressBar.setValue(100);
                progressBar.setString("100%");
                lectureLabel.setText("✓  " + m.getTitre() + "  — terminé");
                btnLire.setEnabled(true);
            }
        }.execute();
    }

    // ---------------------------------------------------------------
    //  Avis
    // ---------------------------------------------------------------

    private void dialogAvis() {
        if (!(selectionCourante instanceof Morceau)) return;
        Morceau m = (Morceau) selectionCourante;
        JSpinner noteSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        JTextArea commentaire = new JTextArea(3, 20);
        commentaire.setLineWrap(true);
        JPanel p = new JPanel(new BorderLayout(6, 6));
        JPanel haut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        haut.add(new JLabel("Note (1-5) : ")); haut.add(noteSpinner);
        p.add(haut, BorderLayout.NORTH);
        p.add(new JLabel("Commentaire :"), BorderLayout.WEST);
        p.add(new JScrollPane(commentaire), BorderLayout.CENTER);
        int res = JOptionPane.showConfirmDialog(frame, p, "Laisser un avis", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;
        ctrl.ajouterAvis(m, (Integer) noteSpinner.getValue(), commentaire.getText().trim());
        JOptionPane.showMessageDialog(frame, "Avis enregistré !");
        selectionnerLigne(resultTable.getSelectedRow());
    }

    // ---------------------------------------------------------------
    //  Utilitaires
    // ---------------------------------------------------------------

    private void styleBtn(JButton btn, Color color) {
        btn.setForeground(color);
        btn.setBackground(WelcomePanel.BTN_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JLabel colorLabel(String text, Color c) {
        JLabel l = new JLabel(text);
        l.setForeground(c);
        return l;
    }
}
