import controller.Controller;
import controller.GUIController;
import view.gui.MainFrame;

import javax.swing.*;

/**
 * Point d'entrée de l'application JavaZic.
 * Lance l'interface graphique par défaut.
 * Passer l'argument {@code --console} pour le mode console.
 */
public class Main {
    public static void main(String[] args) {
        boolean consoleMode = args.length > 0 && args[0].equalsIgnoreCase("--console");

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
