package view.gui;

import controller.GUIController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Fenêtre principale de l'application JavaZic (interface graphique).
 * Gère la navigation entre les écrans via un {@link CardLayout}.
 */
public class MainFrame extends JFrame {

    private final GUIController ctrl;
    private final CardLayout cardLayout;
    private final JPanel contentPane;

    // Noms des cartes
    public static final String CARD_WELCOME   = "welcome";
    public static final String CARD_CATALOGUE = "catalogue";
    public static final String CARD_ABONNE    = "abonne";
    public static final String CARD_ADMIN     = "admin";

    private WelcomePanel   welcomePanel;
    private CataloguePanel cataloguePanel;
    private AbonnePanel    abonnePanel;
    private AdminPanel     adminPanel;

    public MainFrame(GUIController ctrl) {
        this.ctrl = ctrl;
        this.cardLayout  = new CardLayout();
        this.contentPane = new JPanel(cardLayout);
        initUI();
    }

    private void initUI() {
        setTitle("♪ JavaZic");
        setSize(1050, 680);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Sauvegarde automatique à la fermeture
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                ctrl.sauvegarder();
                dispose();
                System.exit(0);
            }
        });

        // Création des panels
        welcomePanel   = new WelcomePanel(ctrl, this);
        cataloguePanel = new CataloguePanel(ctrl, this, false);
        abonnePanel    = new AbonnePanel(ctrl, this);
        adminPanel     = new AdminPanel(ctrl, this);

        contentPane.add(welcomePanel,   CARD_WELCOME);
        contentPane.add(cataloguePanel, CARD_CATALOGUE);
        contentPane.add(abonnePanel,    CARD_ABONNE);
        contentPane.add(adminPanel,     CARD_ADMIN);

        setContentPane(contentPane);
        showCard(CARD_WELCOME);
    }

    /**
     * Navigue vers l'écran indiqué et rafraîchit son contenu.
     */
    public void showCard(String name) {
        cardLayout.show(contentPane, name);
        switch (name) {
            case CARD_ABONNE:    abonnePanel.rafraichir();    break;
            case CARD_ADMIN:     adminPanel.rafraichir();     break;
            case CARD_CATALOGUE: cataloguePanel.rafraichir(); break;
        }
    }

    /** Recrée le panel abonné (après login) et l'affiche. */
    public void ouvrirEspaceAbonne() {
        contentPane.remove(abonnePanel);
        abonnePanel = new AbonnePanel(ctrl, this);
        contentPane.add(abonnePanel, CARD_ABONNE);
        showCard(CARD_ABONNE);
    }

    /** Recrée le panel admin (après login) et l'affiche. */
    public void ouvrirEspaceAdmin() {
        contentPane.remove(adminPanel);
        adminPanel = new AdminPanel(ctrl, this);
        contentPane.add(adminPanel, CARD_ADMIN);
        showCard(CARD_ADMIN);
    }
}
