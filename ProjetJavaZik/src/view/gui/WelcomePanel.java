package view.gui;

import controller.GUIController;
import model.Abonne;
import model.exceptions.CompteInactifException;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Ecran d'accueil de JavaZic — theme Spotify clair.
 */
public class WelcomePanel extends JPanel {

    private final GUIController ctrl;
    private final MainFrame frame;

    // ── Palette Spotify Light ──────────────────────────────────────
    static final Color BG          = new Color(245, 245, 245);
    static final Color CARD_BG     = Color.WHITE;
    static final Color HEADER_BG   = Color.WHITE;
    static final Color ACCENT      = new Color(29, 185, 84);
    static final Color ACCENT_HOVER= new Color(25, 160, 72);
    static final Color ACCENT2     = new Color(30, 215, 96);
    static final Color FG          = new Color(25, 20, 20);
    static final Color FG_DIM      = new Color(115, 115, 115);
    static final Color BTN_BG      = new Color(240, 240, 240);
    static final Color BTN_HOVER   = new Color(225, 225, 225);
    static final Color BORDER      = new Color(222, 222, 222);
    static final Color SELECTION   = new Color(200, 240, 215);
    static final Color TABLE_GRID  = new Color(240, 240, 240);
    static final Color DANGER      = new Color(235, 70, 70);
    static final Color DANGER_HOVER= new Color(210, 55, 55);
    static final int   RADIUS      = 14;
    // ───────────────────────────────────────────────────────────────

    public WelcomePanel(GUIController ctrl, MainFrame frame) {
        this.ctrl  = ctrl;
        this.frame = frame;
        buildUI();
    }

    private void buildUI() {
        setBackground(BG);
        setLayout(new GridBagLayout());

        // Carte centrale avec ombre
        RoundedPanel card = new RoundedPanel(RADIUS, CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(50, 70, 50, 70));

        // Titre
        JLabel titre = new JLabel("JavaZic");
        titre.setFont(new Font("SansSerif", Font.BOLD, 44));
        titre.setForeground(ACCENT);
        titre.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sous = new JLabel("Votre plateforme musicale");
        sous.setFont(new Font("SansSerif", Font.PLAIN, 15));
        sous.setForeground(FG_DIM);
        sous.setAlignmentX(CENTER_ALIGNMENT);

        card.add(titre);
        card.add(Box.createVerticalStrut(8));
        card.add(sous);
        card.add(Box.createVerticalStrut(44));

        card.add(creerBoutonVert("Connexion Administrateur", e -> dialogConnexionAdmin()));
        card.add(Box.createVerticalStrut(12));
        card.add(creerBoutonVert("Connexion Abonne", e -> dialogConnexionAbonne()));
        card.add(Box.createVerticalStrut(12));
        card.add(creerBoutonNeutre("Creer un compte", e -> dialogCreerCompte()));
        card.add(Box.createVerticalStrut(12));
        card.add(creerBoutonNeutre("Continuer en visiteur", e -> modeVisiteur()));
        card.add(Box.createVerticalStrut(36));

        JLabel footer = new JLabel("Admin : admin / 1234");
        footer.setFont(new Font("Monospaced", Font.PLAIN, 11));
        footer.setForeground(new Color(180, 180, 180));
        footer.setAlignmentX(CENTER_ALIGNMENT);
        card.add(footer);

        add(card);
    }

    private JButton creerBoutonVert(String texte, java.awt.event.ActionListener action) {
        JButton btn = new RoundedButton(texte, ACCENT, Color.WHITE, ACCENT_HOVER);
        btn.addActionListener(action);
        return btn;
    }

    private JButton creerBoutonNeutre(String texte, java.awt.event.ActionListener action) {
        JButton btn = new RoundedButton(texte, BTN_BG, FG, BTN_HOVER);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new RoundedLineBorder(BORDER, 1, RADIUS),
                new EmptyBorder(8, 16, 8, 16)));
        btn.addActionListener(action);
        return btn;
    }

    // ------------------------------------------------------------------

    private void dialogConnexionAdmin() {
        JTextField loginField = new JTextField(15);
        JPasswordField mdpField = new JPasswordField(15);
        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.add(new JLabel("Login :")); p.add(loginField);
        p.add(new JLabel("Mot de passe :")); p.add(mdpField);
        int res = JOptionPane.showConfirmDialog(frame, p, "Connexion Administrateur",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        if (ctrl.connecterAdmin(loginField.getText().trim(), new String(mdpField.getPassword()))) {
            frame.ouvrirEspaceAdmin();
        } else {
            JOptionPane.showMessageDialog(frame, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void dialogConnexionAbonne() {
        JTextField loginField = new JTextField(15);
        JPasswordField mdpField = new JPasswordField(15);
        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.add(new JLabel("Login :")); p.add(loginField);
        p.add(new JLabel("Mot de passe :")); p.add(mdpField);
        int res = JOptionPane.showConfirmDialog(frame, p, "Connexion Abonne",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            Abonne a = ctrl.connecterAbonne(loginField.getText().trim(), new String(mdpField.getPassword()));
            if (a != null) {
                frame.ouvrirEspaceAbonne();
            } else {
                JOptionPane.showMessageDialog(frame, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (CompteInactifException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Compte suspendu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void dialogCreerCompte() {
        JTextField nomField    = new JTextField(15);
        JTextField prenomField = new JTextField(15);
        JTextField loginField  = new JTextField(15);
        JPasswordField mdpField = new JPasswordField(15);
        JPanel p = new JPanel(new GridLayout(4, 2, 8, 8));
        p.add(new JLabel("Nom :"));          p.add(nomField);
        p.add(new JLabel("Prenom :"));       p.add(prenomField);
        p.add(new JLabel("Login :"));        p.add(loginField);
        p.add(new JLabel("Mot de passe :")); p.add(mdpField);
        int res = JOptionPane.showConfirmDialog(frame, p, "Creer un compte",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;
        try {
            ctrl.creerCompte(nomField.getText().trim(), prenomField.getText().trim(),
                    loginField.getText().trim(), new String(mdpField.getPassword()));
            frame.ouvrirEspaceAbonne();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void modeVisiteur() {
        ctrl.demarrerSessionVisiteur();
        frame.showCard(MainFrame.CARD_CATALOGUE);
    }

    // ================================================================
    //  Composants UI reutilisables
    // ================================================================

    /** Panel avec coins arrondis. */
    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bgColor = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Ombre legere
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 3, getHeight() - 3, radius + 2, radius + 2));
            // Fond
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 3, getHeight() - 3, radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Bouton avec coins arrondis. */
    static class RoundedButton extends JButton {
        private Color bgColor;

        RoundedButton(String text, Color bg, Color fg, Color hover) {
            super(text);
            this.bgColor = bg;
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setForeground(fg);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setAlignmentX(CENTER_ALIGNMENT);
            setMaximumSize(new Dimension(300, 46));
            setPreferredSize(new Dimension(300, 46));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { bgColor = hover; repaint(); }
                public void mouseExited(MouseEvent e)  { bgColor = bg; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), RADIUS, RADIUS));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Bordure avec coins arrondis. */
    static class RoundedLineBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        RoundedLineBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + 0.5f, y + 0.5f, w - 1, h - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 4, thickness + 2, thickness + 4);
        }
    }
}
