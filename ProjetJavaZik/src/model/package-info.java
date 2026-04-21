/**
 * Couche <b>Modèle</b> de l'application JavaZic (pattern MVC).
 * <p>
 * Regroupe toutes les classes métier qui représentent les données et les règles
 * de l'application : catalogue de musique, utilisateurs, playlists, historique
 * d'écoute, avis, etc. Ces classes sont indépendantes de la vue : elles ne
 * savent pas si l'utilisateur interagit via la console ou l'interface Swing.
 * </p>
 *
 * <h2>Entités musicales</h2>
 * <ul>
 *   <li>{@link model.Catalogue} — contient l'ensemble des morceaux, albums et auteurs.</li>
 *   <li>{@link model.Morceau} — un titre musical.</li>
 *   <li>{@link model.Album} — un regroupement de morceaux.</li>
 *   <li>{@link model.AuteurMusical} — super-classe de {@link model.Artiste} et {@link model.Groupe}.</li>
 *   <li>{@link model.Genre} — énumération des genres musicaux.</li>
 *   <li>{@link model.Avis} — note et commentaire laissés sur un morceau.</li>
 * </ul>
 *
 * <h2>Utilisateurs</h2>
 * <ul>
 *   <li>{@link model.Utilisateur} — super-classe abstraite.</li>
 *   <li>{@link model.Visiteur} — utilisateur non connecté (écoutes limitées).</li>
 *   <li>{@link model.Abonne} — utilisateur inscrit (playlists, historique, avis).</li>
 *   <li>{@link model.Administrateur} — gère le catalogue et les comptes.</li>
 * </ul>
 *
 * <h2>Autres</h2>
 * <ul>
 *   <li>{@link model.Playlist} — liste de morceaux créée par un abonné.</li>
 *   <li>{@link model.HistoriqueEcoute} — suivi des écoutes d'un utilisateur.</li>
 * </ul>
 *
 * @see model.exceptions
 */
package model;
