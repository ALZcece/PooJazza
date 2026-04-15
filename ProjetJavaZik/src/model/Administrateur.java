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
    /* Cette fonction est le constructeur de la classe Administrateur.
     Elle permet de créer un nouvel administrateur avec toutes les informations nécessaires.
     Le constructeur appelle d’abord celui de la classe Utilisateur
     afin d’initialiser les attributs communs comme le nom et le prénom.
     Ensuite, il initialise les attributs spécifiques à l’administrateur,
     à savoir le login et le mot de passe.
     Le login sert d’identifiant unique pour se connecter au système,
     tandis que le mot de passe permet de sécuriser l’accès.
     Comme pour les abonnés, le mot de passe est ici stocké en clair
     pour simplifier le projet, mais dans une application réelle,
     il faudrait utiliser un système de hachage sécurisé.
     Ce constructeur permet donc d’instancier un administrateur prêt à être utilisé
     dans les opérations de gestion du système (catalogue, abonnés, etc.)*/

    public String getLogin() { return login; }
    /* Cette fonction getLogin permet de récupérer le login de l’administrateur.
     Le login est l’identifiant utilisé lors de la connexion au système.
     Cette méthode ne modifie pas l’objet,
     elle se contente de renvoyer la valeur stockée.
     Elle est utile lors des vérifications d’authentification
     ou pour afficher les informations de l’administrateur dans l’interface.
     Elle constitue donc un simple accesseur à l’attribut login.*/

    /** @return true si le mot de passe fourni est correct */
    public boolean verifierMotDePasse(String mdp) { return motDePasse.equals(mdp); }
    /* Cette fonction verifierMotDePasse permet de vérifier
     si le mot de passe fourni en paramètre correspond
     à celui enregistré dans l’objet Administrateur.
     Elle compare directement les deux chaînes de caractères.
     Si elles sont identiques, la fonction retourne true,
     sinon elle retourne false.
     Cette méthode est utilisée lors de la connexion de l’administrateur,
     afin de valider son identité.
     Comme pour les autres classes, la comparaison est simplifiée ici,
     sans chiffrement ni sécurisation avancée.
     Elle joue un rôle central dans le processus d’authentification.*/

    
    @Override
    public String toString() { return "[ADMIN] " + getNomComplet() + " (" + login + ")"; }
    /* Cette fonction toString permet de fournir une représentation textuelle
     de l’objet Administrateur.
     Elle retourne une chaîne de caractères contenant :
     - le préfixe [ADMIN] pour indiquer clairement le rôle de l’utilisateur
     - le nom complet (nom + prénom) hérité de la classe Utilisateur
     - le login entre parenthèses
     Cette représentation est particulièrement utile pour l’affichage
     dans les interfaces, les logs ou les listes d’utilisateurs.
     Elle permet d’identifier rapidement un administrateur
     et de distinguer son rôle par rapport aux autres types d’utilisateurs*/
}
