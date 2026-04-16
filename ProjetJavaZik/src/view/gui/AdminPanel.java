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
import java.util.HashMap;
import java.util.Map;

/**
 * Espace administrateur : onglets Catalogue, Abonnes et Statistiques.
 * Toutes les tables supportent le tri par clic sur les en-tetes.
 */
public class AdminPanel extends JPanel {

    private final GUIController ctrl;
    private final MainFrame frame;

    private DefaultTableModel morceauxModel, albumsModel, auteursModel, abonnesModel;
    private JTable morceauxTable, albumsTable, auteursTable, abonnesTable;
    private JTextArea statsArea;

    public AdminPanel(GUIController ctrl, MainFrame frame) {
        this.ctrl  = ctrl;
        this.frame = frame;
        buildUI();
    }

    private void buildUI() {
        setBackground(WelcomePanel.BG);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WelcomePanel.HEADER_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, WelcomePanel.BORDER),
                new EmptyBorder(10, 18, 10, 18)));

        JLabel lblAdmin = new JLabel("Administration JavaZic");
        lblAdmin.setForeground(WelcomePanel.ACCENT);
        lblAdmin.setFont(new Font("SansSerif", Font.BOLD, 15));

        JButton btnDeco = CataloguePanel.btnNeutre("Deconnexion");
        btnDeco.addActionListener(e -> {
            ctrl.deconnecter();
            frame.showCard(MainFrame.CARD_WELCOME);
        });

        header.add(lblAdmin, BorderLayout.WEST);
        header.add(btnDeco,  BorderLayout.EAST);

        // Onglets
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(WelcomePanel.CARD_BG);
        tabs.setForeground(WelcomePanel.FG);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        tabs.addTab("  Catalogue  ", buildCatalogueTab());
        tabs.addTab("  Abonnes  ",   buildAbonnesTab());
        tabs.addTab("  Statistiques  ", buildStatsTab());

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
    //  CATALOGUE
    // ================================================================

    private JPanel buildCatalogueTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        JTabbedPane sub = new JTabbedPane();
        sub.setBackground(WelcomePanel.CARD_BG);
        sub.setForeground(WelcomePanel.FG);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));

        sub.addTab("Morceaux", buildMorceauxSubTab());
        sub.addTab("Albums",   buildAlbumsSubTab());
        sub.addTab("Auteurs",  buildAuteursSubTab());

        panel.add(sub, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildMorceauxSubTab() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(WelcomePanel.BG);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        morceauxModel = new DefaultTableModel(new String[]{"Titre", "Auteur", "Genre", "Duree", "Ecoutes", "Note moy."}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int col) {
                if (col == 4) return Integer.class;
                return String.class;
            }
        };
        morceauxTable = new JTable(morceauxModel);
        CataloguePanel.styleTable(morceauxTable);
        morceauxTable.setAutoCreateRowSorter(true);

        JPanel btnPanel = btnRow(
                CataloguePanel.btnAccent("+ Ajouter"), e -> dialogAjouterMorceau(),
                CataloguePanel.btnDanger("- Supprimer"), e -> supprimerMorceauSelection()
        );

        p.add(CataloguePanel.wrapInRoundedScroll(morceauxTable), BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildAlbumsSubTab() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(WelcomePanel.BG);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        albumsModel = new DefaultTableModel(new String[]{"Titre", "Auteur", "Annee", "Nb morceaux"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int col) {
                if (col >= 2) return Integer.class;
                return String.class;
            }
        };
        albumsTable = new JTable(albumsModel);
        CataloguePanel.styleTable(albumsTable);
        albumsTable.setAutoCreateRowSorter(true);

        JPanel btnPanel = btnRow(
                CataloguePanel.btnAccent("+ Ajouter"), e -> dialogAjouterAlbum(),
                CataloguePanel.btnDanger("- Supprimer"), e -> supprimerAlbumSelection()
        );

        p.add(CataloguePanel.wrapInRoundedScroll(albumsTable), BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildAuteursSubTab() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(WelcomePanel.BG);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        auteursModel = new DefaultTableModel(new String[]{"Nom", "Type", "Nb morceaux", "Nb albums"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int col) {
                if (col >= 2) return Integer.class;
                return String.class;
            }
        };
        auteursTable = new JTable(auteursModel);
        CataloguePanel.styleTable(auteursTable);
        auteursTable.setAutoCreateRowSorter(true);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        btnPanel.setOpaque(false);
        JButton btnArtiste = CataloguePanel.btnAccent("+ Artiste");
        btnArtiste.addActionListener(e -> dialogAjouterArtiste());
        JButton btnGroupe = CataloguePanel.btnNeutre("+ Groupe");
        btnGroupe.addActionListener(e -> dialogAjouterGroupe());
        JButton btnSup = CataloguePanel.btnDanger("- Supprimer");
        btnSup.addActionListener(e -> supprimerAuteurSelection());
        btnPanel.add(btnArtiste); btnPanel.add(btnGroupe); btnPanel.add(btnSup);

        p.add(CataloguePanel.wrapInRoundedScroll(auteursTable), BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);
        return p;
    }

    // ================================================================
    //  ABONNES
    // ================================================================

    private JPanel buildAbonnesTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        abonnesModel = new DefaultTableModel(new String[]{"Login", "Nom", "Prenom", "Statut", "Playlists", "Historique"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int col) {
                if (col >= 4) return Integer.class;
                return String.class;
            }
        };
        abonnesTable = new JTable(abonnesModel);
        CataloguePanel.styleTable(abonnesTable);
        abonnesTable.setAutoCreateRowSorter(true);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        btnPanel.setOpaque(false);
        JButton btnToggle = CataloguePanel.btnNeutre("Suspendre / Reactiver");
        btnToggle.addActionListener(e -> toggleAbonneSelection());
        JButton btnSup = CataloguePanel.btnDanger("Supprimer compte");
        btnSup.addActionListener(e -> supprimerAbonneSelection());
        btnPanel.add(btnToggle); btnPanel.add(btnSup);

        panel.add(CataloguePanel.wrapInRoundedScroll(abonnesTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  STATISTIQUES
    // ================================================================

    private JPanel buildStatsTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setBackground(WelcomePanel.CARD_BG);
        statsArea.setForeground(WelcomePanel.FG);
        statsArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statsArea.setBorder(new EmptyBorder(20, 24, 20, 24));
        statsArea.setLineWrap(false);

        JButton btnRefresh = CataloguePanel.btnAccent("Actualiser");
        btnRefresh.addActionListener(e -> rafraichirStats());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(btnRefresh);

        panel.add(top, BorderLayout.NORTH);
        panel.add(CataloguePanel.wrapInRoundedScroll(statsArea), BorderLayout.CENTER);
        return panel;
    }

    // ================================================================
    //  DIALOGUES
    // ================================================================

    private void dialogAjouterArtiste() {
        JTextField nomField = new JTextField(20);
        JTextField bioField = new JTextField(20);
        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
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
        if (auteurs.isEmpty()) { JOptionPane.showMessageDialog(frame, "Ajoutez d'abord un artiste ou un groupe."); return; }
        JTextField titreField = new JTextField(20);
        JTextField anneeField = new JTextField(4);
        JComboBox<String> auteurBox = new JComboBox<>(auteurs.stream().map(AuteurMusical::getNom).toArray(String[]::new));
        JPanel p = new JPanel(new GridLayout(3, 2, 8, 8));
        p.add(new JLabel("Titre :")); p.add(titreField);
        p.add(new JLabel("Annee :")); p.add(anneeField);
        p.add(new JLabel("Auteur :")); p.add(auteurBox);
        int res = JOptionPane.showConfirmDialog(frame, p, "Ajouter un album", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        String titre = titreField.getText().trim();
        if (titre.isEmpty()) return;
        int annee;
        try { annee = Integer.parseInt(anneeField.getText().trim()); } catch (NumberFormatException e) { annee = 2024; }
        ctrl.ajouterAlbum(titre, annee, auteurs.get(auteurBox.getSelectedIndex()));
        rafraichirAlbums();
    }

    private void dialogAjouterMorceau() {
        ArrayList<AuteurMusical> auteurs = getAuteursListe();
        if (auteurs.isEmpty()) { JOptionPane.showMessageDialog(frame, "Ajoutez d'abord un artiste ou un groupe."); return; }
        JTextField titreField = new JTextField(20);
        JTextField dureeField = new JTextField(6);
        JComboBox<String> auteurBox = new JComboBox<>(auteurs.stream().map(AuteurMusical::getNom).toArray(String[]::new));
        JComboBox<Genre> genreBox = new JComboBox<>(Genre.values());
        genreBox.setSelectedItem(Genre.INCONNU);
        dureeField.setToolTipText("Duree en secondes (ex: 210)");
        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));
        p.add(new JLabel("Titre :")); p.add(titreField);
        p.add(new JLabel("Duree (sec) :")); p.add(dureeField);
        p.add(new JLabel("Auteur :")); p.add(auteurBox);
        p.add(new JLabel("Genre :")); p.add(genreBox);
        int res = JOptionPane.showConfirmDialog(frame, p, "Ajouter un morceau", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        String titre = titreField.getText().trim();
        if (titre.isEmpty()) return;
        int duree;
        try { duree = Integer.parseInt(dureeField.getText().trim()); } catch (NumberFormatException e) { duree = 180; }
        try {
            ctrl.ajouterMorceau(titre, duree, auteurs.get(auteurBox.getSelectedIndex()), (Genre) genreBox.getSelectedItem());
            rafraichirMorceaux();
        } catch (MorceauDejaExistantException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Deja present", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ================================================================
    //  SUPPRESSIONS
    // ================================================================

    private void supprimerMorceauSelection() {
        int viewRow = morceauxTable.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(frame, "Selectionnez un morceau."); return; }
        int modelRow = morceauxTable.convertRowIndexToModel(viewRow);
        ArrayList<Morceau> morceaux = ctrl.getCatalogue().getMorceaux();
        if (modelRow >= morceaux.size()) return;
        Morceau m = morceaux.get(modelRow);
        int ok = JOptionPane.showConfirmDialog(frame, "Supprimer \"" + m.getTitre() + "\" ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try { ctrl.supprimerMorceau(m); rafraichirMorceaux(); }
        catch (ElementIntrouvableException ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
    }

    private void supprimerAlbumSelection() {
        int viewRow = albumsTable.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(frame, "Selectionnez un album."); return; }
        int modelRow = albumsTable.convertRowIndexToModel(viewRow);
        ArrayList<Album> albums = ctrl.getCatalogue().getAlbums();
        if (modelRow >= albums.size()) return;
        Album a = albums.get(modelRow);
        int ok = JOptionPane.showConfirmDialog(frame, "Supprimer l'album \"" + a.getTitre() + "\" ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try { ctrl.supprimerAlbum(a); rafraichirAlbums(); }
        catch (ElementIntrouvableException ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
    }

    private void supprimerAuteurSelection() {
        int viewRow = auteursTable.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(frame, "Selectionnez un auteur."); return; }
        int modelRow = auteursTable.convertRowIndexToModel(viewRow);
        ArrayList<AuteurMusical> auteurs = getAuteursListe();
        if (modelRow >= auteurs.size()) return;
        AuteurMusical a = auteurs.get(modelRow);
        int ok = JOptionPane.showConfirmDialog(frame, "Supprimer \"" + a.getNom() + "\" et tous ses morceaux/albums ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            if (a instanceof Artiste) ctrl.supprimerArtiste((Artiste) a);
            else ctrl.supprimerGroupe((Groupe) a);
            rafraichirAuteurs(); rafraichirMorceaux(); rafraichirAlbums();
        } catch (ElementIntrouvableException ex) { JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE); }
    }

    private void toggleAbonneSelection() {
        int viewRow = abonnesTable.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(frame, "Selectionnez un abonne."); return; }
        int modelRow = abonnesTable.convertRowIndexToModel(viewRow);
        ArrayList<Abonne> list = ctrl.getAbonnes();
        if (modelRow >= list.size()) return;
        ctrl.toggleSuspension(list.get(modelRow));
        rafraichirAbonnes();
    }

    private void supprimerAbonneSelection() {
        int viewRow = abonnesTable.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(frame, "Selectionnez un abonne."); return; }
        int modelRow = abonnesTable.convertRowIndexToModel(viewRow);
        ArrayList<Abonne> list = ctrl.getAbonnes();
        if (modelRow >= list.size()) return;
        Abonne a = list.get(modelRow);
        int ok = JOptionPane.showConfirmDialog(frame, "Supprimer definitivement le compte de \"" + a.getLogin() + "\" ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        ctrl.supprimerAbonne(a);
        rafraichirAbonnes();
    }

    // ================================================================
    //  RAFRAICHISSEMENT
    // ================================================================

    private void rafraichirMorceaux() {
        if (morceauxModel == null) return;
        morceauxModel.setRowCount(0);
        for (Morceau m : ctrl.getCatalogue().getMorceaux()) {
            String note = m.getAvis().isEmpty() ? "\u2014" : String.format("%.1f / 5", m.getNoteMoyenne());
            morceauxModel.addRow(new Object[]{
                    m.getTitre(),
                    m.getAuteur() != null ? m.getAuteur().getNom() : "",
                    m.getGenre().getLabel(),
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
        for (Artiste a : ctrl.getCatalogue().getArtistes())
            auteursModel.addRow(new Object[]{a.getNom(), "Artiste", a.getMorceaux().size(), a.getAlbums().size()});
        for (Groupe g : ctrl.getCatalogue().getGroupes())
            auteursModel.addRow(new Object[]{g.getNom(), "Groupe", g.getMorceaux().size(), g.getAlbums().size()});
    }

    private void rafraichirAbonnes() {
        if (abonnesModel == null) return;
        abonnesModel.setRowCount(0);
        for (Abonne a : ctrl.getAbonnes()) {
            abonnesModel.addRow(new Object[]{
                    a.getLogin(), a.getNom(), a.getPrenom(),
                    a.isActif() ? "Actif" : "Suspendu",
                    a.getPlaylists().size(),
                    a.getHistorique().getMorceaux().size()
            });
        }
    }

    private void rafraichirStats() {
        if (statsArea == null) return;
        Catalogue cat = ctrl.getCatalogue();
        ArrayList<Abonne> abonnes = ctrl.getAbonnes();
        StringBuilder sb = new StringBuilder();
        String sep = "\u2500".repeat(55);

        // ── VUE D'ENSEMBLE ──
        sb.append("VUE D'ENSEMBLE\n").append(sep).append("\n\n");
        sb.append(String.format("  Morceaux dans le catalogue  : %d%n", cat.getMorceaux().size()));
        sb.append(String.format("  Albums                      : %d%n", cat.getAlbums().size()));
        sb.append(String.format("  Artistes                    : %d%n", cat.getArtistes().size()));
        sb.append(String.format("  Groupes                     : %d%n", cat.getGroupes().size()));
        sb.append(String.format("  Abonnes inscrits            : %d%n", abonnes.size()));
        int actifs = 0;
        for (Abonne a : abonnes) if (a.isActif()) actifs++;
        sb.append(String.format("  Abonnes actifs              : %d%n", actifs));
        sb.append(String.format("  Abonnes suspendus           : %d%n", abonnes.size() - actifs));
        sb.append(String.format("  Ecoutes totales             : %d%n", cat.getNbEcoutesTotales()));

        // Duree totale du catalogue
        int dureeTotale = cat.getMorceaux().stream().mapToInt(Morceau::getDuree).sum();
        sb.append(String.format("  Duree totale du catalogue   : %dh%02dm%n", dureeTotale / 3600, (dureeTotale % 3600) / 60));

        // Nombre total de playlists
        int totalPlaylists = 0;
        for (Abonne a : abonnes) totalPlaylists += a.getPlaylists().size();
        sb.append(String.format("  Playlists creees            : %d%n", totalPlaylists));

        // Nombre total d'avis
        int totalAvis = 0;
        for (Morceau m : cat.getMorceaux()) totalAvis += m.getAvis().size();
        sb.append(String.format("  Avis deposes                : %d%n", totalAvis));

        // ── TOP 5 MORCEAUX PAR ECOUTES ──
        sb.append("\n\nTOP 5 MORCEAUX (par ecoutes)\n").append(sep).append("\n\n");
        int rank = 1;
        for (Morceau m : cat.getMorceauxParEcoutes()) {
            if (rank > 5) break;
            String auteur = m.getAuteur() != null ? m.getAuteur().getNom() : "?";
            sb.append(String.format("  %d.  %-28s  %s  (%d ecoutes)%n", rank++, m.getTitre(), auteur, m.getNbEcoutes()));
        }
        if (cat.getMorceaux().isEmpty()) sb.append("  (catalogue vide)\n");

        // ── TOP 5 ALBUMS PAR ECOUTES ──
        sb.append("\n\nTOP 5 ALBUMS (par ecoutes cumulees)\n").append(sep).append("\n\n");
        ArrayList<Album> albumsTries = new ArrayList<>(cat.getAlbums());
        albumsTries.sort((a1, a2) -> {
            int e1 = a1.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            int e2 = a2.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            return Integer.compare(e2, e1);
        });
        rank = 1;
        for (Album a : albumsTries) {
            if (rank > 5) break;
            int ecoutesAlbum = a.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            String auteur = a.getAuteur() != null ? a.getAuteur().getNom() : "?";
            sb.append(String.format("  %d.  %-28s  %s  (%d ecoutes, %d titres)%n",
                    rank++, a.getTitre(), auteur, ecoutesAlbum, a.getMorceaux().size()));
        }
        if (albumsTries.isEmpty()) sb.append("  (aucun album)\n");

        // ── TOP 5 ARTISTES/GROUPES PAR ECOUTES ──
        sb.append("\n\nTOP 5 ARTISTES / GROUPES (par ecoutes cumulees)\n").append(sep).append("\n\n");
        ArrayList<AuteurMusical> auteursTries = getAuteursListe();
        auteursTries.sort((a1, a2) -> {
            int e1 = a1.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            int e2 = a2.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            return Integer.compare(e2, e1);
        });
        rank = 1;
        for (AuteurMusical a : auteursTries) {
            if (rank > 5) break;
            int ecoutesAuteur = a.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            String type = a instanceof Artiste ? "Artiste" : "Groupe";
            sb.append(String.format("  %d.  %-28s  [%s]  (%d ecoutes, %d titres)%n",
                    rank++, a.getNom(), type, ecoutesAuteur, a.getMorceaux().size()));
        }
        if (auteursTries.isEmpty()) sb.append("  (aucun auteur)\n");

        // ── TOP 5 MORCEAUX LES MIEUX NOTES ──
        sb.append("\n\nTOP 5 MORCEAUX (par note moyenne)\n").append(sep).append("\n\n");
        ArrayList<Morceau> morceauxNotes = new ArrayList<>();
        for (Morceau m : cat.getMorceaux()) {
            if (!m.getAvis().isEmpty()) morceauxNotes.add(m);
        }
        morceauxNotes.sort((a1, a2) -> Double.compare(a2.getNoteMoyenne(), a1.getNoteMoyenne()));
        rank = 1;
        for (Morceau m : morceauxNotes) {
            if (rank > 5) break;
            String auteur = m.getAuteur() != null ? m.getAuteur().getNom() : "?";
            sb.append(String.format("  %d.  %-28s  %s  (%.1f/5, %d avis)%n",
                    rank++, m.getTitre(), auteur, m.getNoteMoyenne(), m.getAvis().size()));
        }
        if (morceauxNotes.isEmpty()) sb.append("  (aucun avis depose)\n");

        // ── TOP 5 MORCEAUX LES PLUS AJOUTES AUX PLAYLISTS ──
        sb.append("\n\nTOP 5 MORCEAUX (les plus ajoutes aux playlists)\n").append(sep).append("\n\n");
        HashMap<Morceau, Integer> playlistCount = new HashMap<>();
        for (Abonne a : abonnes) {
            for (Playlist p : a.getPlaylists()) {
                for (Morceau m : p.getMorceaux()) {
                    playlistCount.merge(m, 1, Integer::sum);
                }
            }
        }
        ArrayList<Map.Entry<Morceau, Integer>> playlistRank = new ArrayList<>(playlistCount.entrySet());
        playlistRank.sort((a1, a2) -> Integer.compare(a2.getValue(), a1.getValue()));
        rank = 1;
        for (Map.Entry<Morceau, Integer> entry : playlistRank) {
            if (rank > 5) break;
            Morceau m = entry.getKey();
            String auteur = m.getAuteur() != null ? m.getAuteur().getNom() : "?";
            sb.append(String.format("  %d.  %-28s  %s  (dans %d playlist(s))%n",
                    rank++, m.getTitre(), auteur, entry.getValue()));
        }
        if (playlistRank.isEmpty()) sb.append("  (aucun morceau dans les playlists)\n");

        // ── ECOUTES PAR ALBUM ──
        sb.append("\n\nECOUTES PAR ALBUM\n").append(sep).append("\n\n");
        for (Album a : cat.getAlbums()) {
            int ecoutesAlbum = a.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            String auteur = a.getAuteur() != null ? a.getAuteur().getNom() : "?";
            sb.append(String.format("  %-30s  %s  (%d ecoutes)%n", a.getTitre(), auteur, ecoutesAlbum));
        }
        if (cat.getAlbums().isEmpty()) sb.append("  (aucun album)\n");

        // ── ECOUTES PAR ARTISTE / GROUPE ──
        sb.append("\n\nECOUTES PAR ARTISTE / GROUPE\n").append(sep).append("\n\n");
        for (AuteurMusical a : getAuteursListe()) {
            int ecoutesAuteur = a.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            String type = a instanceof Artiste ? "Artiste" : "Groupe";
            sb.append(String.format("  %-30s  [%s]  %d ecoutes  (%d titres, %d albums)%n",
                    a.getNom(), type, ecoutesAuteur, a.getMorceaux().size(), a.getAlbums().size()));
        }
        if (getAuteursListe().isEmpty()) sb.append("  (aucun auteur)\n");

        // ── ABONNES SUSPENDUS ──
        sb.append("\n\nABONNES SUSPENDUS\n").append(sep).append("\n\n");
        boolean aucunSuspendu = true;
        for (Abonne a : abonnes) {
            if (!a.isActif()) {
                sb.append("  - ").append(a.getLogin()).append(" (").append(a.getNomComplet()).append(")\n");
                aucunSuspendu = false;
            }
        }
        if (aucunSuspendu) sb.append("  (aucun)\n");

        // ── ABONNES LES PLUS ACTIFS ──
        sb.append("\n\nABONNES LES PLUS ACTIFS\n").append(sep).append("\n\n");
        ArrayList<Abonne> abonnesTries = new ArrayList<>(abonnes);
        abonnesTries.sort((a1, a2) -> Integer.compare(
                a2.getHistorique().getMorceaux().size(),
                a1.getHistorique().getMorceaux().size()));
        rank = 1;
        for (Abonne a : abonnesTries) {
            if (rank > 5) break;
            int nbEcoutes = a.getHistorique().getMorceaux().size();
            int nbPl = a.getPlaylists().size();
            sb.append(String.format("  %d.  %-20s  %d ecoutes,  %d playlist(s)%n",
                    rank++, a.getLogin(), nbEcoutes, nbPl));
        }
        if (abonnes.isEmpty()) sb.append("  (aucun abonne)\n");

        sb.append("\n").append(sep).append("\n");
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

    /** Cree une rangee de 2 boutons avec leurs listeners. */
    private JPanel btnRow(JButton b1, java.awt.event.ActionListener a1, JButton b2, java.awt.event.ActionListener a2) {
        b1.addActionListener(a1);
        b2.addActionListener(a2);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        p.setOpaque(false);
        p.add(b1); p.add(b2);
        return p;
    }
}
