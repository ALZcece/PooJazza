package model;

import model.exceptions.LimiteEcoutesAtteinte;

/**
 * Représente un visiteur non connecté.
 * Peut consulter le catalogue et écouter un nombre limité de morceaux par session.
 */
public class Visiteur extends Utilisateur {
    private static final long serialVersionUID = 1L;

    /** Nombre maximum d'écoutes autorisées par session pour un visiteur. */
    public static final int MAX_ECOUTES_SESSION = 5;

    private int nbEcoutesSession;

    public Visiteur() {
        super("Visiteur", "");
        this.nbEcoutesSession = 0;
    }

    public int getNbEcoutesSession() { return nbEcoutesSession; }

    /** @return true si le visiteur peut encore écouter un morceau */
    public boolean peutEcouter() {
        return nbEcoutesSession < MAX_ECOUTES_SESSION;
    }

    /**
     * Incrémente le compteur d'écoutes de la session.
     * @throws LimiteEcoutesAtteinte si la limite de session est dépassée
     */
    public void incrementerEcoutes() throws LimiteEcoutesAtteinte {
        if (!peutEcouter()) {
            throw new LimiteEcoutesAtteinte(
                    "Limite de " + MAX_ECOUTES_SESSION + " écoutes atteinte. "
                    + "Créez un compte pour écouter sans limite !");
        }
        nbEcoutesSession++;
    }

    /** Réinitialise le compteur d'écoutes (nouveau démarrage de session). */
    public void reinitialiserSession() { nbEcoutesSession = 0; }
}
