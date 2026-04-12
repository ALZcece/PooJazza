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
import java.util.Map;

/**
 * Espace abonne : onglets Catalogue, Playlists (avec collaboratif) et Historique.
 */
public class AbonnePanel extends JPanel {

    private final GUIController ctrl;
    private final MainFrame frame;

    private CataloguePanel catalogueTab;
    private JTabbedPane tabs;

    // Playlists
    private DefaultListModel<String> playlistListModel;
    private JList<String> playlistList;
    private DefaultTableModel morceauxPlaylistModel;
    private JTable morceauxPlaylistTable;

    // Playlists partagees
    private DefaultListModel<String> partageesListModel;
    private JList<String> partageesJList;
    private DefaultTableModel morceauxPartageesModel;
    private JTable morceauxPartageesTable;

    // Historique
    private DefaultListModel<String> historiqueModel;

    public AbonnePanel(GUIController ctrl, MainFrame frame) {
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

        String pseudo = ctrl.estAbonne() ? ((Abonne) ctrl.getUtilisateurCourant()).getLogin() : "";
        JLabel lblUser = new JLabel("Connecte : " + pseudo);
        lblUser.setForeground(WelcomePanel.ACCENT);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 15));

        JButton btnDeco = CataloguePanel.btnNeutre("Deconnexion");
        btnDeco.addActionListener(e -> {
            ctrl.deconnecter();
            frame.showCard(MainFrame.CARD_WELCOME);
        });

        header.add(lblUser, BorderLayout.WEST);
        header.add(btnDeco, BorderLayout.EAST);

        // Onglets
        tabs = new JTabbedPane();
        tabs.setBackground(WelcomePanel.CARD_BG);
        tabs.setForeground(WelcomePanel.FG);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        catalogueTab = new CataloguePanel(ctrl, frame, true);
        tabs.addTab("  Catalogue  ", catalogueTab);
        tabs.addTab("  Mes Playlists  ", buildPlaylistTab());
        tabs.addTab("  Playlists partagees  ", buildPartageesTab());
        tabs.addTab("  Historique  ", buildHistoriqueTab());

        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    public void rafraichir() {
        catalogueTab.rafraichir();
        rafraichirPlaylists();
        rafraichirPartageesListe();
        rafraichirHistorique();
    }

    // ================================================================
    //  ONGLET MES PLAYLISTS
    // ================================================================

    private JPanel buildPlaylistTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Gauche : liste
        playlistListModel = new DefaultListModel<>();
        playlistList = new JList<>(playlistListModel);
        styleList(playlistList);
        playlistList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) rafraichirMorceauxPlaylist();
        });

        JPanel leftPanel = new JPanel(new BorderLayout(6, 6));
        leftPanel.setBackground(WelcomePanel.BG);
        JLabel lblPl = new JLabel("  Mes playlists");
        lblPl.setForeground(WelcomePanel.FG);
        lblPl.setFont(new Font("SansSerif", Font.BOLD, 14));
        leftPanel.add(lblPl, BorderLayout.NORTH);
        leftPanel.add(CataloguePanel.wrapInRoundedScroll(playlistList), BorderLayout.CENTER);
        leftPanel.add(buildPlaylistButtons(), BorderLayout.SOUTH);
        leftPanel.setPreferredSize(new Dimension(260, 0));

        // Droite : morceaux
        morceauxPlaylistModel = new DefaultTableModel(new String[]{"Titre", "Auteur", "Duree"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        morceauxPlaylistTable = new JTable(morceauxPlaylistModel);
        CataloguePanel.styleTable(morceauxPlaylistTable);
        morceauxPlaylistTable.setAutoCreateRowSorter(true);

        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        rightPanel.setBackground(WelcomePanel.BG);
        JLabel lblMorceaux = new JLabel("  Morceaux de la playlist");
        lblMorceaux.setForeground(WelcomePanel.FG);
        lblMorceaux.setFont(new Font("SansSerif", Font.BOLD, 14));
        rightPanel.add(lblMorceaux, BorderLayout.NORTH);
        rightPanel.add(CataloguePanel.wrapInRoundedScroll(morceauxPlaylistTable), BorderLayout.CENTER);
        rightPanel.add(buildMorceauxPlaylistButtons(), BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(270);
        split.setDividerSize(5);
        split.setBackground(WelcomePanel.BG);
        split.setBorder(BorderFactory.createEmptyBorder());

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPlaylistButtons() {
        JPanel p = new JPanel(new GridLayout(2, 2, 4, 4));
        p.setOpaque(false);

        JButton btnCree = CataloguePanel.btnAccent("+ Creer");
        btnCree.addActionListener(e -> {
            String nom = JOptionPane.showInputDialog(frame, "Nom de la playlist :", "Nouvelle playlist", JOptionPane.PLAIN_MESSAGE);
            if (nom != null && !nom.trim().isEmpty()) {
                ctrl.creerPlaylist(nom.trim());
                rafraichirPlaylists();
            }
        });

        JButton btnRen = CataloguePanel.btnNeutre("Renommer");
        btnRen.addActionListener(e -> {
            Playlist p2 = getPlaylistSelectionnee();
            if (p2 == null) return;
            String nom = JOptionPane.showInputDialog(frame, "Nouveau nom :", p2.getNom());
            if (nom != null && !nom.trim().isEmpty()) {
                p2.setNom(nom.trim());
                rafraichirPlaylists();
            }
        });

        JButton btnSup = CataloguePanel.btnDanger("Supprimer");
        btnSup.addActionListener(e -> {
            Playlist p2 = getPlaylistSelectionnee();
            if (p2 == null) return;
            int ok = JOptionPane.showConfirmDialog(frame, "Supprimer \"" + p2.getNom() + "\" ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                try { ctrl.supprimerPlaylist(p2); } catch (ElementIntrouvableException ex) { /* ignore */ }
                rafraichirPlaylists();
            }
        });

        JButton btnCollab = CataloguePanel.btnAccent("Partager");
        btnCollab.addActionListener(e -> dialogGererCollaborateurs());

        p.add(btnCree); p.add(btnCollab);
        p.add(btnRen);  p.add(btnSup);
        return p;
    }

    private JPanel buildMorceauxPlaylistButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        p.setOpaque(false);

        JButton btnAjout = CataloguePanel.btnAccent("+ Ajouter du catalogue");
        btnAjout.addActionListener(e -> dialogAjouterMorceau(getPlaylistSelectionnee()));

        JButton btnRetirer = CataloguePanel.btnDanger("- Retirer");
        btnRetirer.addActionListener(e -> retirerMorceauSelection(getPlaylistSelectionnee(), morceauxPlaylistTable));

        p.add(btnAjout); p.add(btnRetirer);
        return p;
    }

    // ================================================================
    //  ONGLET PLAYLISTS PARTAGEES
    // ================================================================

    private JPanel buildPartageesTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Gauche : liste des playlists partagees
        partageesListModel = new DefaultListModel<>();
        partageesJList = new JList<>(partageesListModel);
        styleList(partageesJList);
        partageesJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) rafraichirMorceauxPartagees();
        });

        JPanel leftPanel = new JPanel(new BorderLayout(6, 6));
        leftPanel.setBackground(WelcomePanel.BG);
        JLabel lblPart = new JLabel("  Playlists partagees avec moi");
        lblPart.setForeground(WelcomePanel.FG);
        lblPart.setFont(new Font("SansSerif", Font.BOLD, 14));
        leftPanel.add(lblPart, BorderLayout.NORTH);
        leftPanel.add(CataloguePanel.wrapInRoundedScroll(partageesJList), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(300, 0));

        // Droite : morceaux de la playlist partagee
        morceauxPartageesModel = new DefaultTableModel(new String[]{"Titre", "Auteur", "Duree"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        morceauxPartageesTable = new JTable(morceauxPartageesModel);
        CataloguePanel.styleTable(morceauxPartageesTable);
        morceauxPartageesTable.setAutoCreateRowSorter(true);

        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));
        rightPanel.setBackground(WelcomePanel.BG);
        JLabel lblMrc = new JLabel("  Morceaux");
        lblMrc.setForeground(WelcomePanel.FG);
        lblMrc.setFont(new Font("SansSerif", Font.BOLD, 14));
        rightPanel.add(lblMrc, BorderLayout.NORTH);
        rightPanel.add(CataloguePanel.wrapInRoundedScroll(morceauxPartageesTable), BorderLayout.CENTER);

        // Boutons pour les playlists editables
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        btnPanel.setOpaque(false);
        JButton btnAjout = CataloguePanel.btnAccent("+ Ajouter");
        btnAjout.addActionListener(e -> {
            Playlist pl = getPlaylistPartageeSelectionnee();
            if (pl == null) return;
            if (!(ctrl.getUtilisateurCourant() instanceof Abonne)) return;
            if (!pl.peutModifier((Abonne) ctrl.getUtilisateurCourant())) {
                JOptionPane.showMessageDialog(frame, "Vous n'avez pas les droits d'edition sur cette playlist.", "Lecture seule", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dialogAjouterMorceau(pl);
            rafraichirMorceauxPartagees();
        });
        JButton btnRetirer = CataloguePanel.btnDanger("- Retirer");
        btnRetirer.addActionListener(e -> {
            Playlist pl = getPlaylistPartageeSelectionnee();
            if (pl == null) return;
            if (!(ctrl.getUtilisateurCourant() instanceof Abonne)) return;
            if (!pl.peutModifier((Abonne) ctrl.getUtilisateurCourant())) {
                JOptionPane.showMessageDialog(frame, "Vous n'avez pas les droits d'edition sur cette playlist.", "Lecture seule", JOptionPane.WARNING_MESSAGE);
                return;
            }
            retirerMorceauSelection(pl, morceauxPartageesTable);
            rafraichirMorceauxPartagees();
        });
        btnPanel.add(btnAjout); btnPanel.add(btnRetirer);
        rightPanel.add(btnPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(310);
        split.setDividerSize(5);
        split.setBackground(WelcomePanel.BG);
        split.setBorder(BorderFactory.createEmptyBorder());

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    // ================================================================
    //  GESTION COLLABORATEURS (dialog)
    // ================================================================

    private void dialogGererCollaborateurs() {
        Playlist pl = getPlaylistSelectionnee();
        if (pl == null) {
            JOptionPane.showMessageDialog(frame,
                    "Selectionnez d'abord une playlist dans la liste de gauche.",
                    "Aucune playlist selectionnee", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Verifier s'il y a d'autres abonnes
        ArrayList<Abonne> autresAbonnes = ctrl.getAutresAbonnes();
        if (autresAbonnes.isEmpty()) {
            JOptionPane.showMessageDialog(frame,
                    "Il n'y a pas encore d'autres abonnes inscrits.\n"
                    + "Creez d'autres comptes pour pouvoir partager vos playlists.",
                    "Aucun abonne disponible", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // --- Dialog principal ---
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setPreferredSize(new Dimension(500, 380));

        // Titre
        JLabel lblTitre = new JLabel("Partager \"" + pl.getNom() + "\" avec d'autres abonnes");
        lblTitre.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitre.setForeground(WelcomePanel.ACCENT);
        mainPanel.add(lblTitre, BorderLayout.NORTH);

        // Liste des collaborateurs actuels
        DefaultListModel<String> collabModel = new DefaultListModel<>();
        refreshCollabModel(pl, collabModel);
        JList<String> collabList = new JList<>(collabModel);
        collabList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        collabList.setFixedCellHeight(28);

        JPanel centerPanel = new JPanel(new BorderLayout(4, 4));
        JLabel lblCollab = new JLabel("Collaborateurs actuels :");
        lblCollab.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblCollab.setForeground(WelcomePanel.FG_DIM);
        centerPanel.add(lblCollab, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(collabList), BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton btnAjouter = new JButton("+ Ajouter un abonne");
        btnAjouter.setFont(new Font("SansSerif", Font.BOLD, 12));
        JButton btnRetirer = new JButton("- Retirer le selectionne");
        btnRetirer.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPanel.add(btnAjouter);
        btnPanel.add(btnRetirer);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        btnAjouter.addActionListener(e -> {
            // Filtrer ceux deja collaborateurs
            ArrayList<Abonne> disponibles = new ArrayList<>();
            for (Abonne a : ctrl.getAutresAbonnes()) {
                if (!pl.estCollaborateur(a)) disponibles.add(a);
            }
            if (disponibles.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Tous les abonnes sont deja collaborateurs de cette playlist.",
                        "Aucun abonne disponible", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Sous-dialog pour choisir l'abonne
            JPanel addPanel = new JPanel(new BorderLayout(8, 8));
            addPanel.setPreferredSize(new Dimension(350, 250));

            JLabel lblChoix = new JLabel("Choisir un abonne a ajouter :");
            lblChoix.setFont(new Font("SansSerif", Font.PLAIN, 12));
            addPanel.add(lblChoix, BorderLayout.NORTH);

            String[] noms = disponibles.stream()
                    .map(a -> a.getLogin() + "  (" + a.getNomComplet() + ")")
                    .toArray(String[]::new);
            JList<String> listeAb = new JList<>(noms);
            listeAb.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listeAb.setFont(new Font("SansSerif", Font.PLAIN, 13));
            listeAb.setFixedCellHeight(28);
            addPanel.add(new JScrollPane(listeAb), BorderLayout.CENTER);

            JCheckBox checkEdit = new JCheckBox("Autoriser l'edition (ajout/suppression de morceaux)");
            checkEdit.setFont(new Font("SansSerif", Font.PLAIN, 12));
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(checkEdit, BorderLayout.NORTH);
            JLabel hint = new JLabel("Sans cette option, l'abonne pourra uniquement consulter la playlist.");
            hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
            hint.setForeground(WelcomePanel.FG_DIM);
            bottomPanel.add(hint, BorderLayout.SOUTH);
            addPanel.add(bottomPanel, BorderLayout.SOUTH);

            int res = JOptionPane.showConfirmDialog(mainPanel, addPanel, "Ajouter un collaborateur",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION || listeAb.getSelectedIndex() < 0) return;

            Abonne selected = disponibles.get(listeAb.getSelectedIndex());
            ctrl.ajouterCollaborateur(pl, selected, checkEdit.isSelected());
            refreshCollabModel(pl, collabModel);
            JOptionPane.showMessageDialog(mainPanel,
                    "Playlist partagee avec " + selected.getLogin() + " !",
                    "Partage effectue", JOptionPane.INFORMATION_MESSAGE);
        });

        btnRetirer.addActionListener(e -> {
            int idx = collabList.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(mainPanel, "Selectionnez un collaborateur dans la liste.");
                return;
            }
            ArrayList<Abonne> collabs = new ArrayList<>(pl.getCollaborateurs().keySet());
            if (idx >= collabs.size()) return;
            Abonne toRemove = collabs.get(idx);
            int ok = JOptionPane.showConfirmDialog(mainPanel,
                    "Retirer " + toRemove.getLogin() + " des collaborateurs ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
            ctrl.retirerCollaborateur(pl, toRemove);
            refreshCollabModel(pl, collabModel);
        });

        JOptionPane.showMessageDialog(frame, mainPanel, "Partager la playlist", JOptionPane.PLAIN_MESSAGE);
        rafraichirPlaylists();
    }

    private void refreshCollabModel(Playlist pl, DefaultListModel<String> model) {
        model.clear();
        for (Map.Entry<Abonne, Boolean> entry : pl.getCollaborateurs().entrySet()) {
            String droits = entry.getValue() ? "Edition" : "Lecture seule";
            model.addElement(entry.getKey().getLogin() + "  [" + droits + "]");
        }
        if (model.isEmpty()) model.addElement("(aucun collaborateur)");
    }

    // ================================================================
    //  ACTIONS PARTAGEES
    // ================================================================

    private void dialogAjouterMorceau(Playlist pl) {
        if (pl == null) { JOptionPane.showMessageDialog(frame, "Selectionnez une playlist."); return; }

        ArrayList<Morceau> morceaux = ctrl.getCatalogue().getMorceaux();
        String[] titres = morceaux.stream().map(m -> m.getTitre() + " \u2014 " + m.getAuteur().getNom()).toArray(String[]::new);
        if (titres.length == 0) { JOptionPane.showMessageDialog(frame, "Le catalogue est vide."); return; }

        JList<String> liste = new JList<>(titres);
        liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        int res = JOptionPane.showConfirmDialog(frame, new JScrollPane(liste), "Choisir un morceau",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION || liste.getSelectedIndex() < 0) return;

        try {
            ctrl.ajouterMorceauPlaylist(pl, morceaux.get(liste.getSelectedIndex()));
            rafraichirMorceauxPlaylist();
        } catch (MorceauDejaExistantException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Deja present", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void retirerMorceauSelection(Playlist pl, JTable table) {
        if (pl == null) return;
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { JOptionPane.showMessageDialog(frame, "Selectionnez un morceau."); return; }
        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow >= pl.getMorceaux().size()) return;
        try {
            ctrl.retirerMorceauPlaylist(pl, pl.getMorceaux().get(modelRow));
            rafraichirMorceauxPlaylist();
        } catch (ElementIntrouvableException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================================================================
    //  ONGLET HISTORIQUE
    // ================================================================

    private JPanel buildHistoriqueTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(14, 14, 14, 14));

        historiqueModel = new DefaultListModel<>();
        JList<String> liste = new JList<>(historiqueModel);
        styleList(liste);

        JLabel titre = new JLabel("  Derniers morceaux ecoutes (du plus recent)");
        titre.setForeground(WelcomePanel.FG);
        titre.setFont(new Font("SansSerif", Font.BOLD, 14));

        panel.add(titre, BorderLayout.NORTH);
        panel.add(CataloguePanel.wrapInRoundedScroll(liste), BorderLayout.CENTER);
        return panel;
    }

    // ================================================================
    //  RAFRAICHISSEMENTS
    // ================================================================

    private Playlist getPlaylistSelectionnee() {
        int idx = playlistList.getSelectedIndex();
        if (idx < 0 || !(ctrl.getUtilisateurCourant() instanceof Abonne)) return null;
        ArrayList<Playlist> pls = ((Abonne) ctrl.getUtilisateurCourant()).getPlaylists();
        return idx < pls.size() ? pls.get(idx) : null;
    }

    private Playlist getPlaylistPartageeSelectionnee() {
        int idx = partageesJList.getSelectedIndex();
        if (idx < 0) return null;
        ArrayList<Playlist> partagees = ctrl.getPlaylistsPartagees();
        return idx < partagees.size() ? partagees.get(idx) : null;
    }

    private void rafraichirPlaylists() {
        playlistListModel.clear();
        if (!(ctrl.getUtilisateurCourant() instanceof Abonne)) return;
        for (Playlist p : ((Abonne) ctrl.getUtilisateurCourant()).getPlaylists()) {
            int nbCollab = p.getCollaborateurs().size();
            String suffix = nbCollab > 0 ? "  \u2022 " + nbCollab + " collab." : "";
            playlistListModel.addElement(p.getNom() + "  (" + p.getMorceaux().size() + " titres)" + suffix);
        }
        morceauxPlaylistModel.setRowCount(0);
    }

    private void rafraichirMorceauxPlaylist() {
        morceauxPlaylistModel.setRowCount(0);
        Playlist pl = getPlaylistSelectionnee();
        if (pl == null) return;
        pl.getMorceaux().forEach(m -> morceauxPlaylistModel.addRow(new Object[]{
                m.getTitre(),
                m.getAuteur() != null ? m.getAuteur().getNom() : "",
                m.getDureeFormatee()
        }));
    }

    private void rafraichirPartageesListe() {
        if (partageesListModel == null) return;
        partageesListModel.clear();
        ArrayList<Playlist> partagees = ctrl.getPlaylistsPartagees();
        if (!(ctrl.getUtilisateurCourant() instanceof Abonne)) return;
        Abonne moi = (Abonne) ctrl.getUtilisateurCourant();
        for (Playlist p : partagees) {
            String droits = p.peutModifier(moi) ? "Edition" : "Lecture seule";
            partageesListModel.addElement(p.getNom() + "  (par " + p.getProprietaire().getLogin() + ")  [" + droits + "]");
        }
        morceauxPartageesModel.setRowCount(0);
    }

    private void rafraichirMorceauxPartagees() {
        morceauxPartageesModel.setRowCount(0);
        Playlist pl = getPlaylistPartageeSelectionnee();
        if (pl == null) return;
        pl.getMorceaux().forEach(m -> morceauxPartageesModel.addRow(new Object[]{
                m.getTitre(),
                m.getAuteur() != null ? m.getAuteur().getNom() : "",
                m.getDureeFormatee()
        }));
    }

    private void rafraichirHistorique() {
        if (historiqueModel == null) return;
        historiqueModel.clear();
        if (!(ctrl.getUtilisateurCourant() instanceof Abonne)) return;
        int i = 1;
        for (Morceau m : ((Abonne) ctrl.getUtilisateurCourant()).getHistorique().getMorceaux())
            historiqueModel.addElement(i++ + ".  " + m.getTitre() + "  \u2014  " + (m.getAuteur() != null ? m.getAuteur().getNom() : ""));
    }

    // ================================================================
    //  UTILITAIRES
    // ================================================================

    private void styleList(JList<?> list) {
        list.setBackground(WelcomePanel.CARD_BG);
        list.setForeground(WelcomePanel.FG);
        list.setSelectionBackground(WelcomePanel.SELECTION);
        list.setSelectionForeground(WelcomePanel.FG);
        list.setFont(new Font("SansSerif", Font.PLAIN, 13));
        list.setFixedCellHeight(34);
        list.setBorder(new EmptyBorder(6, 10, 6, 10));
    }
}
