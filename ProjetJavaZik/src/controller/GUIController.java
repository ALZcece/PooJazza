package controller;

import model.*;
import model.exceptions.*;

import java.io.*;
import java.util.ArrayList;

/**
 * Contrôleur pour l'interface graphique JavaZic.
 * Expose des méthodes métier appelées par les composants Swing.
 * Partage les mêmes fichiers de sauvegarde que le contrôleur console.
 */
public class GUIController {

    private Catalogue catalogue;
    private ArrayList<Abonne> abonnes;
    private final Administrateur admin;
    private Utilisateur utilisateurCourant;

    private static final String DATA_DIR       = "data";
    private static final String CATALOGUE_FILE = DATA_DIR + "/catalogue.ser";
    private static final String ABONNES_FILE   = DATA_DIR + "/abonnes.ser";

    public GUIController() {
        this.admin   = new Administrateur("Admin", "Super", "admin", "1234");
        this.abonnes = new ArrayList<>();
        chargerDonnees();
    }
    /* Cette fonction est le constructeur du contrôleur graphique.
     Elle initialise d’abord l’administrateur par défaut de l’application
     avec ses identifiants fixes.
     Elle initialise également la liste des abonnés par une liste vide,
     afin d’éviter toute valeur nulle au démarrage.
     Enfin, elle appelle la méthode chargerDonnees
     pour recharger le catalogue et les abonnés enregistrés précédemment.
     Ce constructeur prépare donc tout l’état nécessaire
     au bon fonctionnement de l’interface graphique dès son lancement.*/

    // =========================================================
    //  SESSION
    // =========================================================

    public Utilisateur getUtilisateurCourant() { return utilisateurCourant; } 
    /* Cette fonction getUtilisateurCourant permet de récupérer
     l’utilisateur actuellement connecté dans l’application.
     Elle peut retourner un administrateur, un abonné, un visiteur
     ou null si aucune session n’est ouverte.
     Elle est utile à l’interface graphique pour adapter l’affichage
     en fonction du profil actif.
     C’est donc une méthode d’accès simple à l’état courant de la session.*/
    public boolean estAdmin()   { return utilisateurCourant instanceof Administrateur; }
    /* Cette fonction estAdmin permet de savoir
     si l’utilisateur actuellement connecté est un administrateur.
     Elle retourne true uniquement si l’objet utilisateurCourant
     est une instance de la classe Administrateur.
     Cela permet à l’interface d’activer ou non
     les fonctionnalités réservées à l’administration.
     Cette méthode sert donc à gérer les droits d’accès dans la vue graphique.*/
    public boolean estAbonne()  { return utilisateurCourant instanceof Abonne; }
    /* Cette fonction estAbonne permet de vérifier
     si l’utilisateur courant est un abonné.
     Elle retourne true lorsque la session en cours
     correspond à un utilisateur disposant d’un compte abonné.
     Cette information est utile pour autoriser l’accès
     aux playlists, à l’historique et aux avis.
     Elle participe donc à la gestion du comportement de l’interface selon le rôle.*/
    public boolean estVisiteur(){ return utilisateurCourant instanceof Visiteur && !(utilisateurCourant instanceof Abonne); }
    /*Cette fonction estVisiteur permet de déterminer
     si l’utilisateur actuel est un simple visiteur.
     Elle vérifie d’abord qu’il s’agit bien d’un Visiteur,
     puis exclut explicitement le cas d’un Abonne,
     car un abonné peut hériter de certaines caractéristiques proches.
     Cette méthode permet de distinguer clairement
     un visiteur anonyme d’un utilisateur inscrit.
     Elle est notamment utile pour limiter le nombre d’écoutes autorisées.*/
    
    /** Initialise une session visiteur anonyme. */
    public void demarrerSessionVisiteur() { utilisateurCourant = new Visiteur(); }
    /* Cette fonction demarrerSessionVisiteur permet d’ouvrir
     une session anonyme dans l’application.
     Elle crée un nouvel objet Visiteur
     puis l’affecte comme utilisateur courant.
     Cela permet à une personne non connectée
     d’accéder au catalogue avec des droits limités.
     La méthode est donc utilisée lorsque l’on choisit
     de continuer sans créer de compte ni se connecter.*/

    public void deconnecter() { utilisateurCourant = null; }
    /* Cette fonction deconnecter permet de fermer la session en cours.
     Elle supprime simplement la référence vers l’utilisateur connecté
     en remettant utilisateurCourant à null.
     Cela signifie qu’aucun utilisateur n’est alors actif dans l’application.
     Cette méthode est utile lors d’une déconnexion manuelle
     ou lors d’un retour à l’écran d’accueil.
     Elle remet donc le contrôleur dans un état neutre.*/

    // =========================================================
    //  AUTHENTIFICATION
    // =========================================================

    /** @return true si les identifiants admin sont corrects */
    public boolean connecterAdmin(String login, String mdp) {
        if (admin.getLogin().equals(login) && admin.verifierMotDePasse(mdp)) {
            utilisateurCourant = admin;
            return true;
        }
        return false;
    }
    /* Cette fonction connecterAdmin permet de tenter
     une connexion en tant qu’administrateur.
     Elle compare le login fourni avec celui de l’administrateur par défaut
     puis vérifie également la validité du mot de passe.
     Si les deux informations sont correctes,
     l’administrateur devient l’utilisateur courant.
     La fonction retourne alors true pour signaler la réussite de la connexion.
     Si l’un des identifiants est incorrect,
     la session n’est pas modifiée et la fonction retourne false.
     Cette méthode centralise donc la logique d’authentification administrateur.*/

    /**
     * Connecte un abonné.
     * @return l'abonné connecté, ou null si identifiants incorrects
     * @throws CompteInactifException si le compte est suspendu
     */
    public Abonne connecterAbonne(String login, String mdp) throws CompteInactifException {
        for (Abonne a : abonnes) {
            if (a.getLogin().equalsIgnoreCase(login) && a.verifierMotDePasse(mdp)) {
                if (!a.isActif()) throw new CompteInactifException("Ce compte est suspendu.");
                utilisateurCourant = a;
                return a;
            }
        }
        return null;
    }
    /* Cette fonction connecterAbonne permet de rechercher
     un abonné correspondant au login et au mot de passe saisis.
     Elle parcourt la liste complète des abonnés enregistrés.
     La comparaison du login se fait sans tenir compte des majuscules et minuscules,
     ce qui rend la connexion plus souple pour l’utilisateur.
     Si un abonné correspondant est trouvé,
     la méthode vérifie ensuite si son compte est actif.
     Si le compte est suspendu,
     une exception CompteInactifException est levée
     afin que l’interface puisse afficher un message adapté.
     Si tout est correct, l’abonné devient l’utilisateur courant
     et la fonction retourne cet objet.
     Si aucun abonné valide n’est trouvé,
     la fonction retourne null.
     Cette méthode regroupe donc la logique complète de connexion d’un abonné.*/

    /**
     * Crée un nouveau compte abonné et ouvre sa session.
     * @throws Exception si le login est déjà utilisé
     */
    public Abonne creerCompte(String nom, String prenom, String login, String mdp) throws Exception {
        for (Abonne a : abonnes) {
            if (a.getLogin().equalsIgnoreCase(login))
                throw new Exception("Le login \"" + login + "\" est déjà utilisé.");
        }
        Abonne nouvel = new Abonne(nom, prenom, login, mdp);
        abonnes.add(nouvel);
        utilisateurCourant = nouvel;
        return nouvel;
    }
    /* Cette fonction creerCompte permet de créer
     un nouveau compte abonné dans l’application.
     Elle commence par vérifier que le login demandé
     n’est pas déjà utilisé par un autre abonné.
     Pour cela, elle parcourt la liste des abonnés existants
     et compare les logins sans tenir compte de la casse.
     Si un doublon est détecté,
     une exception est levée avec un message explicite.
     Si le login est disponible,
     un nouvel objet Abonne est créé avec les informations fournies.
     Cet abonné est ensuite ajouté à la liste des abonnés enregistrés.
     La méthode ouvre également immédiatement sa session
     en le définissant comme utilisateur courant.
     Enfin, elle retourne l’objet nouvellement créé.
     Cette fonction sert donc à la fois à l’inscription
     et à la connexion automatique après création du compte.*/

    // =========================================================
    //  CATALOGUE
    // =========================================================

    public Catalogue getCatalogue() { return catalogue; }
    /* Cette fonction getCatalogue permet de récupérer
     le catalogue musical actuellement chargé dans l’application.
     Elle fournit un accès direct au modèle Catalogue
     pour que l’interface graphique puisse afficher les morceaux,
     albums, artistes et groupes.
     Cette méthode ne modifie rien,
     elle expose simplement les données nécessaires à la vue.
     Elle joue donc un rôle d’accès au contenu principal de l’application.*/

    // =========================================================
    //  LECTURE
    // =========================================================

    /** @return true si l'utilisateur courant peut encore écouter */
    public boolean peutEcouter() {
        if (utilisateurCourant instanceof Visiteur)
            return ((Visiteur) utilisateurCourant).peutEcouter();
        return true;
    }
    /* Cette fonction peutEcouter permet de savoir
     si l’utilisateur courant a encore le droit d’écouter un morceau.
     Si l’utilisateur est un visiteur,
     la décision dépend de la limite d’écoutes autorisées par session.
     Dans ce cas, la méthode délègue le calcul
     à la méthode peutEcouter de la classe Visiteur.
     Si l’utilisateur est un abonné ou un administrateur,
     la fonction retourne directement true,
     car ces profils ne sont pas soumis à une limitation d’écoute.
     Cette méthode est donc utile pour bloquer ou autoriser
     les boutons de lecture dans l’interface.*/

    /** @return écoutes restantes pour un visiteur, -1 pour un abonné */
    public int getEcoutesRestantes() {
        if (utilisateurCourant instanceof Visiteur && !(utilisateurCourant instanceof Abonne))
            return Visiteur.MAX_ECOUTES_SESSION - ((Visiteur) utilisateurCourant).getNbEcoutesSession();
        return -1;
    }
    /* Cette fonction getEcoutesRestantes permet de connaître
     le nombre d’écoutes encore disponibles pour un visiteur.
     Elle s’applique uniquement aux simples visiteurs,
     c’est-à-dire aux utilisateurs anonymes non abonnés.
     Elle calcule la différence entre la limite maximale autorisée
     et le nombre d’écoutes déjà effectuées pendant la session.
     Si l’utilisateur courant n’est pas un simple visiteur,
     la fonction retourne -1.
     Cette valeur spéciale permet de signaler
     qu’aucune limite ne s’applique à ce profil.
     Cette méthode est donc utile pour afficher une information dynamique
     dans l’interface graphique.*/

    /**
     * Enregistre l'écoute d'un morceau pour l'utilisateur courant.
     * @throws LimiteEcoutesAtteinte si le visiteur a atteint sa limite
     */
    public void ecouter(Morceau m) throws LimiteEcoutesAtteinte {
        if (utilisateurCourant instanceof Visiteur)
            ((Visiteur) utilisateurCourant).incrementerEcoutes();
        m.incrementerEcoutes();
        catalogue.incrementerEcoutesTotales();
        if (utilisateurCourant instanceof Abonne)
            ((Abonne) utilisateurCourant).getHistorique().ajouterEcoute(m);
    }
    /* Cette fonction ecouter permet d’enregistrer
     l’écoute effective d’un morceau par l’utilisateur courant.
     Si l’utilisateur est un visiteur,
     elle commence par incrémenter son compteur d’écoutes de session.
     Cette opération peut lever une exception
     si la limite autorisée est déjà atteinte.
     Ensuite, quel que soit le type d’utilisateur,
     la méthode incrémente le nombre total d’écoutes du morceau.
     Elle incrémente également le compteur global d’écoutes du catalogue.
     Si l’utilisateur courant est un abonné,
     le morceau écouté est ajouté à son historique d’écoute.
     Cette fonction centralise donc toute la logique métier
     liée à l’action d’écouter un morceau.
     Elle garantit aussi la mise à jour cohérente
     des statistiques et de l’historique utilisateur.*/

    // =========================================================
    //  AVIS
    // =========================================================

    /** Ajoute ou remplace l'avis de l'abonné courant sur un morceau. */
    public void ajouterAvis(Morceau m, int note, String commentaire) {
        if (!(utilisateurCourant instanceof Abonne)) return;
        m.ajouterAvis(new Avis((Abonne) utilisateurCourant, note, commentaire));
    }
    /* Cette fonction ajouterAvis permet à l’utilisateur courant
     de déposer un avis sur un morceau donné.
     Elle vérifie d’abord que l’utilisateur connecté est bien un abonné,
     car seuls les abonnés ont le droit de laisser un avis.
     Si ce n’est pas le cas, la méthode s’arrête immédiatement.
     Sinon, elle crée un nouvel objet Avis
     contenant l’abonné, la note et le commentaire.
     Cet avis est ensuite transmis au morceau concerné
     via la méthode ajouterAvis.
     Selon l’implémentation du modèle,
     cet avis peut être ajouté ou remplacer un avis précédent du même abonné.
     Cette fonction relie donc l’action de l’interface
     au mécanisme de gestion des avis dans le modèle.*/

    /** Supprime l'avis de l'abonné courant sur un morceau. */
    public void supprimerAvis(Morceau m) {
        if (!(utilisateurCourant instanceof Abonne)) return;
        m.supprimerAvis((Abonne) utilisateurCourant);
    }
    /* Cette fonction supprimerAvis permet à l’abonné courant
     de retirer son avis sur un morceau.
     Comme pour l’ajout d’avis, elle vérifie d’abord
     que l’utilisateur connecté est bien un abonné.
     Si ce n’est pas le cas, aucune action n’est effectuée.
     Si la condition est respectée,
     la méthode demande au morceau de supprimer l’avis
     associé à cet abonné précis.
     Cette fonction permet donc à l’utilisateur
     de modifier librement sa participation aux avis.
     Elle assure aussi que seul l’auteur de l’avis concerné
     peut déclencher sa suppression.*/

    // =========================================================
    //  PLAYLISTS
    // =========================================================

    /** Cree une playlist pour l'abonne courant. */
    public Playlist creerPlaylist(String nom) {
        if (!(utilisateurCourant instanceof Abonne)) return null;
        return ((Abonne) utilisateurCourant).creerPlaylist(nom);
    }
    /* Cette fonction creerPlaylist permet à l’utilisateur courant
     de créer une nouvelle playlist personnelle.
     Elle vérifie d’abord que l’utilisateur connecté est un abonné,
     car seuls les abonnés peuvent gérer des playlists.
     Si l’utilisateur n’est pas un abonné,
     la méthode retourne null pour signaler qu’aucune playlist n’a été créée.
     Si la condition est remplie,
     la création est déléguée à la méthode creerPlaylist de l’abonné.
     La playlist nouvellement créée est ensuite retournée.
     Cette méthode constitue donc un point d’entrée simple
     entre l’interface graphique et la logique métier des playlists.*/

    public void supprimerPlaylist(Playlist p) throws ElementIntrouvableException {
        if (!(utilisateurCourant instanceof Abonne)) return;
        ((Abonne) utilisateurCourant).supprimerPlaylist(p);
    }/* Cette fonction supprimerPlaylist permet à l’abonné courant
     de supprimer une playlist lui appartenant.
     Elle commence par vérifier que l’utilisateur connecté est bien un abonné.
     Si ce n’est pas le cas, aucune suppression n’est tentée.
     Si l’utilisateur est valide,
     la suppression est déléguée à l’objet Abonne.
     Cette opération peut lever une exception ElementIntrouvableException
     si la playlist n’existe pas ou n’est pas trouvée dans sa collection.
     Cette méthode relie donc l’action utilisateur
     à la suppression effective côté modèle.*/

    public void ajouterMorceauPlaylist(Playlist p, Morceau m) throws MorceauDejaExistantException {
        p.ajouterMorceau(m);
    }/* Cette fonction ajouterMorceauPlaylist permet d’ajouter
     un morceau donné à une playlist donnée.
     Elle délègue directement l’opération à la playlist concernée.
     Si le morceau est déjà présent,
     une exception MorceauDejaExistantException peut être levée.
     Cela permet à l’interface graphique de détecter le doublon
     et d’afficher un message explicite à l’utilisateur.
     Cette méthode joue donc le rôle d’intermédiaire simple
     entre la vue et la logique de gestion des playlists.*/

    public void retirerMorceauPlaylist(Playlist p, Morceau m) throws ElementIntrouvableException {
        p.retirerMorceau(m);
    } /* Cette fonction retirerMorceauPlaylist permet de supprimer
     un morceau précis d’une playlist.
     Comme pour l’ajout, elle délègue directement le travail
     à la méthode de la playlist concernée.
     Si le morceau n’est pas présent dans la playlist,
     une exception ElementIntrouvableException peut être levée.
     Cela permet à l’interface de gérer proprement ce cas d’erreur.
     Cette fonction sert donc à relier la commande de retrait
     à la logique métier de la playlist.*/

    // =========================================================
    //  PLAYLISTS COLLABORATIVES
    // =========================================================

    /**
     * Retourne toutes les playlists partagees avec l'abonne courant
     * (dont il est collaborateur, mais pas proprietaire).
     */
    public ArrayList<Playlist> getPlaylistsPartagees() {
        ArrayList<Playlist> result = new ArrayList<>();
        if (!(utilisateurCourant instanceof Abonne)) return result;
        Abonne moi = (Abonne) utilisateurCourant;
        for (Abonne a : abonnes) {
            for (Playlist p : a.getPlaylists()) {
                if (p.estCollaborateur(moi)) {
                    result.add(p);
                }
            }
        }
        return result;
    }
    /* Cette fonction getPlaylistsPartagees permet de récupérer
     l’ensemble des playlists collaboratives accessibles à l’abonné courant.
     Elle crée d’abord une liste vide qui servira à stocker le résultat.
     Si l’utilisateur courant n’est pas un abonné,
     la liste vide est immédiatement retournée.
     Si un abonné est connecté,
     la méthode parcourt tous les abonnés enregistrés dans l’application.
     Pour chacun d’eux, elle parcourt ensuite toutes leurs playlists.
     Elle vérifie alors si l’abonné courant est collaborateur de chaque playlist.
     Si c’est le cas, la playlist est ajoutée à la liste résultat.
     Cette méthode permet donc de construire dynamiquement
     la liste des playlists partagées avec un utilisateur donné.
     Elle est très utile pour alimenter une vue dédiée
     aux playlists collaboratives dans l’interface graphique.*/

    /** Ajoute un collaborateur a une playlist. */
    public void ajouterCollaborateur(Playlist p, Abonne collab, boolean peutModifier) {
        p.ajouterCollaborateur(collab, peutModifier);
    }
    /* Cette fonction ajouterCollaborateur permet d’ajouter
     un nouvel abonné comme collaborateur d’une playlist.
     Elle reçoit la playlist concernée,
     l’abonné à ajouter
     ainsi qu’un booléen indiquant s’il aura le droit de modifier la playlist.
     La logique réelle est déléguée à l’objet Playlist.
     Cette méthode sert donc de relais
     entre les actions de l’interface graphique
     et la gestion métier des droits collaboratifs.
     Elle permet de mettre en place des playlists partagées
     avec différents niveaux d’autorisation.*/

    /** Retire un collaborateur d'une playlist. */
    public void retirerCollaborateur(Playlist p, Abonne collab) {
        p.retirerCollaborateur(collab);
    }
    /* Cette fonction retirerCollaborateur permet de supprimer
     un collaborateur d’une playlist partagée.
     Elle reçoit la playlist concernée
     ainsi que l’abonné à retirer.
     L’opération est transmise directement à la playlist.
     Cette méthode permet donc de mettre fin
     aux droits d’accès collaboratifs d’un utilisateur sur une playlist.
     Elle est utile pour la gestion des partages dans l’interface graphique.*/

    /** Retourne la liste de tous les abonnes sauf l'utilisateur courant. */
    public ArrayList<Abonne> getAutresAbonnes() {
        ArrayList<Abonne> autres = new ArrayList<>();
        if (!(utilisateurCourant instanceof Abonne)) return autres;
        for (Abonne a : abonnes) {
            if (!a.equals(utilisateurCourant)) autres.add(a);
        }
        return autres;
    }
    /* Cette fonction getAutresAbonnes permet d’obtenir
     la liste de tous les abonnés sauf celui qui est actuellement connecté.
     Elle commence par créer une liste vide pour stocker le résultat.
     Si l’utilisateur courant n’est pas un abonné,
     cette liste vide est immédiatement retournée.
     Sinon, la méthode parcourt tous les abonnés enregistrés.
     Chaque abonné différent de l’utilisateur courant
     est ajouté à la liste de résultat.
     Cette méthode est particulièrement utile
     lors de l’ajout de collaborateurs à une playlist,
     car elle évite de proposer l’utilisateur lui-même.
     Elle facilite donc la préparation des choix à afficher dans l’interface.*/

    // =========================================================
    //  ADMIN — CATALOGUE
    // =========================================================

    public Artiste ajouterArtiste(String nom, String bio) {
        Artiste a = new Artiste(nom, bio);
        catalogue.ajouterArtiste(a);
        return a;
    }
    /* Cette fonction ajouterArtiste permet de créer
     un nouvel artiste dans le catalogue.
     Elle construit un objet Artiste à partir du nom et de la biographie fournis.
     Cet artiste est ensuite ajouté au catalogue général.
     Enfin, l’objet créé est retourné.
     Cette méthode est utile à l’interface d’administration
     pour afficher ou manipuler immédiatement l’artiste nouvellement ajouté.
     Elle centralise donc la création d’un artiste dans le contrôleur.*/

    public Groupe ajouterGroupe(String nom) {
        Groupe g = new Groupe(nom);
        catalogue.ajouterGroupe(g);
        return g;
    }
    /* Cette fonction ajouterGroupe permet de créer
     un nouveau groupe musical dans le catalogue.
     Elle instancie d’abord un objet Groupe à partir du nom fourni.
     Elle ajoute ensuite ce groupe au catalogue.
     Enfin, elle retourne le groupe créé.
     Cette méthode est utilisée dans les fonctionnalités d’administration
     afin d’enrichir dynamiquement le catalogue musical.
     Elle constitue donc le point d’entrée pour l’ajout d’un groupe.*/

    public Album ajouterAlbum(String titre, int annee, AuteurMusical auteur) {
        Album a = new Album(titre, annee, auteur);
        catalogue.ajouterAlbum(a);
        return a;
    }/* Cette fonction ajouterAlbum permet de créer
     un nouvel album dans le catalogue.
     Elle reçoit le titre, l’année de sortie
     et l’auteur musical auquel l’album doit être rattaché.
     Elle crée l’objet Album correspondant
     puis l’ajoute au catalogue général.
     Enfin, elle retourne l’album créé.
     Cette méthode permet à l’interface d’administration
     de manipuler immédiatement le nouvel album après sa création.*/


    public Morceau ajouterMorceau(String titre, int duree, AuteurMusical auteur)
            throws MorceauDejaExistantException {
        return ajouterMorceau(titre, duree, auteur, Genre.INCONNU);
    }

    public Morceau ajouterMorceau(String titre, int duree, AuteurMusical auteur, Genre genre)
            throws MorceauDejaExistantException {
        Morceau m = new Morceau(titre, duree, auteur, genre);
        catalogue.ajouterMorceau(m);
        return m;
    }/* Cette fonction ajouterMorceau permet de créer
     un nouveau morceau dans le catalogue musical.
     Elle reçoit le titre du morceau, sa durée
     ainsi que son auteur musical.
     Elle crée d’abord l’objet Morceau correspondant.
     Ensuite, elle tente de l’ajouter au catalogue.
     Si un morceau équivalent existe déjà,
     une exception MorceauDejaExistantException peut être levée.
     Cela permet à l’interface graphique de gérer les doublons proprement.
     Si tout se passe bien, la fonction retourne le morceau créé.
     Elle centralise donc l’ajout de nouveaux morceaux dans le modèle.*/

    public void supprimerMorceau(Morceau m) throws ElementIntrouvableException {
        catalogue.supprimerMorceau(m);
    }
    /* Cette fonction supprimerMorceau permet de retirer
     un morceau du catalogue musical.
     Elle délègue la suppression directement au catalogue.
     Si le morceau n’existe pas ou n’est pas retrouvé,
     une exception ElementIntrouvableException peut être levée.
     Cette méthode sert donc d’intermédiaire
     entre la vue d’administration et le modèle.
     Elle permet de centraliser la suppression de morceaux.*/

    public void supprimerAlbum(Album a) throws ElementIntrouvableException {
        catalogue.supprimerAlbum(a);
    }
    /* Cette fonction supprimerAlbum permet de retirer
     un album du catalogue.
     Elle transmet simplement l’opération de suppression au catalogue.
     Si l’album n’est pas trouvé,
     une exception ElementIntrouvableException peut être levée.
     Cette méthode est utilisée par l’interface d’administration
     pour supprimer proprement un album existant.
     Elle sert donc de passerelle vers la logique métier de suppression.*/

    public void supprimerArtiste(Artiste a) throws ElementIntrouvableException {
        catalogue.supprimerArtiste(a);
    }
    /* Cette fonction supprimerArtiste permet de retirer
     un artiste du catalogue musical.
     Elle délègue la suppression au catalogue.
     Si l’artiste n’existe pas dans les données actuelles,
     une exception ElementIntrouvableException peut être levée.
     Cette méthode permet à l’administrateur
     de maintenir ou nettoyer le catalogue.
     Elle joue donc un rôle simple mais important dans la gestion des artistes.*/

    public void supprimerGroupe(Groupe g) throws ElementIntrouvableException {
        catalogue.supprimerGroupe(g);
    }
    /* Cette fonction supprimerGroupe permet de retirer
     un groupe du catalogue musical.
     Elle transmet directement la demande de suppression au catalogue.
     Si le groupe demandé n’est pas présent,
     une exception ElementIntrouvableException peut être levée.
     Cette méthode est utilisée dans les outils d’administration
     pour gérer le contenu du catalogue.
     Elle complète ainsi les autres opérations de suppression.*/

    // =========================================================
    //  ADMIN — ABONNÉS
    // =========================================================

    public ArrayList<Abonne> getAbonnes() { return abonnes; }
    /* Cette fonction getAbonnes permet de récupérer
     la liste complète des abonnés enregistrés dans l’application.
     Elle offre un accès direct à cette collection
     afin que l’interface graphique puisse l’afficher ou l’exploiter.
     Cette méthode ne modifie rien,
     elle sert uniquement à exposer les données de gestion des comptes.
     Elle est donc utile dans les écrans d’administration des abonnés.*/

    public void toggleSuspension(Abonne a) { a.setActif(!a.isActif()); }
    /* Cette fonction toggleSuspension permet de changer
     l’état actif ou suspendu d’un abonné.
     Elle lit d’abord l’état actuel du compte,
     puis applique l’état inverse.
     Si le compte était actif, il devient suspendu.
     S’il était suspendu, il redevient actif.
     Cette méthode est très utile pour l’administration,
     car elle permet de gérer rapidement l’accès d’un utilisateur au service.
     Elle centralise donc la logique de suspension et de réactivation.*/

    public void supprimerAbonne(Abonne a) { abonnes.remove(a); }
    /* Cette fonction supprimerAbonne permet de retirer
     un abonné de la liste des comptes enregistrés.
     Elle supprime directement l’objet passé en paramètre
     de la collection des abonnés.
     Cette opération correspond à une suppression définitive du compte
     du point de vue des données en mémoire.
     La suppression sera ensuite conservée
     lors de la prochaine sauvegarde.
     Cette méthode est donc utilisée pour la gestion administrative des comptes.*/

    // =========================================================
    //  PERSISTANCE
    // =========================================================

    /** Sauvegarde le catalogue et les abonnés (appelé à la fermeture). */
    public void sauvegarder() {
        new File(DATA_DIR).mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CATALOGUE_FILE))) {
            oos.writeObject(catalogue);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde catalogue : " + e.getMessage());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ABONNES_FILE))) {
            oos.writeObject(abonnes);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde abonnés : " + e.getMessage());
        }
    }/* Cette fonction sauvegarder permet d’enregistrer
     l’état actuel du catalogue et de la liste des abonnés dans des fichiers.
     Elle commence par s’assurer que le dossier de stockage existe,
     en le créant si nécessaire.
     Ensuite, elle ouvre un flux de sortie binaire
     vers le fichier de sauvegarde du catalogue.
     Elle y écrit l’objet catalogue grâce à la sérialisation Java.
     Si une erreur d’écriture survient,
     un message explicite est affiché sur la sortie d’erreur.
     La même logique est ensuite appliquée à la liste des abonnés.
     Cette séparation permet de sauvegarder indépendamment
     les deux ensembles de données principaux.
     La méthode est généralement appelée à la fermeture de l’application
     afin de conserver les changements effectués par l’utilisateur.
     Elle joue donc un rôle essentiel dans la persistance des données.*/


    @SuppressWarnings("unchecked")
    private void chargerDonnees() {
        new File(DATA_DIR).mkdirs();
        // Catalogue
        File fc = new File(CATALOGUE_FILE);
        if (fc.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fc))) {
                catalogue = (Catalogue) ois.readObject();
            } catch (Exception e) {
                catalogue = new Catalogue();
                chargerCatalogueTxt();
            }
        } else {
            catalogue = new Catalogue();
            chargerCatalogueTxt();
        }
        // Abonnés
        File fa = new File(ABONNES_FILE);
        if (fa.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fa))) {
                abonnes = (ArrayList<Abonne>) ois.readObject();
            } catch (Exception e) {
                abonnes = new ArrayList<>();
            }
        }
    }/* Cette fonction chargerDonnees permet de restaurer
     les données sauvegardées de l’application au démarrage.
     Elle commence par vérifier l’existence du dossier de données
     et le crée si nécessaire.
     Pour le catalogue, elle regarde d’abord
     si le fichier de sauvegarde binaire existe.
     Si oui, elle tente de désérialiser son contenu
     pour reconstituer l’objet Catalogue.
     Si cette lecture échoue pour une quelconque raison,
     un nouveau catalogue vide est créé
     puis complété à partir du fichier texte ou des données de démonstration.
     Si aucun fichier de sauvegarde n’existe,
     le même mécanisme de secours est utilisé directement.
     Ensuite, la méthode applique une logique similaire pour les abonnés.
     Si le fichier des abonnés existe,
     elle essaie de relire la liste sérialisée.
     En cas d’échec, elle réinitialise simplement la liste à vide.
     Cette fonction est donc fondamentale pour retrouver
     l’état précédent de l’application entre deux exécutions.*/


    private void chargerCatalogueTxt() {
        if (!DataLoader.chargerCatalogueDepuisTxt(catalogue)) {
            DataLoader.initialiserDonneesDemo(catalogue);
        }
    }
    /* Cette fonction chargerCatalogueTxt permet de remplir le catalogue
     à partir d’une source texte externe.
     Elle appelle pour cela la méthode spécialisée du DataLoader.
     Si le chargement depuis le fichier texte échoue,
     elle bascule automatiquement sur des données de démonstration.
     Cette méthode garantit donc que le catalogue
     ne reste jamais vide au démarrage du programme.
     Elle joue un rôle de secours important
     dans la stratégie d’initialisation des données.*/
}
}
