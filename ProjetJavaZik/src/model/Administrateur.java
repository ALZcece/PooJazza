package model;

/**
 * Représente un administrateur du système JavaZic.
 * Peut gérer le catalogue musical, les comptes abonnés et consulter les statistiques.
 */
public class Administrateur extends Utilisateur {
    private static final long serialVersionUID = 1L;

    private String login;
    private String motDePasse;

    /**
     * @param nom        nom de famille
     * @param prenom     prénom
     * @param login      identifiant de connexion
     * @param motDePasse mot de passe
     */
    public Administrateur(String nom, String prenom, String login, String motDePasse) {
        super(nom, prenom);
        this.login = login;
        this.motDePasse = motDePasse;
    }

    public String getLogin() { return login; }

    /** @return true si le mot de passe fourni est correct */
    public boolean verifierMotDePasse(String mdp) { return motDePasse.equals(mdp); }

    @Override
    public String toString() { return "[ADMIN] " + getNomComplet() + " (" + login + ")"; }
}
