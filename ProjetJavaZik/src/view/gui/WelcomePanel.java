package view.gui;

import controller.GUIController;
import model.Abonne;
import model.exceptions.CompteInactifException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Écran d'accueil de JavaZic.
 * Propose la connexion admin, la connexion abonné, la création de compte et le mode visiteur.
 */
public class WelcomePanel extends JPanel {

    private final GUIController ctrl;
    private final MainFrame frame;

    // Palette de couleurs
    static final Color BG        = new Color(28, 28, 35);
    static final Color ACCENT    = new Color(99, 179, 237);
    static final Color ACCENT2   = new Color(72, 199, 142);
    static final Color FG        = new Color(230, 230, 235);
    static final Color FG_DIM    = new Color(150, 150, 160);
    static final Color BTN_BG    = new Color(45, 45, 58);
    static final Color BTN_HOVER = new Color(60, 60, 78);

    public WelcomePanel(GUIController ctrl, MainFrame frame) {
        this.ctrl  = ctrl;
        this.frame = frame;
        buildUI();
    }

    private void buildUI() {
        setBackground(BG);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(new Color(38, 38, 50));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Titre
        JLabel titre = new JLabel("♪  JavaZic");
        titre.setFont(new Font("SansSerif", Font.BOLD, 38));
        titre.setForeground(ACCENT);
        titre.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sous = new JLabel("Votre plateforme musicale");
        sous.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sous.setForeground(FG_DIM);
        sous.setAlignmentX(CENTER_ALIGNMENT);

        card.add(titre);
        card.add(Box.createVerticalStrut(6));
        card.add(sous);
        card.add(Box.createVerticalStrut(36));

        // Boutons
        card.add(creerBouton("  Connexion Administrateur", ACCENT,   e -> dialogConnexionAdmin()));
        card.add(Box.createVerticalStrut(12));
        card.add(creerBouton("  Connexion Abonné",         ACCENT2,  e -> dialogConnexionAbonne()));
        card.add(Box.createVerticalStrut(12));
        card.add(creerBouton("  Créer un compte",          FG,       e -> dialogCreerCompte()));
        card.add(Box.createVerticalStrut(12));
        card.add(creerBouton("  Continuer en visiteur",    FG_DIM,   e -> modeVisiteur()));
        card.add(Box.createVerticalStrut(28));

        JLabel footer = new JLabel("Admin : admin / 1234");
        footer.setFont(new Font("Monospaced", Font.PLAIN, 11));
        footer.setForeground(FG_DIM);
        footer.setAlignmentX(CENTER_ALIGNMENT);
        card.add(footer);

        add(card);
    }

    private JButton creerBouton(String texte, Color couleur, java.awt.event.ActionListener action) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(couleur);
        btn.setBackground(BTN_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(280, 44));
        btn.setPreferredSize(new Dimension(280, 44));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(BTN_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(BTN_BG); }
        });
        btn.addActionListener(action);
        return btn;
    }

    // ------------------------------------------------------------------

    private void dialogConnexionAdmin() {
        JTextField loginField = new JTextField(15);
        JPasswordField mdpField = new JPasswordField(15);
        JPanel p = new JPanel(new GridLayout(2, 2, 6, 6));
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
        JPanel p = new JPanel(new GridLayout(2, 2, 6, 6));
        p.add(new JLabel("Login :")); p.add(loginField);
        p.add(new JLabel("Mot de passe :")); p.add(mdpField);
        int res = JOptionPane.showConfirmDialog(frame, p, "Connexion Abonné",
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
        JPanel p = new JPanel(new GridLayout(4, 2, 6, 6));
        p.add(new JLabel("Nom :"));          p.add(nomField);
        p.add(new JLabel("Prénom :"));       p.add(prenomField);
        p.add(new JLabel("Login :"));        p.add(loginField);
        p.add(new JLabel("Mot de passe :")); p.add(mdpField);
        int res = JOptionPane.showConfirmDialog(frame, p, "Créer un compte",
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
}
