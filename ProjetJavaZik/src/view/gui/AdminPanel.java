package view.gui;

import controller.GUIController;
import model.*;
import model.exceptions.ElementIntrouvableException;
import model.exceptions.MorceauDejaExistantException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Espace administrateur : onglets Catalogue, Abonnés et Statistiques.
 */
public class AdminPanel extends JPanel {

    private final GUIController ctrl;
    private final MainFrame frame;

    // Catalogue
    private DefaultTableModel morceauxModel;
    private JTable morceauxTable;
    private DefaultTableModel albumsModel;
    private JTable albumsTable;
    private DefaultTableModel auteursModel;
    private JTable auteursTable;

    // Abonnés
    private DefaultTableModel abonnesModel;
    private JTable abonnesTable;

    // Statistiques
    private JTextArea statsArea;

    public AdminPanel(GUIController ctrl, MainFrame frame) {
        this.ctrl  = ctrl;
        this.frame = frame;
        buildUI();
    }

    private void buildUI() {
        setBackground(WelcomePanel.BG);
        setLayout(new BorderLayout());

        // Barre haute
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(38, 38, 50));
        header.setBorder(new EmptyBorder(6, 16, 6, 16));

        JLabel lblAdmin = new JLabel("⚙  Administration JavaZic");
        lblAdmin.setForeground(WelcomePanel.ACCENT);
        lblAdmin.setFont(new Font("SansSerif", Font.BOLD, 13));

        JButton btnDeco = new JButton("Déconnexion");
        btnDeco.setForeground(WelcomePanel.FG_DIM);
        btnDeco.setBackground(WelcomePanel.BTN_BG);
        btnDeco.setFocusPainted(false);
        btnDeco.setBorderPainted(false);
        btnDeco.addActionListener(e -> {
            ctrl.deconnecter();
            frame.showCard(MainFrame.CARD_WELCOME);
        });

        header.add(lblAdmin, BorderLayout.WEST);
        header.add(btnDeco,  BorderLayout.EAST);

        // Onglets
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(WelcomePanel.BG);
        tabs.setForeground(WelcomePanel.FG);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabs.addTab("♪  Catalogue",  buildCatalogueTab());
        tabs.addTab("👤  Abonnés",    buildAbonnesTab());
        tabs.addTab("📊  Statistiques", buildStatsTab());

        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    public void rafraichir() {
        rafraichirMorceaux();
        rafraichirAlbums();
        rafraichirAuteurs();
        rafraichirAbonnes();
        rafraichirStats();
    }

    // ================================================================
    //  ONGLET CATALOGUE
    // ================================================================

    private JPanel buildCatalogueTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane sub = new JTabbedPane();
        sub.setBackground(new Color(38, 38, 50));
        sub.setForeground(WelcomePanel.FG);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));

        sub.addTab("Morceaux", buildMorceauxSubTab());
        sub.addTab("Albums",   buildAlbumsSubTab());
        sub.addTab("Auteurs",  buildAuteursSubTab());

        panel.add(sub, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMorceauxSubTab() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(WelcomePanel.BG);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));

        morceauxModel = new DefaultTableModel(new String[]{"Titre", "Auteur", "Durée", "Écoutes", "Note moy."}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        morceauxTable = new JTable(morceauxModel);
        styleTable(morceauxTable);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        btnPanel.setBackground(WelcomePanel.BG);

        JButton btnAjouter = bouton("+ Ajouter", WelcomePanel.ACCENT2);
        btnAjouter.addActionListener(e -> dialogAjouterMorceau());

        JButton btnSupprimer = bouton("- Supprimer", new Color(220, 80, 80));
        btnSupprimer.addActionListener(e -> supprimerMorceauSelection());

        btnPanel.add(btnAjouter);
        btnPanel.add(btnSupprimer);

        p.add(new JScrollPane(morceauxTable), BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildAlbumsSubTab() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(WelcomePanel.BG);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));

        albumsModel = new DefaultTableModel(new String[]{"Titre", "Auteur", "Année", "Nb morceaux"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        albumsTable = new JTable(albumsModel);
        styleTable(albumsTable);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        btnPanel.setBackground(WelcomePanel.BG);

        JButton btnAjouter = bouton("+ Ajouter", WelcomePanel.ACCENT2);
        btnAjouter.addActionListener(e -> dialogAjouterAlbum());

        JButton btnSupprimer = bouton("- Supprimer", new Color(220, 80, 80));
        btnSupprimer.addActionListener(e -> supprimerAlbumSelection());

        btnPanel.add(btnAjouter);
        btnPanel.add(btnSupprimer);

        p.add(new JScrollPane(albumsTable), BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildAuteursSubTab() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(WelcomePanel.BG);
        p.setBorder(new EmptyBorder(8, 8, 8, 8));

        auteursModel = new DefaultTableModel(new String[]{"Nom", "Type", "Nb morceaux", "Nb albums"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        auteursTable = new JTable(auteursModel);
        styleTable(auteursTable);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        btnPanel.setBackground(WelcomePanel.BG);

        JButton btnArtiste = bouton("+ Artiste", WelcomePanel.ACCENT2);
        btnArtiste.addActionListener(e -> dialogAjouterArtiste());

        JButton btnGroupe = bouton("+ Groupe", WelcomePanel.ACCENT);
        btnGroupe.addActionListener(e -> dialogAjouterGroupe());

        JButton btnSupprimer = bouton("- Supprimer", new Color(220, 80, 80));
        btnSupprimer.addActionListener(e -> supprimerAuteurSelection());

        btnPanel.add(btnArtiste);
        btnPanel.add(btnGroupe);
        btnPanel.add(btnSupprimer);

        p.add(new JScrollPane(auteursTable), BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        return p;
    }

    // ================================================================
    //  ONGLET ABONNÉS
    // ================================================================

    private JPanel buildAbonnesTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        abonnesModel = new DefaultTableModel(new String[]{"Login", "Nom", "Prénom", "Statut", "Playlists", "Historique"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        abonnesTable = new JTable(abonnesModel);
        styleTable(abonnesTable);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        btnPanel.setBackground(WelcomePanel.BG);

        JButton btnToggle = bouton("Suspendre / Réactiver", WelcomePanel.ACCENT);
        btnToggle.addActionListener(e -> toggleAbonneSelection());

        JButton btnSupprimer = bouton("Supprimer compte", new Color(220, 80, 80));
        btnSupprimer.addActionListener(e -> supprimerAbonneSelection());

        btnPanel.add(btnToggle);
        btnPanel.add(btnSupprimer);

        panel.add(new JScrollPane(abonnesTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  ONGLET STATISTIQUES
    // ================================================================

    private JPanel buildStatsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setBackground(new Color(38, 38, 50));
        statsArea.setForeground(WelcomePanel.FG);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statsArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        statsArea.setLineWrap(false);

        JButton btnRefresh = bouton("↻ Actualiser", WelcomePanel.ACCENT2);
        btnRefresh.addActionListener(e -> rafraichirStats());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(WelcomePanel.BG);
        top.add(btnRefresh);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
        return panel;
    }

    // ================================================================
    //  DIALOGUES — CATALOGUE
    // ================================================================

    private void dialogAjouterArtiste() {
        JTextField nomField = new JTextField(20);
        JTextField bioField = new JTextField(20);
        JPanel p = new JPanel(new GridLayout(2, 2, 6, 6));
        p.add(new JLabel("Nom :")); p.add(nomField);
        p.add(new JLabel("Biographie :")); p.add(bioField);
        int res = JOptionPane.showConfirmDialog(frame, p, "Ajouter un artiste",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) return;
        ctrl.ajouterArtiste(nom, bioField.getText().trim());
        rafraichirAuteurs();
    }

    private void dialogAjouterGroupe() {
        String nom = JOptionPane.showInputDialog(frame, "Nom du groupe :", "Ajouter un groupe", JOptionPane.PLAIN_MESSAGE);
        if (nom == null || nom.trim().isEmpty()) return;
        ctrl.ajouterGroupe(nom.trim());
        rafraichirAuteurs();
    }

    private void dialogAjouterAlbum() {
        ArrayList<AuteurMusical> auteurs = getAuteursListe();
        if (auteurs.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Ajoutez d'abord un artiste ou un groupe."); return;
        }
        JTextField titreField = new JTextField(20);
        JTextField anneeField = new JTextField(4);
        JComboBox<String> auteurBox = new JComboBox<>(
                auteurs.stream().map(AuteurMusical::getNom).toArray(String[]::new));

        JPanel p = new JPanel(new GridLayout(3, 2, 6, 6));
        p.add(new JLabel("Titre :")); p.add(titreField);
        p.add(new JLabel("Année :")); p.add(anneeField);
        p.add(new JLabel("Auteur :")); p.add(auteurBox);

        int res = JOptionPane.showConfirmDialog(frame, p, "Ajouter un album",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String titre = titreField.getText().trim();
        if (titre.isEmpty()) return;
        int annee;
        try { annee = Integer.parseInt(anneeField.getText().trim()); }
        catch (NumberFormatException e) { annee = 2024; }

        ctrl.ajouterAlbum(titre, annee, auteurs.get(auteurBox.getSelectedIndex()));
        rafraichirAlbums();
    }

    private void dialogAjouterMorceau() {
        ArrayList<AuteurMusical> auteurs = getAuteursListe();
        if (auteurs.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Ajoutez d'abord un artiste ou un groupe."); return;
        }
        JTextField titreField = new JTextField(20);
        JTextField dureeField = new JTextField(6);
        JComboBox<String> auteurBox = new JComboBox<>(
                auteurs.stream().map(AuteurMusical::getNom).toArray(String[]::new));
        dureeField.setToolTipText("Durée en secondes (ex: 210)");

        JPanel p = new JPanel(new GridLayout(3, 2, 6, 6));
        p.add(new JLabel("Titre :")); p.add(titreField);
        p.add(new JLabel("Durée (sec) :")); p.add(dureeField);
        p.add(new JLabel("Auteur :")); p.add(auteurBox);

        int res = JOptionPane.showConfirmDialog(frame, p, "Ajouter un morceau",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String titre = titreField.getText().trim();
        if (titre.isEmpty()) return;
        int duree;
        try { duree = Integer.parseInt(dureeField.getText().trim()); }
        catch (NumberFormatException e) { duree = 180; }

        try {
            ctrl.ajouterMorceau(titre, duree, auteurs.get(auteurBox.getSelectedIndex()));
            rafraichirMorceaux();
        } catch (MorceauDejaExistantException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Déjà présent", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ================================================================
    //  SUPPRESSIONS
    // ================================================================

    private void supprimerMorceauSelection() {
        int row = morceauxTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(frame, "Sélectionnez un morceau."); return; }
        ArrayList<Morceau> morceaux = ctrl.getCatalogue().getMorceaux();
        if (row >= morceaux.size()) return;
        Morceau m = morceaux.get(row);
        int ok = JOptionPane.showConfirmDialog(frame, "Supprimer \"" + m.getTitre() + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            ctrl.supprimerMorceau(m);
            rafraichirMorceaux();
        } catch (ElementIntrouvableException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerAlbumSelection() {
        int row = albumsTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(frame, "Sélectionnez un album."); return; }
        ArrayList<Album> albums = ctrl.getCatalogue().getAlbums();
        if (row >= albums.size()) return;
        Album a = albums.get(row);
        int ok = JOptionPane.showConfirmDialog(frame, "Supprimer l'album \"" + a.getTitre() + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            ctrl.supprimerAlbum(a);
            rafraichirAlbums();
        } catch (ElementIntrouvableException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void supprimerAuteurSelection() {
        int row = auteursTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(frame, "Sélectionnez un auteur."); return; }
        ArrayList<AuteurMusical> auteurs = getAuteursListe();
        if (row >= auteurs.size()) return;
        AuteurMusical a = auteurs.get(row);
        int ok = JOptionPane.showConfirmDialog(frame,
                "Supprimer \"" + a.getNom() + "\" et tous ses morceaux/albums ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            if (a instanceof Artiste) ctrl.supprimerArtiste((Artiste) a);
            else ctrl.supprimerGroupe((Groupe) a);
            rafraichirAuteurs();
            rafraichirMorceaux();
            rafraichirAlbums();
        } catch (ElementIntrouvableException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleAbonneSelection() {
        int row = abonnesTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(frame, "Sélectionnez un abonné."); return; }
        ArrayList<Abonne> list = ctrl.getAbonnes();
        if (row >= list.size()) return;
        ctrl.toggleSuspension(list.get(row));
        rafraichirAbonnes();
    }

    private void supprimerAbonneSelection() {
        int row = abonnesTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(frame, "Sélectionnez un abonné."); return; }
        ArrayList<Abonne> list = ctrl.getAbonnes();
        if (row >= list.size()) return;
        Abonne a = list.get(row);
        int ok = JOptionPane.showConfirmDialog(frame,
                "Supprimer définitivement le compte de \"" + a.getLogin() + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        ctrl.supprimerAbonne(a);
        rafraichirAbonnes();
    }

    // ================================================================
    //  RAFRAÎCHISSEMENT
    // ================================================================

    private void rafraichirMorceaux() {
        if (morceauxModel == null) return;
        morceauxModel.setRowCount(0);
        for (Morceau m : ctrl.getCatalogue().getMorceaux()) {
            String note = m.getAvis().isEmpty() ? "—" : String.format("%.1f ★", m.getNoteMoyenne());
            morceauxModel.addRow(new Object[]{
                    m.getTitre(),
                    m.getAuteur() != null ? m.getAuteur().getNom() : "",
                    m.getDureeFormatee(),
                    m.getNbEcoutes(),
                    note
            });
        }
    }

    private void rafraichirAlbums() {
        if (albumsModel == null) return;
        albumsModel.setRowCount(0);
        for (Album a : ctrl.getCatalogue().getAlbums()) {
            albumsModel.addRow(new Object[]{
                    a.getTitre(),
                    a.getAuteur() != null ? a.getAuteur().getNom() : "",
                    a.getAnnee(),
                    a.getMorceaux().size()
            });
        }
    }

    private void rafraichirAuteurs() {
        if (auteursModel == null) return;
        auteursModel.setRowCount(0);
        for (Artiste a : ctrl.getCatalogue().getArtistes()) {
            auteursModel.addRow(new Object[]{a.getNom(), "Artiste", a.getMorceaux().size(), a.getAlbums().size()});
        }
        for (Groupe g : ctrl.getCatalogue().getGroupes()) {
            auteursModel.addRow(new Object[]{g.getNom(), "Groupe", g.getMorceaux().size(), g.getAlbums().size()});
        }
    }

    private void rafraichirAbonnes() {
        if (abonnesModel == null) return;
        abonnesModel.setRowCount(0);
        for (Abonne a : ctrl.getAbonnes()) {
            abonnesModel.addRow(new Object[]{
                    a.getLogin(),
                    a.getNom(),
                    a.getPrenom(),
                    a.isActif() ? "✓ Actif" : "✗ Suspendu",
                    a.getPlaylists().size(),
                    a.getHistorique().getMorceaux().size()
            });
        }
    }

    private void rafraichirStats() {
        if (statsArea == null) return;
        Catalogue cat = ctrl.getCatalogue();
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════\n");
        sb.append("  STATISTIQUES JAVAZIC\n");
        sb.append("════════════════════════════════════════════════════\n\n");

        sb.append(String.format("  Morceaux dans le catalogue  : %d%n", cat.getMorceaux().size()));
        sb.append(String.format("  Albums                       : %d%n", cat.getAlbums().size()));
        sb.append(String.format("  Artistes                     : %d%n", cat.getArtistes().size()));
        sb.append(String.format("  Groupes                      : %d%n", cat.getGroupes().size()));
        sb.append(String.format("  Abonnés inscrits             : %d%n", ctrl.getAbonnes().size()));
        sb.append(String.format("  Écoutes totales              : %d%n%n", cat.getNbEcoutesTotales()));

        Morceau top = cat.getMorceauPlusEcoute();
        if (top != null) {
            sb.append("  ♪ Morceau le plus écouté :\n");
            sb.append(String.format("    %s — %s  (%d écoutes)%n%n",
                    top.getTitre(),
                    top.getAuteur() != null ? top.getAuteur().getNom() : "?",
                    top.getNbEcoutes()));
        }

        sb.append("  Top 5 morceaux :\n");
        int rank = 1;
        for (Morceau m : cat.getMorceauxParEcoutes()) {
            if (rank > 5) break;
            sb.append(String.format("    %d. %-30s  %3d écoutes%n",
                    rank++,
                    m.getTitre() + (m.getAuteur() != null ? " — " + m.getAuteur().getNom() : ""),
                    m.getNbEcoutes()));
        }

        sb.append("\n  Abonnés suspendus :\n");
        boolean aucun = true;
        for (Abonne a : ctrl.getAbonnes()) {
            if (!a.isActif()) {
                sb.append("    • ").append(a.getLogin()).append("\n");
                aucun = false;
            }
        }
        if (aucun) sb.append("    (aucun)\n");

        sb.append("\n════════════════════════════════════════════════════\n");
        statsArea.setText(sb.toString());
        statsArea.setCaretPosition(0);
    }

    // ================================================================
    //  UTILITAIRES
    // ================================================================

    private ArrayList<AuteurMusical> getAuteursListe() {
        ArrayList<AuteurMusical> liste = new ArrayList<>();
        liste.addAll(ctrl.getCatalogue().getArtistes());
        liste.addAll(ctrl.getCatalogue().getGroupes());
        return liste;
    }

    private JButton bouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setForeground(couleur);
        btn.setBackground(WelcomePanel.BTN_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setBackground(new Color(38, 38, 50));
        table.setForeground(WelcomePanel.FG);
        table.setGridColor(new Color(55, 55, 70));
        table.setSelectionBackground(new Color(60, 100, 160));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(24);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(45, 45, 58));
        table.getTableHeader().setForeground(WelcomePanel.FG_DIM);
    }
}
