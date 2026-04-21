/**
 * Couche <b>Vue</b> de l'application JavaZic — version graphique Swing.
 * <p>
 * Ensemble des fenêtres et panneaux composant l'interface graphique.
 * Le thème visuel (palette de verts façon Spotify) est défini dans
 * {@link view.gui.WelcomePanel} et réutilisé par tous les autres panneaux.
 * </p>
 *
 * <h2>Fenêtre principale</h2>
 * <ul>
 *   <li>{@link view.gui.MainFrame} — fenêtre racine qui bascule entre les panneaux.</li>
 * </ul>
 *
 * <h2>Panneaux</h2>
 * <ul>
 *   <li>{@link view.gui.WelcomePanel} — écran d'accueil (connexion / inscription) et palette de couleurs partagée.</li>
 *   <li>{@link view.gui.CataloguePanel} — navigation dans le catalogue musical.</li>
 *   <li>{@link view.gui.AbonnePanel} — espace personnel d'un abonné (playlists, historique).</li>
 *   <li>{@link view.gui.AdminPanel} — interface de gestion pour l'administrateur.</li>
 * </ul>
 *
 * @see view
 */
package view.gui;
