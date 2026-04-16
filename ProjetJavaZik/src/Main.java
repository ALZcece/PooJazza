import controller.Controller;
import controller.GUIController;
import view.gui.MainFrame;

import javax.swing.*;

/**
 * Point d'entrée de l'application JavaZic.
 * Lance l'interface graphique par défaut.
 * Passer l'argument {@code --console} ou mettre {@code MODE_CONSOLE = true} pour le mode console.
 */
public class Main {

    /** Mettre à true pour lancer en mode console, false pour le mode graphique. */
    private static final boolean MODE_CONSOLE = false;

    public static void main(String[] args) {
        boolean consoleMode = MODE_CONSOLE || (args.length > 0 && args[0].equalsIgnoreCase("--console"));

        if (consoleMode) {
            new Controller().demarrer();
        } else {
            SwingUtilities.invokeLater(() -> {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}
                GUIController ctrl = new GUIController();
                MainFrame frame = new MainFrame(ctrl);
                frame.setVisible(true);
            });
        }
    }
}
