/**
 * Couche <b>Contrôleur</b> de l'application JavaZic (pattern MVC).
 * <p>
 * Cette couche fait le lien entre la <i>vue</i> (console ou interface graphique)
 * et le <i>modèle</i> (catalogue, utilisateurs, playlists). Elle reçoit les
 * actions de l'utilisateur, applique les règles métier et met à jour le modèle
 * puis demande à la vue de se rafraîchir.
 * </p>
 *
 * <h2>Classes principales</h2>
 * <ul>
 *   <li>{@link controller.Controller} — contrôleur de la version console.</li>
 *   <li>{@link controller.GUIController} — contrôleur de l'interface graphique Swing.</li>
 *   <li>{@link controller.DataLoader} — chargement et sauvegarde des données
 *       (sérialisation / fichiers CSV).</li>
 * </ul>
 */
package controller;
