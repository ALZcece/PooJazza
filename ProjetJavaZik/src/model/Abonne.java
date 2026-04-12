package model;

import model.exceptions.ElementIntrouvableException;
import model.exceptions.LimiteEcoutesAtteinte;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Représente un abonné : utilisateur connecté avec un compte persistant.
 * Hérite de {@link Visiteur} mais dispose d'écoutes illimitées, de playlists et d'un historique.
 */
public class Abonne extends Visiteur {
    private static final long serialVersionUID = 1L;

    private String id;
    private String login;
    private String motDePasse;
    private ArrayList<Playlist> playlists;
    private HistoriqueEcoute historique;
    private boolean actif;

    /**
     * @param nom        nom de famille de l'abonné
     * @param prenom     prénom de l'abonné
     * @param login      identifiant de connexion unique
     * @param motDePasse mot de passe (stocké en clair ici ; à hasher en production)
     */
    public Abonne(String nom, String prenom, String login, String motDePasse) {
        super();
        this.nom = nom;
        this.prenom = prenom;
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.motDePasse = motDePasse;
        this.playlists = new ArrayList<>();
        this.historique = new HistoriqueEcoute();
        this.actif = true;
    }
    /* Cette fonction est le constructeur de la classe Abonne.
     Elle permet d’initialiser un nouvel utilisateur abonné
     avec toutes les informations nécessaires à son fonctionnement dans l’application.
     Le constructeur appelle d’abord le constructeur de la classe parente Visiteur
     afin de récupérer les propriétés communes à tous les utilisateurs.
     Ensuite, il affecte le nom et le prénom reçus en paramètres.
     Il génère également un identifiant unique grâce à UUID,
     ce qui permet de distinguer chaque abonné de façon sûre,
     même si plusieurs utilisateurs ont le même nom ou le même login.
     Le login et le mot de passe sont ensuite enregistrés.
     Le mot de passe est stocké ici en clair pour simplifier le projet,
     même si, dans une application réelle, il faudrait le chiffrer ou le hasher.
     Le constructeur initialise aussi une liste vide de playlists,
     car un nouvel abonné ne possède encore aucune playlist au départ.
     Il crée également un historique d’écoute vide
     afin de pouvoir mémoriser les morceaux écoutés par la suite.
     Enfin, le compte est marqué comme actif par défaut,
     ce qui signifie qu’il peut se connecter et utiliser les fonctionnalités normalement.
     Ce constructeur met donc en place l’état complet et cohérent d’un abonné.*/

    public String getId() { return id; } 
    /* Cette fonction getId permet de récupérer l’identifiant unique de l’abonné.
     Cet identifiant est utilisé pour distinguer un abonné d’un autre
     indépendamment du nom ou du login.
     La méthode ne modifie pas l’objet,
     elle se contente de renvoyer la valeur stockée dans l’attribut id.
     Elle est utile pour les comparaisons, la persistance
     ou toute logique nécessitant une identité stable.*/
    
    public String getLogin() { return login; }
    /* Cette fonction getLogin permet d’obtenir le login de l’abonné.
     Le login correspond à l’identifiant utilisé pour se connecter à l’application.
     Cette méthode fournit simplement l’accès à cet attribut.
     Elle est utile dans les opérations d’authentification,
     d’affichage ou d’administration des comptes.*/
    
    public String getMotDePasse() { return motDePasse; }
    /* Cette fonction getMotDePasse permet de récupérer le mot de passe enregistré pour l’abonné.
     Elle donne un accès direct à la valeur de l’attribut motDePasse.
     Dans ce projet, cela peut être utile pour certaines vérifications simples.
     En pratique, dans une vraie application,
     ce type de getter serait généralement évité pour des raisons de sécurité.
     Ici, il reste cohérent avec l’approche simplifiée choisie pour le projet.*/
    
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    /* Cette fonction setMotDePasse permet de modifier le mot de passe de l’abonné.
     Elle remplace simplement l’ancienne valeur
     par la nouvelle chaîne reçue en paramètre.
     Cette méthode pourrait être utilisée dans une fonctionnalité de changement de mot de passe.
     Elle ne fait ici aucun contrôle particulier sur la qualité ou la sécurité du mot de passe.
     Son rôle est donc de mettre à jour directement cet attribut.*/
    
    public ArrayList<Playlist> getPlaylists() { return playlists; }
    /* Cette fonction getPlaylists permet d’accéder à la liste des playlists de l’abonné.
     Elle retourne la collection complète des playlists associées à ce compte.
     Cette méthode est utile pour afficher, parcourir ou gérer les playlists dans l’application.
     Elle ne crée pas de nouvelle liste,
     elle renvoie directement celle stockée dans l’objet.
     Elle constitue donc un point d’accès aux playlists personnelles de l’abonné.*/
    
    public HistoriqueEcoute getHistorique() { return historique; }
    /* Cette fonction getHistorique permet de récupérer l’historique d’écoute de l’abonné.
     Cet historique contient les morceaux écoutés au fil du temps.
     La méthode est utile pour consulter les écoutes passées
     ou pour mettre à jour l’historique après une nouvelle lecture.
     Elle ne modifie pas l’objet,
     elle donne simplement accès à la structure déjà existante.*/
    
    public boolean isActif() { return actif; }
    /* Cette fonction isActif permet de savoir si le compte abonné est actuellement actif.
     Elle retourne true si le compte est autorisé à fonctionner normalement,
     et false s’il a été suspendu.
     Cette information est importante pour la connexion,
     l’écoute de morceaux et l’accès aux fonctionnalités.
     La méthode sert donc à consulter l’état du compte sans le modifier.*/
    
    public void setActif(boolean actif) { this.actif = actif; }
    /* Cette fonction setActif permet de modifier l’état du compte abonné.
     Elle peut être utilisée pour suspendre un compte ou au contraire le réactiver.
     La nouvelle valeur est directement affectée à l’attribut actif.
     Cette méthode est particulièrement utile dans les opérations d’administration.
     Elle centralise donc la mise à jour du statut du compte.*/

    /** @return true si le mot de passe fourni correspond */
    public boolean verifierMotDePasse(String mdp) { return motDePasse.equals(mdp); }
    /* Cette fonction verifierMotDePasse permet de comparer
     le mot de passe fourni en paramètre avec celui enregistré dans l’objet.
     Elle retourne true si les deux chaînes sont identiques,
     et false dans le cas contraire.
     Cette méthode est utilisée lors de l’authentification d’un abonné.
     Elle constitue donc la vérification directe de la validité du mot de passe.
     Dans ce projet, la comparaison se fait en clair,
     ce qui reste volontairement simplifié*/
    
    /** Un abonné actif peut toujours écouter (pas de limite de session). */
    @Override
    public boolean peutEcouter() { return actif; }
    /* Cette fonction peutEcouter redéfinit le comportement hérité de la classe Visiteur.
     Pour un abonné, il n’existe pas de limite d’écoutes par session.
     La seule condition à vérifier est donc que le compte soit actif.
     Si le compte est actif, la fonction retourne true.
     Si le compte est suspendu, elle retourne false.
     Cette redéfinition adapte la logique générale d’écoute
     au cas particulier des abonnés*/
    
    /** Pour un abonné, seul le statut actif est vérifié (pas de compteur de session). */
    @Override
    public void incrementerEcoutes() throws LimiteEcoutesAtteinte {
        if (!actif)
            throw new LimiteEcoutesAtteinte("Votre compte est suspendu. Contactez un administrateur.");
    }
    /* Cette fonction incrementerEcoutes redéfinit également la logique héritée de Visiteur.
     Pour un simple visiteur, cette méthode sert à compter les écoutes
     et à bloquer l’utilisateur lorsqu’il dépasse une limite.
     Pour un abonné, il n’y a pas de compteur à incrémenter,
     car les écoutes sont illimitées.
     La méthode vérifie uniquement si le compte est actif.
     Si le compte est suspendu, elle lève une exception LimiteEcoutesAtteinte
     avec un message indiquant que l’utilisateur doit contacter un administrateur.
     Cette exception est ici réutilisée pour signaler qu’une écoute n’est pas autorisée.
     Si le compte est actif, la méthode ne fait rien d’autre
     et laisse l’écoute se poursuivre normalement.*/

    /**
     * Crée une nouvelle playlist et l'ajoute à la liste de l'abonné.
     * @param nom nom de la playlist
     * @return la playlist créée
     */
    public Playlist creerPlaylist(String nom) {
        Playlist p = new Playlist(nom, this);
        playlists.add(p);
        return p;
    }
    /* Cette fonction creerPlaylist permet à l’abonné de créer une nouvelle playlist.
     Elle reçoit en paramètre le nom de la playlist à créer.
     La méthode instancie d’abord un nouvel objet Playlist
     en indiquant ce nom et l’abonné courant comme propriétaire.
     Elle ajoute ensuite cette nouvelle playlist à la liste des playlists de l’abonné.
     Enfin, elle retourne l’objet créé.
     Cette méthode est essentielle pour la gestion personnalisée des morceaux,
     car elle permet à chaque abonné de constituer ses propres sélections musicales.
     Elle assure aussi que la playlist créée est bien rattachée à son propriétaire.*/

    /**
     * Supprime une playlist de l'abonné.
     * @throws ElementIntrouvableException si la playlist n'appartient pas à cet abonné
     */
    public void supprimerPlaylist(Playlist p) throws ElementIntrouvableException {
        if (!playlists.remove(p))
            throw new ElementIntrouvableException("Playlist introuvable.");
    }
    /* Cette fonction supprimerPlaylist permet de retirer une playlist
     de la collection personnelle de l’abonné.
     Elle tente de supprimer directement l’objet playlist reçu en paramètre
     de la liste des playlists.
     La méthode remove retourne true si la suppression a effectivement eu lieu,
     et false si la playlist n’était pas présente.
     Si la playlist n’est pas trouvée,
     la méthode lève une exception ElementIntrouvableException.
     Cela permet au contrôleur ou à la vue de gérer proprement l’erreur
     et d’informer l’utilisateur.
     Cette fonction garantit donc que seule une playlist réellement possédée
     peut être supprimée*/

    /**
     * Recherche une playlist par son nom (insensible à la casse).
     * @throws ElementIntrouvableException si aucune playlist ne correspond
     */
    public Playlist getPlaylistParNom(String nom) throws ElementIntrouvableException {
        return playlists.stream()
                .filter(p -> p.getNom().equalsIgnoreCase(nom))
                .findFirst()
                .orElseThrow(() -> new ElementIntrouvableException("Aucune playlist nommée \"" + nom + "\"."));
    }
    /* Cette fonction getPlaylistParNom permet de retrouver
     une playlist précise à partir de son nom.
     La recherche est effectuée sans tenir compte des majuscules et minuscules,
     ce qui rend l’utilisation plus souple.
     La méthode utilise ici les streams Java pour parcourir la liste des playlists
     de manière concise et expressive.
     Elle filtre les playlists en gardant uniquement celles
     dont le nom correspond à celui recherché.
     Elle récupère ensuite la première correspondance trouvée.
     Si aucune playlist ne correspond,
     elle lève une exception ElementIntrouvableException
     avec un message explicite.
     Cette méthode est utile pour accéder rapidement à une playlist
     sans avoir à parcourir manuellement toute la collection*/

    @Override
    public String toString() {
        return login + " (" + getNomComplet() + ")" + (actif ? "" : " [SUSPENDU]");
    }
    /*Cette fonction toString fournit une représentation textuelle de l’abonné.
     Elle retourne une chaîne contenant le login de l’utilisateur,
     suivi de son nom complet entre parenthèses.
     Si le compte est suspendu, la mention [SUSPENDU] est ajoutée à la fin.
     Cette représentation est particulièrement utile pour l’affichage
     dans des listes, des menus ou des interfaces d’administration.
     Elle permet de voir rapidement l’identité de l’abonné
     ainsi que son statut.
     Cette redéfinition améliore donc la lisibilité des objets Abonne
     lorsqu’ils sont affichés directement.*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Abonne)) return false;
        return id.equals(((Abonne) o).id);
    }
    /* Cette fonction equals permet de définir
     la manière dont deux objets Abonne sont comparés.
     Elle commence par vérifier s’il s’agit exactement du même objet en mémoire.
     Dans ce cas, elle retourne immédiatement true.
     Ensuite, elle vérifie que l’objet comparé est bien un Abonne.
     Si ce n’est pas le cas, elle retourne false.
     Enfin, elle compare les identifiants uniques des deux abonnés.
     Deux abonnés sont donc considérés comme égaux
     s’ils possèdent le même id.
     Cette définition est importante pour garantir
     des comparaisons cohérentes dans les collections Java*/

    @Override
    public int hashCode() { return id.hashCode(); }
    /* Cette fonction hashCode fournit le code de hachage associé à l’abonné.
     Elle est basée sur l’identifiant unique id.
     Cette redéfinition est nécessaire pour rester cohérente
     avec la méthode equals.
     En effet, deux objets considérés comme égaux
     doivent produire le même hashCode.
     Cette méthode est donc essentielle
     pour le bon fonctionnement des structures de données
     comme HashMap, HashSet ou d’autres collections basées sur le hachage*/
}
