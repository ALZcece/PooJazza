/**
 * Exceptions métier levées par la couche {@link model modèle}.
 * <p>
 * Ces exceptions expriment des erreurs propres au domaine de l'application
 * (règles de gestion non respectées), et non des erreurs techniques. Elles
 * sont destinées à être attrapées par la couche {@link controller contrôleur}
 * afin d'afficher un message clair à l'utilisateur.
 * </p>
 *
 * <h2>Liste</h2>
 * <ul>
 *   <li>{@link model.exceptions.CompteInactifException} — tentative d'action sur un compte désactivé.</li>
 *   <li>{@link model.exceptions.ElementIntrouvableException} — morceau / album / utilisateur introuvable.</li>
 *   <li>{@link model.exceptions.LimiteEcoutesAtteinte} — un visiteur a dépassé son quota d'écoutes gratuites.</li>
 *   <li>{@link model.exceptions.MorceauDejaExistantException} — ajout d'un morceau déjà présent dans le catalogue.</li>
 * </ul>
 */
package model.exceptions;
