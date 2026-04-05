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
 * Espace abonné : onglets Catalogue, Playlists et Historique.
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

        // Barre haute avec pseudo et déconnexion
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(38, 38, 50));
        header.setBorder(new EmptyBorder(6, 16, 6, 16));

        String pseudo = ctrl.estAbonne() ? ((Abonne) ctrl.getUtilisateurCourant()).getLogin() : "";
        JLabel lblUser = new JLabel("♪  Connecté : " + pseudo);
        lblUser.setForeground(WelcomePanel.ACCENT2);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));

        JButton btnDeco = new JButton("Déconnexion");
        btnDeco.setForeground(WelcomePanel.FG_DIM);
        btnDeco.setBackground(WelcomePanel.BTN_BG);
        btnDeco.setFocusPainted(false);
        btnDeco.setBorderPainted(false);
        btnDeco.addActionListener(e -> {
            ctrl.deconnecter();
            frame.showCard(MainFrame.CARD_WELCOME);
        });

        header.add(lblUser,  BorderLayout.WEST);
        header.add(btnDeco,  BorderLayout.EAST);

        // Onglets
        tabs = new JTabbedPane();
        tabs.setBackground(WelcomePanel.BG);
        tabs.setForeground(WelcomePanel.FG);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));

        catalogueTab = new CataloguePanel(ctrl, frame, true);
        tabs.addTab("♪  Catalogue",   catalogueTab);
        tabs.addTab("☰  Playlists",   buildPlaylistTab());
        tabs.addTab("⏱  Historique",  buildHistoriqueTab());

        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
    }

    public void rafraichir() {
        catalogueTab.rafraichir();
        rafraichirPlaylists();
        rafraichirHistorique();
    }

    // ================================================================
    //  ONGLET PLAYLISTS
    // ================================================================

    private JPanel buildPlaylistTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Gauche : liste des playlists
        playlistListModel = new DefaultListModel<>();
        playlistList = new JList<>(playlistListModel);
        playlistList.setBackground(new Color(38, 38, 50));
        playlistList.setForeground(WelcomePanel.FG);
        playlistList.setSelectionBackground(new Color(60, 100, 160));
        playlistList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        playlistList.setFixedCellHeight(28);
        playlistList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) rafraichirMorceauxPlaylist();
        });

        JPanel leftPanel = new JPanel(new BorderLayout(4, 4));
        leftPanel.setBackground(WelcomePanel.BG);
        leftPanel.add(new JScrollPane(playlistList), BorderLayout.CENTER);
        leftPanel.add(buildPlaylistButtons(), BorderLayout.SOUTH);
        leftPanel.setPreferredSize(new Dimension(220, 0));

        // Droite : morceaux de la playlist sélectionnée
        morceauxPlaylistModel = new DefaultTableModel(new String[]{"Titre", "Auteur", "Durée"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        morceauxPlaylistTable = new JTable(morceauxPlaylistModel);
        styleTable(morceauxPlaylistTable);

        JPanel rightPanel = new JPanel(new BorderLayout(4, 4));
        rightPanel.setBackground(WelcomePanel.BG);
        JLabel lblMorceaux = new JLabel("  Morceaux de la playlist");
        lblMorceaux.setForeground(WelcomePanel.FG_DIM);
        lblMorceaux.setFont(new Font("SansSerif", Font.BOLD, 12));
        rightPanel.add(lblMorceaux,                           BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(morceauxPlaylistTable), BorderLayout.CENTER);
        rightPanel.add(buildMorceauxPlaylistButtons(),         BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(230);
        split.setDividerSize(4);
        split.setBackground(WelcomePanel.BG);
        split.setBorder(BorderFactory.createEmptyBorder());

        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPlaylistButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        p.setBackground(WelcomePanel.BG);

        JButton btnCree = bouton("+ Créer", WelcomePanel.ACCENT2);
        btnCree.addActionListener(e -> {
            String nom = JOptionPane.showInputDialog(frame, "Nom de la playlist :", "Nouvelle playlist", JOptionPane.PLAIN_MESSAGE);
            if (nom != null && !nom.trim().isEmpty()) {
                ctrl.creerPlaylist(nom.trim());
                rafraichirPlaylists();
            }
        });

        JButton btnRen = bouton("Renommer", WelcomePanel.FG);
        btnRen.addActionListener(e -> {
            Playlist p2 = getPlaylistSelectionnee();
            if (p2 == null) return;
            String nom = JOptionPane.showInputDialog(frame, "Nouveau nom :", p2.getNom());
            if (nom != null && !nom.trim().isEmpty()) {
                p2.setNom(nom.trim());
                rafraichirPlaylists();
            }
        });

        JButton btnSup = bouton("Supprimer", new Color(220, 80, 80));
        btnSup.addActionListener(e -> {
            Playlist p2 = getPlaylistSelectionnee();
            if (p2 == null) return;
            int ok = JOptionPane.showConfirmDialog(frame, "Supprimer \"" + p2.getNom() + "\" ?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                try { ctrl.supprimerPlaylist(p2); } catch (ElementIntrouvableException ex) { /* ignoré */ }
                rafraichirPlaylists();
            }
        });

        p.add(btnCree); p.add(btnRen); p.add(btnSup);
        return p;
    }

    private JPanel buildMorceauxPlaylistButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        p.setBackground(WelcomePanel.BG);

        JButton btnAjout = bouton("+ Ajouter du catalogue", WelcomePanel.ACCENT);
        btnAjout.addActionListener(e -> dialogAjouterMorceau());

        JButton btnRetirer = bouton("- Retirer", new Color(220, 80, 80));
        btnRetirer.addActionListener(e -> retirerMorceauSelection());

        p.add(btnAjout); p.add(btnRetirer);
        return p;
    }

    private void dialogAjouterMorceau() {
        Playlist pl = getPlaylistSelectionnee();
        if (pl == null) { JOptionPane.showMessageDialog(frame, "Sélectionnez une playlist."); return; }

        ArrayList<Morceau> morceaux = ctrl.getCatalogue().getMorceaux();
        String[] titres = morceaux.stream().map(m -> m.getTitre() + " — " + m.getAuteur().getNom()).toArray(String[]::new);
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
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Déjà présent", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void retirerMorceauSelection() {
        Playlist pl = getPlaylistSelectionnee();
        if (pl == null) return;
        int row = morceauxPlaylistTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(frame, "Sélectionnez un morceau."); return; }
        try {
            ctrl.retirerMorceauPlaylist(pl, pl.getMorceaux().get(row));
            rafraichirMorceauxPlaylist();
        } catch (ElementIntrouvableException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Playlist getPlaylistSelectionnee() {
        int idx = playlistList.getSelectedIndex();
        if (idx < 0 || !(ctrl.getUtilisateurCourant() instanceof Abonne)) return null;
        ArrayList<Playlist> pls = ((Abonne) ctrl.getUtilisateurCourant()).getPlaylists();
        return idx < pls.size() ? pls.get(idx) : null;
    }

    private void rafraichirPlaylists() {
        playlistListModel.clear();
        if (!(ctrl.getUtilisateurCourant() instanceof Abonne)) return;
        ((Abonne) ctrl.getUtilisateurCourant()).getPlaylists()
                .forEach(p -> playlistListModel.addElement("☰ " + p.getNom() + "  (" + p.getMorceaux().size() + ")"));
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

    // ================================================================
    //  ONGLET HISTORIQUE
    // ================================================================

    private JPanel buildHistoriqueTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(WelcomePanel.BG);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        historiqueModel = new DefaultListModel<>();
        JList<String> liste = new JList<>(historiqueModel);
        liste.setBackground(new Color(38, 38, 50));
        liste.setForeground(WelcomePanel.FG);
        liste.setFont(new Font("SansSerif", Font.PLAIN, 13));
        liste.setFixedCellHeight(28);

        JLabel titre = new JLabel("  Derniers morceaux écoutés (du plus récent)");
        titre.setForeground(WelcomePanel.FG_DIM);
        titre.setFont(new Font("SansSerif", Font.BOLD, 12));

        panel.add(titre,                    BorderLayout.NORTH);
        panel.add(new JScrollPane(liste),   BorderLayout.CENTER);
        return panel;
    }

    private void rafraichirHistorique() {
        if (historiqueModel == null) return;
        historiqueModel.clear();
        if (!(ctrl.getUtilisateurCourant() instanceof Abonne)) return;
        int i = 1;
        for (Morceau m : ((Abonne) ctrl.getUtilisateurCourant()).getHistorique().getMorceaux())
            historiqueModel.addElement(i++ + ".  " + m.getTitre() + "  —  " + (m.getAuteur() != null ? m.getAuteur().getNom() : ""));
    }

    // ================================================================
    //  Utilitaires
    // ================================================================

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
