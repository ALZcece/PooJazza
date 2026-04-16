package view.gui;

import controller.GUIController;
import model.*;
import model.exceptions.LimiteEcoutesAtteinte;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Panel de navigation et de recherche dans le catalogue musical.
 * Supporte le tri des colonnes par clic sur les en-tetes.
 */
public class CataloguePanel extends JPanel {

    private final GUIController ctrl;
    private final MainFrame frame;
    private final boolean embedded;

    private JTextField rechercheField;
    private JComboBox<String> filtreCombo;
    private DefaultTableModel tableModel;
    private JTable resultTable;
    private JTextArea detailArea;
    private JLabel lectureLabel;
    private JProgressBar progressBar;
    private JButton btnLire;
    private JButton btnAvis;

    private Object selectionCourante;

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
    //  Barre de recherche
    // ---------------------------------------------------------------

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setBackground(WelcomePanel.HEADER_BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, WelcomePanel.BORDER));

        if (!embedded) {
            JButton btnRetour = btnNeutre("Accueil");
            btnRetour.addActionListener(e -> { ctrl.deconnecter(); frame.showCard(MainFrame.CARD_WELCOME); });
            bar.add(btnRetour);
        }

        rechercheField = new JTextField(22);
        rechercheField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rechercheField.setBorder(BorderFactory.createCompoundBorder(
                new WelcomePanel.RoundedLineBorder(WelcomePanel.BORDER, 1, 10),
                new EmptyBorder(6, 12, 6, 12)));
        rechercheField.addActionListener(e -> rechercher());

        filtreCombo = new JComboBox<>(new String[]{"Tout", "Morceaux", "Albums", "Artistes", "Groupes"});
        filtreCombo.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton btnSearch = btnAccent("Rechercher");
        btnSearch.addActionListener(e -> rechercher());

        JButton btnAll = btnNeutre("Tout afficher");
        btnAll.addActionListener(e -> afficherTout());

        JLabel lbl = new JLabel("Recherche :");
        lbl.setForeground(WelcomePanel.FG_DIM);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));

        bar.add(lbl);
        bar.add(rechercheField);
        bar.add(filtreCombo);
        bar.add(btnSearch);
        bar.add(btnAll);
        return bar;
    }

    // ---------------------------------------------------------------
    //  Zone centrale
    // ---------------------------------------------------------------

    private JSplitPane buildCenter() {
        // Table
        tableModel = new DefaultTableModel(new String[]{"Type", "Titre / Nom", "Infos"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(tableModel);
        styleTable(resultTable);
        resultTable.setAutoCreateRowSorter(true);
        resultTable.getColumnModel().getColumn(0).setMaxWidth(90);
        resultTable.getColumnModel().getColumn(2).setMaxWidth(160);
        resultTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int viewRow = resultTable.getSelectedRow();
                if (viewRow >= 0) {
                    int modelRow = resultTable.convertRowIndexToModel(viewRow);
                    selectionnerLigne(modelRow);
                }
                if (e.getClickCount() == 2 && viewRow >= 0) actionDouble();
            }
        });

        JScrollPane scrollListe = wrapInRoundedScroll(resultTable);

        // Detail
        JPanel detailPanel = new JPanel(new BorderLayout(0, 10));
        detailPanel.setBackground(WelcomePanel.BG);
        detailPanel.setBorder(new EmptyBorder(0, 8, 0, 0));

        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setBackground(WelcomePanel.CARD_BG);
        detailArea.setForeground(WelcomePanel.FG);
        detailArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailArea.setBorder(new EmptyBorder(16, 18, 16, 18));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        JScrollPane scrollDetail = wrapInRoundedScroll(detailArea);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actionPanel.setOpaque(false);
        btnLire = btnAccent("Ecouter");
        btnLire.setVisible(false);
        btnLire.addActionListener(e -> ecouterSelection());

        btnAvis = btnNeutre("Laisser un avis");
        btnAvis.setVisible(false);
        btnAvis.addActionListener(e -> dialogAvis());

        actionPanel.add(btnLire);
        actionPanel.add(btnAvis);

        detailPanel.add(scrollDetail, BorderLayout.CENTER);
        detailPanel.add(actionPanel,  BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollListe, detailPanel);
        split.setDividerLocation(500);
        split.setDividerSize(5);
        split.setBackground(WelcomePanel.BG);
        split.setBorder(new EmptyBorder(8, 8, 8, 8));
        return split;
    }

    // ---------------------------------------------------------------
    //  Barre de lecture
    // ---------------------------------------------------------------

    private JPanel buildPlayerBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setBackground(WelcomePanel.CARD_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, WelcomePanel.BORDER),
                new EmptyBorder(10, 18, 10, 18)));

        lectureLabel = new JLabel("Aucune lecture en cours");
        lectureLabel.setForeground(WelcomePanel.FG_DIM);
        lectureLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("");
        progressBar.setForeground(WelcomePanel.ACCENT);
        progressBar.setBackground(WelcomePanel.BTN_BG);
        progressBar.setPreferredSize(new Dimension(300, 14));
        progressBar.setBorder(BorderFactory.createEmptyBorder());

        bar.add(lectureLabel, BorderLayout.WEST);
        bar.add(progressBar,  BorderLayout.CENTER);
        return bar;
    }

    // ---------------------------------------------------------------
    //  Recherche et affichage
    // ---------------------------------------------------------------

    public void rafraichir() { afficherTout(); }

    private void afficherTout() {
        tableModel.setRowCount(0);
        selectionCourante = null;
        detailArea.setText("");
        btnLire.setVisible(false);
        btnAvis.setVisible(false);

        Catalogue cat = ctrl.getCatalogue();
        String filtre = (String) filtreCombo.getSelectedItem();
        if ("Tout".equals(filtre) || "Morceaux".equals(filtre))
            cat.getMorceaux().forEach(m -> tableModel.addRow(new Object[]{"Morceau", m.getTitre(), m.getAuteur() != null ? m.getAuteur().getNom() : ""}));
        if ("Tout".equals(filtre) || "Albums".equals(filtre))
            cat.getAlbums().forEach(a -> tableModel.addRow(new Object[]{"Album", a.getTitre(), String.valueOf(a.getAnnee())}));
        if ("Tout".equals(filtre) || "Artistes".equals(filtre))
            cat.getArtistes().forEach(a -> tableModel.addRow(new Object[]{"Artiste", a.getNom(), ""}));
        if ("Tout".equals(filtre) || "Groupes".equals(filtre))
            cat.getGroupes().forEach(g -> tableModel.addRow(new Object[]{"Groupe", g.getNom(), g.getMembres().size() + " membres"}));
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
            cat.rechercherMorceaux(query).forEach(m -> tableModel.addRow(new Object[]{"Morceau", m.getTitre(), m.getAuteur() != null ? m.getAuteur().getNom() : ""}));
        if ("Tout".equals(filtre) || "Albums".equals(filtre))
            cat.rechercherAlbums(query).forEach(a -> tableModel.addRow(new Object[]{"Album", a.getTitre(), String.valueOf(a.getAnnee())}));
        if ("Tout".equals(filtre) || "Artistes".equals(filtre))
            cat.rechercherArtistes(query).forEach(a -> tableModel.addRow(new Object[]{"Artiste", a.getNom(), ""}));
        if ("Tout".equals(filtre) || "Groupes".equals(filtre))
            cat.rechercherGroupes(query).forEach(g -> tableModel.addRow(new Object[]{"Groupe", g.getNom(), ""}));
    }

    // ---------------------------------------------------------------
    //  Selection et details
    // ---------------------------------------------------------------

    private void selectionnerLigne(int modelRow) {
        String type  = (String) tableModel.getValueAt(modelRow, 0);
        String nom   = (String) tableModel.getValueAt(modelRow, 1);
        Catalogue cat = ctrl.getCatalogue();

        btnLire.setVisible(false);
        btnAvis.setVisible(false);

        if (type.contains("Morceau")) {
            Morceau m = cat.getMorceaux().stream().filter(x -> x.getTitre().equals(nom)).findFirst().orElse(null);
            if (m == null) return;
            selectionCourante = m;
            StringBuilder sb = new StringBuilder();
            sb.append(m.getTitre()).append("\n");
            sb.append("\u2500".repeat(30)).append("\n\n");
            sb.append("Auteur   :  ").append(m.getAuteur() != null ? m.getAuteur().getNom() : "Inconnu").append("\n");
            sb.append("Duree    :  ").append(m.getDureeFormatee()).append("\n");
            sb.append("Ecoutes  :  ").append(m.getNbEcoutes()).append("\n");
            if (!m.getAlbums().isEmpty()) {
                sb.append("Albums   :  ");
                m.getAlbums().forEach(a -> sb.append(a.getTitre()).append("  "));
                sb.append("\n");
            }
            if (!m.getAvis().isEmpty()) {
                sb.append(String.format("Note     :  %.1f / 5  (%d avis)\n", m.getNoteMoyenne(), m.getAvis().size()));
                sb.append("\nAvis :\n");
                m.getAvis().forEach(av -> sb.append("  \u2022 ").append(av).append("\n"));
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
            sb.append(a.getTitre()).append("\n");
            sb.append("\u2500".repeat(30)).append("\n\n");
            sb.append("Auteur  :  ").append(a.getAuteur() != null ? a.getAuteur().getNom() : "Inconnu").append("\n");
            sb.append("Annee   :  ").append(a.getAnnee()).append("\n");
            sb.append("Duree   :  ").append(a.getDureeTotaleFormatee()).append("\n\n");
            sb.append("Titres :\n");
            a.getMorceaux().forEach(m -> sb.append("  ").append(m.getTitre()).append("  (").append(m.getDureeFormatee()).append(")\n"));
            detailArea.setText(sb.toString());

        } else if (type.contains("Artiste")) {
            Artiste ar = cat.getArtistes().stream().filter(x -> x.getNom().equals(nom)).findFirst().orElse(null);
            if (ar == null) return;
            selectionCourante = ar;
            StringBuilder sb = new StringBuilder();
            sb.append(ar.getNom()).append("\n");
            sb.append("\u2500".repeat(30)).append("\n\n");
            if (!ar.getBiographie().isEmpty()) sb.append(ar.getBiographie()).append("\n\n");
            if (ar.getGroupe() != null) sb.append("Groupe  :  ").append(ar.getGroupe().getNom()).append("\n");
            sb.append("Albums  :  ").append(ar.getAlbums().size()).append("\n");
            sb.append("Titres  :  ").append(ar.getMorceaux().size()).append("\n\n");
            if (!ar.getAlbums().isEmpty()) {
                sb.append("Discographie :\n");
                ar.getAlbums().forEach(alb -> sb.append("  ").append(alb.getTitre()).append(" (").append(alb.getAnnee()).append(")\n"));
            }
            detailArea.setText(sb.toString());

        } else if (type.contains("Groupe")) {
            Groupe g = cat.getGroupes().stream().filter(x -> x.getNom().equals(nom)).findFirst().orElse(null);
            if (g == null) return;
            selectionCourante = g;
            StringBuilder sb = new StringBuilder();
            sb.append(g.getNom()).append("\n");
            sb.append("\u2500".repeat(30)).append("\n\n");
            sb.append("Membres :  ");
            g.getMembres().forEach(m -> sb.append(m.getNom()).append("  "));
            sb.append("\n\nAlbums  :  ").append(g.getAlbums().size()).append("\n\n");
            if (!g.getAlbums().isEmpty()) {
                sb.append("Discographie :\n");
                g.getAlbums().forEach(a -> sb.append("  ").append(a.getTitre()).append(" (").append(a.getAnnee()).append(")\n"));
            }
            detailArea.setText(sb.toString());
        }
        detailArea.setCaretPosition(0);
    }

    private void actionDouble() {
        if (selectionCourante instanceof Morceau) ecouterSelection();
    }

    // ---------------------------------------------------------------
    //  Lecture
    // ---------------------------------------------------------------

    private void ecouterSelection() {
        if (!(selectionCourante instanceof Morceau)) return;
        Morceau m = (Morceau) selectionCourante;
        try { ctrl.ecouter(m); }
        catch (LimiteEcoutesAtteinte ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Limite atteinte", JOptionPane.WARNING_MESSAGE);
            return;
        }
        simulerLecture(m);
        int viewRow = resultTable.getSelectedRow();
        if (viewRow >= 0) selectionnerLigne(resultTable.convertRowIndexToModel(viewRow));
    }

    private void simulerLecture(Morceau m) {
        lectureLabel.setText("  " + m.getTitre() + "  \u2014  " + m.getAuteur().getNom());
        lectureLabel.setForeground(WelcomePanel.ACCENT);
        progressBar.setValue(0);
        progressBar.setString("0%");
        btnLire.setEnabled(false);
        int msTotal = Math.min(m.getDuree(), 3) * 1000;
        new SwingWorker<Void, Integer>() {
            protected Void doInBackground() throws Exception {
                int steps = 50;
                int delay = msTotal / steps;
                for (int i = 1; i <= steps; i++) { Thread.sleep(delay); publish(i * 2); }
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
                lectureLabel.setText("  " + m.getTitre() + "  \u2014 termine");
                lectureLabel.setForeground(WelcomePanel.FG_DIM);
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
        JOptionPane.showMessageDialog(frame, "Avis enregistre !");
        int viewRow = resultTable.getSelectedRow();
        if (viewRow >= 0) selectionnerLigne(resultTable.convertRowIndexToModel(viewRow));
    }

    // ---------------------------------------------------------------
    //  Utilitaires UI partages (static)
    // ---------------------------------------------------------------

    static void styleTable(JTable table) {
        table.setBackground(WelcomePanel.CARD_BG);
        table.setForeground(WelcomePanel.FG);
        table.setGridColor(WelcomePanel.TABLE_GRID);
        table.setSelectionBackground(WelcomePanel.SELECTION);
        table.setSelectionForeground(WelcomePanel.FG);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);
        JTableHeader header = table.getTableHeader();
        header.setBackground(WelcomePanel.BG);
        header.setForeground(WelcomePanel.FG_DIM);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, WelcomePanel.ACCENT));
        header.setReorderingAllowed(false);
    }

    /** Wrap un composant dans un JScrollPane avec bordure arrondie. */
    static JScrollPane wrapInRoundedScroll(Component comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBorder(new WelcomePanel.RoundedLineBorder(WelcomePanel.BORDER, 1, WelcomePanel.RADIUS));
        sp.getViewport().setBackground(WelcomePanel.CARD_BG);
        return sp;
    }

    static JButton btnAccent(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setBackground(WelcomePanel.ACCENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(WelcomePanel.ACCENT_HOVER); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(WelcomePanel.ACCENT); }
        });
        return btn;
    }

    static JButton btnNeutre(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(WelcomePanel.FG);
        btn.setBackground(WelcomePanel.BTN_BG);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new WelcomePanel.RoundedLineBorder(WelcomePanel.BORDER, 1, 10),
                new EmptyBorder(5, 14, 5, 14)));
        btn.setOpaque(true);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(WelcomePanel.BTN_HOVER); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(WelcomePanel.BTN_BG); }
        });
        return btn;
    }

    static JButton btnDanger(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setBackground(WelcomePanel.DANGER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(WelcomePanel.DANGER_HOVER); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(WelcomePanel.DANGER); }
        });
        return btn;
    }
}
