package controller;

import model.*;
import model.exceptions.*;
import view.ConsoleView;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Contrôleur principal de l'application JavaZic.
 * Fait le lien entre le modèle ({@link Catalogue}, {@link Abonne}…) et la vue ({@link ConsoleView}).
 * Gère aussi la persistance des données (sérialisation Java).
 */
public class Controller {

    private Catalogue catalogue;
    private ArrayList<Abonne> abonnes;
    private final Administrateur admin;
    private final ConsoleView vue;

    private static final String DATA_DIR      = "data";
    private static final String CATALOGUE_FILE = DATA_DIR + "/catalogue.ser";
    private static final String ABONNES_FILE   = DATA_DIR + "/abonnes.ser";

    public Controller() {
        this.vue     = new ConsoleView();
        this.admin   = new Administrateur("Admin", "Super", "admin", "1234");
        this.abonnes = new ArrayList<>();
        chargerDonnees();
    }

    // =========================================================
    //  DÉMARRAGE
    // =========================================================

    /** Démarre la boucle principale de l'application. */
    public void demarrer() {
        boolean continuer = true;
        while (continuer) {
            int choix = vue.afficherMenuPrincipal();
            switch (choix) {
                case 1: connexionAdmin();   break;
                case 2: connexionAbonne();  break;
                case 3: creerCompte();      break;
                case 4: modeVisiteur();     break;
                case 5: continuer = false;  break;
            }
        }
        sauvegarderDonnees();
        System.out.println("\n  Au revoir ! À bientôt sur JavaZic ♪\n");
    }

    // =========================================================
    //  AUTHENTIFICATION
    // =========================================================

    private void connexionAdmin() {
        System.out.println("\n  -- Connexion administrateur --");
        String login = vue.lireChaine("  Login : ");
        String mdp   = vue.lireChaine("  Mot de passe : ");
        if (admin.getLogin().equals(login) && admin.verifierMotDePasse(mdp)) {
            vue.afficherMessage("Bienvenue, " + admin.getNomComplet() + " !");
            menuAdmin();
        } else {
            vue.afficherErreur("Identifiants incorrects.");
        }
    }
    

    private void connexionAbonne() {
        System.out.println("\n  -- Connexion abonné --");
        String login = vue.lireChaine("  Login : ");
        String mdp   = vue.lireChaine("  Mot de passe : ");
        for (Abonne a : abonnes) {
            if (a.getLogin().equalsIgnoreCase(login) && a.verifierMotDePasse(mdp)) {
                if (!a.isActif()) {
                    vue.afficherErreur("Votre compte est suspendu. Contactez un administrateur.");
                    return;
                }
                vue.afficherMessage("Bienvenue, " + a.getPrenom() + " !");
                menuAbonne(a);
                return;
            }
        }
        vue.afficherErreur("Identifiants incorrects.");
    }

    private void creerCompte() {
        System.out.println("\n  -- Création de compte --");
        String nom    = vue.lireChaine("  Nom : ");
        String prenom = vue.lireChaine("  Prénom : ");
        String login  = vue.lireChaine("  Login : ");
        for (Abonne a : abonnes) {
            if (a.getLogin().equalsIgnoreCase(login)) {
                vue.afficherErreur("Ce login est déjà utilisé. Choisissez-en un autre.");
                return;
            }
        }
        String mdp = vue.lireChaine("  Mot de passe : ");
        Abonne nouvel = new Abonne(nom, prenom, login, mdp);
        abonnes.add(nouvel);
        vue.afficherMessage("Compte créé avec succès ! Bienvenue, " + prenom + " !");
        menuAbonne(nouvel);
    }

    private void modeVisiteur() {
        Visiteur v = new Visiteur();
        boolean continuer = true;
        while (continuer) {
            int restantes = Visiteur.MAX_ECOUTES_SESSION - v.getNbEcoutesSession();
            int choix = vue.afficherMenuVisiteur(restantes);
            switch (choix) {
                case 1: parcourirCatalogue(v); break;
                case 2: rechercherCatalogue(v); break;
                case 3: continuer = false; break;
            }
        }
    }

    // =========================================================
    //  MENU ABONNÉ
    // =========================================================

    private void menuAbonne(Abonne a) {
        boolean continuer = true;
        while (continuer) {
            int choix = vue.afficherMenuAbonne(a);
            switch (choix) {
                case 1: parcourirCatalogue(a); break;
                case 2: rechercherCatalogue(a); break;
                case 3: menuPlaylists(a); break;
                case 4: vue.afficherHistorique(a.getHistorique()); vue.attendreEntree(); break;
                case 5: continuer = false; vue.afficherMessage("À bientôt, " + a.getPrenom() + " !"); break;
            }
        }
    }

    // =========================================================
    //  MENU ADMINISTRATEUR
    // =========================================================

    private void menuAdmin() {
        boolean continuer = true;
        while (continuer) {
            int choix = vue.afficherMenuAdmin();
            switch (choix) {
                case 1: menuGestionCatalogue(); break;
                case 2: menuGestionAbonnes(); break;
                case 3: vue.afficherStatistiques(catalogue, abonnes); vue.attendreEntree(); break;
                case 4: continuer = false; vue.afficherMessage("Déconnexion."); break;
            }
        }
    }

    // =========================================================
    //  PARCOURIR LE CATALOGUE
    // =========================================================

    private void parcourirCatalogue(Utilisateur u) {
        boolean continuer = true;
        while (continuer) {
            int choix = vue.afficherMenuCatalogue();
            switch (choix) {
                case 1: parcourirMorceaux(catalogue.getMorceaux(), u); break;
                case 2: parcourirAlbums(catalogue.getAlbums(), u); break;
                case 3: parcourirArtistes(catalogue.getArtistes(), u); break;
                case 4: parcourirGroupes(catalogue.getGroupes(), u); break;
                case 5: continuer = false; break;
            }
        }
    }

    private void rechercherCatalogue(Utilisateur u) {
        boolean continuer = true;
        while (continuer) {
            int choix = vue.afficherMenuRecherche();
            if (choix == 5) { continuer = false; break; }
            String query = vue.lireChaine("  Rechercher : ");
            switch (choix) {
                case 1: parcourirMorceaux(catalogue.rechercherMorceaux(query), u); break;
                case 2: parcourirAlbums(catalogue.rechercherAlbums(query), u); break;
                case 3: parcourirArtistes(catalogue.rechercherArtistes(query), u); break;
                case 4: parcourirGroupes(catalogue.rechercherGroupes(query), u); break;
            }
        }
    }

    // ---- Navigation dans les listes ----

    private void parcourirMorceaux(ArrayList<Morceau> morceaux, Utilisateur u) {
        vue.afficherListeMorceaux(morceaux);
        if (morceaux.isEmpty()) { vue.attendreEntree(); return; }
        int idx = vue.choisirDansListe(morceaux.size());
        if (idx == 0) return;
        voirDetailMorceau(morceaux.get(idx - 1), u);
    }

    private void parcourirAlbums(ArrayList<Album> albums, Utilisateur u) {
        vue.afficherListeAlbums(albums);
        if (albums.isEmpty()) { vue.attendreEntree(); return; }
        int idx = vue.choisirDansListe(albums.size());
        if (idx == 0) return;
        voirDetailAlbum(albums.get(idx - 1), u);
    }

    private void parcourirArtistes(ArrayList<Artiste> artistes, Utilisateur u) {
        vue.afficherListeArtistes(artistes);
        if (artistes.isEmpty()) { vue.attendreEntree(); return; }
        int idx = vue.choisirDansListe(artistes.size());
        if (idx == 0) return;
        voirDetailArtiste(artistes.get(idx - 1), u);
    }

    private void parcourirGroupes(ArrayList<Groupe> groupes, Utilisateur u) {
        vue.afficherListeGroupes(groupes);
        if (groupes.isEmpty()) { vue.attendreEntree(); return; }
        int idx = vue.choisirDansListe(groupes.size());
        if (idx == 0) return;
        voirDetailGroupe(groupes.get(idx - 1), u);
    }

    // ---- Détails des éléments ----

    private void voirDetailMorceau(Morceau m, Utilisateur u) {
        boolean continuer = true;
        while (continuer) {
            vue.afficherDetailMorceau(m);
            boolean estAbonne = u instanceof Abonne;
            int choix = vue.afficherMenuOptionsMorceau(estAbonne);
            if (!estAbonne) {
                // Visiteur : 1=écouter, 2=voir avis, 3=retour
                switch (choix) {
                    case 1: ecouter(m, u); break;
                    case 2: vue.afficherAvis(m.getAvis()); vue.attendreEntree(); break;
                    case 3: continuer = false; break;
                }
            } else {
                // Abonné : 1=écouter, 2=voir avis, 3=laisser avis, 4=supprimer avis, 5=retour
                Abonne a = (Abonne) u;
                switch (choix) {
                    case 1: ecouter(m, u); break;
                    case 2: vue.afficherAvis(m.getAvis()); vue.attendreEntree(); break;
                    case 3: laisserAvis(m, a); break;
                    case 4: supprimerAvis(m, a); break;
                    case 5: continuer = false; break;
                }
            }
        }
    }

    private void voirDetailAlbum(Album album, Utilisateur u) {
        boolean continuer = true;
        while (continuer) {
            vue.afficherDetailAlbum(album);
            int choix = vue.afficherMenuOptionsAlbum();
            switch (choix) {
                case 1:
                    parcourirMorceaux(album.getMorceaux(), u);
                    break;
                case 2:
                    if (album.getAuteur() instanceof Artiste)
                        voirDetailArtiste((Artiste) album.getAuteur(), u);
                    else if (album.getAuteur() instanceof Groupe)
                        voirDetailGroupe((Groupe) album.getAuteur(), u);
                    break;
                case 3: continuer = false; break;
            }
        }
    }

    private void voirDetailArtiste(Artiste artiste, Utilisateur u) {
        boolean continuer = true;
        while (continuer) {
            vue.afficherDetailArtiste(artiste);
            int choix = vue.afficherMenuOptionsAuteur();
            switch (choix) {
                case 1: parcourirAlbums(artiste.getAlbums(), u); break;
                case 2: parcourirMorceaux(artiste.getMorceaux(), u); break;
                case 3: continuer = false; break;
            }
        }
    }

    private void voirDetailGroupe(Groupe groupe, Utilisateur u) {
        boolean continuer = true;
        while (continuer) {
            vue.afficherDetailGroupe(groupe);
            int choix = vue.afficherMenuOptionsAuteur();
            switch (choix) {
                case 1: parcourirAlbums(groupe.getAlbums(), u); break;
                case 2: parcourirMorceaux(groupe.getMorceaux(), u); break;
                case 3: continuer = false; break;
            }
        }
    }

    // =========================================================
    //  ÉCOUTE
    // =========================================================

    /**
     * Gère l'écoute d'un morceau pour un utilisateur (visiteur ou abonné).
     */
    private void ecouter(Morceau m, Utilisateur u) {
        try {
            if (u instanceof Visiteur) ((Visiteur) u).incrementerEcoutes();
            vue.simulerEcoute(m);
            m.incrementerEcoutes();
            catalogue.incrementerEcoutesTotales();
            if (u instanceof Abonne) ((Abonne) u).getHistorique().ajouterEcoute(m);
        } catch (LimiteEcoutesAtteinte e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    // =========================================================
    //  AVIS (fonctionnalité supplémentaire)
    // =========================================================

    private void laisserAvis(Morceau m, Abonne a) {
        int note = vue.lireEntier("  Note (1-5) : ", 1, 5);
        String commentaire = vue.lireChaine("  Commentaire : ");
        try {
            Avis avis = new Avis(a, note, commentaire);
            m.ajouterAvis(avis);
            vue.afficherMessage("Avis enregistré !");
        } catch (IllegalArgumentException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    private void supprimerAvis(Morceau m, Abonne a) {
        m.supprimerAvis(a);
        vue.afficherMessage("Avis supprimé.");
    }

    // =========================================================
    //  GESTION DES PLAYLISTS
    // =========================================================

    private void menuPlaylists(Abonne a) {
        boolean continuer = true;
        while (continuer) {
            int choix = vue.afficherMenuPlaylists();
            switch (choix) {
                case 1:
                    vue.afficherPlaylists(a.getPlaylists());
                    vue.attendreEntree();
                    break;
                case 2:
                    String nom = vue.lireChaine("  Nom de la playlist : ");
                    Playlist p = a.creerPlaylist(nom);
                    vue.afficherMessage("Playlist \"" + p.getNom() + "\" créée.");
                    break;
                case 3: renommerPlaylist(a); break;
                case 4: supprimerPlaylist(a); break;
                case 5: gererPlaylist(a); break;
                case 6: voirPlaylistsPartagees(a); break;
                case 7: gererCollaborateurs(a); break;
                case 8: continuer = false; break;
            }
        }
    }

    private void renommerPlaylist(Abonne a) {
        vue.afficherPlaylists(a.getPlaylists());
        if (a.getPlaylists().isEmpty()) return;
        int idx = vue.choisirDansListe(a.getPlaylists().size());
        if (idx == 0) return;
        Playlist p = a.getPlaylists().get(idx - 1);
        String nouveau = vue.lireChaine("  Nouveau nom : ");
        p.setNom(nouveau);
        vue.afficherMessage("Playlist renommée en \"" + nouveau + "\".");
    }

    private void supprimerPlaylist(Abonne a) {
        vue.afficherPlaylists(a.getPlaylists());
        if (a.getPlaylists().isEmpty()) return;
        int idx = vue.choisirDansListe(a.getPlaylists().size());
        if (idx == 0) return;
        Playlist p = a.getPlaylists().get(idx - 1);
        try {
            a.supprimerPlaylist(p);
            vue.afficherMessage("Playlist supprimée.");
        } catch (ElementIntrouvableException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    private void gererPlaylist(Abonne a) {
        vue.afficherPlaylists(a.getPlaylists());
        if (a.getPlaylists().isEmpty()) return;
        int idx = vue.choisirDansListe(a.getPlaylists().size());
        if (idx == 0) return;
        Playlist p = a.getPlaylists().get(idx - 1);
        menuDetailPlaylist(p, a);
    }

    // ---- Playlists collaboratives ----

    private void voirPlaylistsPartagees(Abonne a) {
        ArrayList<Playlist> partagees = new ArrayList<>();
        for (Abonne autre : abonnes) {
            for (Playlist p : autre.getPlaylists()) {
                if (p.estCollaborateur(a)) partagees.add(p);
            }
        }
        if (partagees.isEmpty()) {
            vue.afficherInfo("Aucune playlist partagée avec vous.");
            vue.attendreEntree();
            return;
        }
        System.out.println("\n  Playlists partagées avec vous :");
        for (int i = 0; i < partagees.size(); i++) {
            Playlist p = partagees.get(i);
            String droits = p.peutModifier(a) ? "[Lecture + Édition]" : "[Lecture seule]";
            System.out.printf("  %3d. %s (par %s) %s%n", i + 1, p.getNom(), p.getProprietaire().getLogin(), droits);
        }
        int idx = vue.choisirDansListe(partagees.size());
        if (idx == 0) return;
        Playlist selected = partagees.get(idx - 1);
        menuDetailPlaylist(selected, a);
    }

    private void gererCollaborateurs(Abonne a) {
        vue.afficherPlaylists(a.getPlaylists());
        if (a.getPlaylists().isEmpty()) return;
        int idx = vue.choisirDansListe(a.getPlaylists().size());
        if (idx == 0) return;
        Playlist p = a.getPlaylists().get(idx - 1);

        boolean continuer = true;
        while (continuer) {
            System.out.println("\n  Collaborateurs de \"" + p.getNom() + "\" :");
            if (p.getCollaborateurs().isEmpty()) {
                System.out.println("    (aucun)");
            } else {
                for (Map.Entry<Abonne, Boolean> entry : p.getCollaborateurs().entrySet()) {
                    String droits = entry.getValue() ? "Lecture + Édition" : "Lecture seule";
                    System.out.println("    - " + entry.getKey().getLogin() + " [" + droits + "]");
                }
            }
            System.out.println("\n  1. Ajouter un collaborateur");
            System.out.println("  2. Retirer un collaborateur");
            System.out.println("  3. Retour");
            int choix = vue.lireChoix(1, 3);
            switch (choix) {
                case 1:
                    ArrayList<Abonne> autres = new ArrayList<>();
                    for (Abonne ab : abonnes) {
                        if (!ab.equals(a) && !p.estCollaborateur(ab)) autres.add(ab);
                    }
                    if (autres.isEmpty()) { vue.afficherInfo("Aucun abonné disponible."); break; }
                    vue.afficherListeAbonnes(autres);
                    int idxCollab = vue.choisirDansListe(autres.size());
                    if (idxCollab == 0) break;
                    System.out.println("  Droits : 1. Lecture seule  2. Lecture + Édition");
                    int droits = vue.lireChoix(1, 2);
                    p.ajouterCollaborateur(autres.get(idxCollab - 1), droits == 2);
                    vue.afficherMessage("Collaborateur ajouté.");
                    break;
                case 2:
                    if (p.getCollaborateurs().isEmpty()) { vue.afficherInfo("Aucun collaborateur."); break; }
                    ArrayList<Abonne> collabs = new ArrayList<>(p.getCollaborateurs().keySet());
                    for (int i = 0; i < collabs.size(); i++)
                        System.out.printf("  %3d. %s%n", i + 1, collabs.get(i).getLogin());
                    int idxRet = vue.choisirDansListe(collabs.size());
                    if (idxRet == 0) break;
                    p.retirerCollaborateur(collabs.get(idxRet - 1));
                    vue.afficherMessage("Collaborateur retiré.");
                    break;
                case 3: continuer = false; break;
            }
        }
    }

    private void menuDetailPlaylist(Playlist p, Abonne a) {
        boolean continuer = true;
        while (continuer) {
            int choix = vue.afficherMenuDetailPlaylist(p);
            switch (choix) {
                case 1:
                    vue.afficherListeMorceaux(p.getMorceaux());
                    vue.attendreEntree();
                    break;
                case 2: ajouterMorceauPlaylist(p); break;
                case 3: retirerMorceauPlaylist(p); break;
                case 4: ecouterDepuisPlaylist(p, a); break;
                case 5: continuer = false; break;
            }
        }
    }

    private void ajouterMorceauPlaylist(Playlist p) {
        String query = vue.lireChaine("  Rechercher un morceau : ");
        ArrayList<Morceau> resultats = catalogue.rechercherMorceaux(query);
        vue.afficherListeMorceaux(resultats);
        if (resultats.isEmpty()) return;
        int idx = vue.choisirDansListe(resultats.size());
        if (idx == 0) return;
        try {
            p.ajouterMorceau(resultats.get(idx - 1));
            vue.afficherMessage("Morceau ajouté à la playlist.");
        } catch (MorceauDejaExistantException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    private void retirerMorceauPlaylist(Playlist p) {
        vue.afficherListeMorceaux(p.getMorceaux());
        if (p.getMorceaux().isEmpty()) return;
        int idx = vue.choisirDansListe(p.getMorceaux().size());
        if (idx == 0) return;
        try {
            p.retirerMorceau(p.getMorceaux().get(idx - 1));
            vue.afficherMessage("Morceau retiré de la playlist.");
        } catch (ElementIntrouvableException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    private void ecouterDepuisPlaylist(Playlist p, Abonne a) {
        vue.afficherListeMorceaux(p.getMorceaux());
        if (p.getMorceaux().isEmpty()) return;
        int idx = vue.choisirDansListe(p.getMorceaux().size());
        if (idx == 0) return;
        ecouter(p.getMorceaux().get(idx - 1), a);
    }

    // =========================================================
    //  GESTION DU CATALOGUE (ADMIN)
    // =========================================================

    private void menuGestionCatalogue() {
        boolean continuer = true;
        while (continuer) {
            int choix = vue.afficherMenuGestionCatalogue();
            switch (choix) {
                case 1: ajouterArtiste(); break;
                case 2: ajouterGroupe(); break;
                case 3: ajouterAlbum(); break;
                case 4: ajouterMorceau(); break;
                case 5: supprimerMorceau(); break;
                case 6: supprimerAlbum(); break;
                case 7: supprimerArtiste(); break;
                case 8: supprimerGroupe(); break;
                case 9: continuer = false; break;
            }
        }
    }

    private void ajouterArtiste() {
        String nom = vue.lireChaine("  Nom de l'artiste : ");
        String bio = vue.lireChaineOptionnelle("  Biographie (optionnel) : ");
        Artiste a = new Artiste(nom, bio);
        catalogue.ajouterArtiste(a);
        vue.afficherMessage("Artiste \"" + nom + "\" ajouté.");
    }

    private void ajouterGroupe() {
        String nom = vue.lireChaine("  Nom du groupe : ");
        Groupe g = new Groupe(nom);
        catalogue.ajouterGroupe(g);
        vue.afficherMessage("Groupe \"" + nom + "\" ajouté.");
    }

    private void ajouterAlbum() {
        AuteurMusical auteur = choisirAuteur();
        if (auteur == null) return;
        String titre = vue.lireChaine("  Titre de l'album : ");
        int annee = vue.lireEntier("  Année de sortie : ", 1900, 2100);
        Album album = new Album(titre, annee, auteur);
        catalogue.ajouterAlbum(album);
        vue.afficherMessage("Album \"" + titre + "\" ajouté.");
    }

    private void ajouterMorceau() {
        AuteurMusical auteur = choisirAuteur();
        if (auteur == null) return;
        String titre = vue.lireChaine("  Titre du morceau : ");
        int duree = vue.lireEntier("  Durée en secondes : ", 1, 3600);
        Morceau m = new Morceau(titre, duree, auteur);
        try {
            catalogue.ajouterMorceau(m);
            vue.afficherMessage("Morceau \"" + titre + "\" ajouté.");
            // Proposer de l'associer à un album
            if (!auteur.getAlbums().isEmpty()) {
                vue.afficherInfo("Ajouter ce morceau à un album ?");
                vue.afficherListeAlbums(auteur.getAlbums());
                int idx = vue.choisirDansListe(auteur.getAlbums().size());
                if (idx > 0) {
                    auteur.getAlbums().get(idx - 1).ajouterMorceau(m);
                    vue.afficherMessage("Morceau ajouté à l'album.");
                }
            }
        } catch (MorceauDejaExistantException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    private void supprimerMorceau() {
        vue.afficherListeMorceaux(catalogue.getMorceaux());
        if (catalogue.getMorceaux().isEmpty()) return;
        int idx = vue.choisirDansListe(catalogue.getMorceaux().size());
        if (idx == 0) return;
        try {
            Morceau m = catalogue.getMorceaux().get(idx - 1);
            catalogue.supprimerMorceau(m);
            vue.afficherMessage("Morceau supprimé.");
        } catch (ElementIntrouvableException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    private void supprimerAlbum() {
        vue.afficherListeAlbums(catalogue.getAlbums());
        if (catalogue.getAlbums().isEmpty()) return;
        int idx = vue.choisirDansListe(catalogue.getAlbums().size());
        if (idx == 0) return;
        try {
            catalogue.supprimerAlbum(catalogue.getAlbums().get(idx - 1));
            vue.afficherMessage("Album supprimé.");
        } catch (ElementIntrouvableException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    private void supprimerArtiste() {
        vue.afficherListeArtistes(catalogue.getArtistes());
        if (catalogue.getArtistes().isEmpty()) return;
        int idx = vue.choisirDansListe(catalogue.getArtistes().size());
        if (idx == 0) return;
        try {
            catalogue.supprimerArtiste(catalogue.getArtistes().get(idx - 1));
            vue.afficherMessage("Artiste supprimé.");
        } catch (ElementIntrouvableException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    private void supprimerGroupe() {
        vue.afficherListeGroupes(catalogue.getGroupes());
        if (catalogue.getGroupes().isEmpty()) return;
        int idx = vue.choisirDansListe(catalogue.getGroupes().size());
        if (idx == 0) return;
        try {
            catalogue.supprimerGroupe(catalogue.getGroupes().get(idx - 1));
            vue.afficherMessage("Groupe supprimé.");
        } catch (ElementIntrouvableException e) {
            vue.afficherErreur(e.getMessage());
        }
    }

    /** Demande à l'admin de choisir entre un artiste et un groupe. */
    private AuteurMusical choisirAuteur() {
        System.out.println("  Type d'auteur : 1. Artiste  2. Groupe  0. Annuler");
        int type = vue.lireChoix(0, 2);
        if (type == 0) return null;
        if (type == 1) {
            vue.afficherListeArtistes(catalogue.getArtistes());
            if (catalogue.getArtistes().isEmpty()) { vue.afficherErreur("Aucun artiste. Ajoutez-en un d'abord."); return null; }
            int idx = vue.choisirDansListe(catalogue.getArtistes().size());
            return idx == 0 ? null : catalogue.getArtistes().get(idx - 1);
        } else {
            vue.afficherListeGroupes(catalogue.getGroupes());
            if (catalogue.getGroupes().isEmpty()) { vue.afficherErreur("Aucun groupe. Ajoutez-en un d'abord."); return null; }
            int idx = vue.choisirDansListe(catalogue.getGroupes().size());
            return idx == 0 ? null : catalogue.getGroupes().get(idx - 1);
        }
    }

    // =========================================================
    //  GESTION DES ABONNÉS (ADMIN)
    // =========================================================

    private void menuGestionAbonnes() {
        boolean continuer = true;
        while (continuer) {
            int choix = vue.afficherMenuGestionAbonnes();
            switch (choix) {
                case 1: vue.afficherListeAbonnes(abonnes); vue.attendreEntree(); break;
                case 2: toggleSuspension(); break;
                case 3: supprimerAbonne(); break;
                case 4: continuer = false; break;
            }
        }
    }

    private void toggleSuspension() {
        vue.afficherListeAbonnes(abonnes);
        if (abonnes.isEmpty()) return;
        int idx = vue.choisirDansListe(abonnes.size());
        if (idx == 0) return;
        Abonne a = abonnes.get(idx - 1);
        a.setActif(!a.isActif());
        String etat = a.isActif() ? "réactivé" : "suspendu";
        vue.afficherMessage("Compte de " + a.getLogin() + " " + etat + ".");
    }

    private void supprimerAbonne() {
        vue.afficherListeAbonnes(abonnes);
        if (abonnes.isEmpty()) return;
        int idx = vue.choisirDansListe(abonnes.size());
        if (idx == 0) return;
        Abonne a = abonnes.get(idx - 1);
        abonnes.remove(a);
        vue.afficherMessage("Compte de " + a.getLogin() + " supprimé.");
    }

    // =========================================================
    //  PERSISTANCE
    // =========================================================

    /**
     * Charge le catalogue et la liste des abonnés.
     * Priorité : fichier catalogue.ser (sauvegarde binaire) → catalogue.txt (données initiales).
     * Les abonnés sont toujours chargés depuis abonnes.ser si disponible.
     */
    @SuppressWarnings("unchecked")
    private void chargerDonnees() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        // Catalogue : d'abord la sauvegarde binaire, sinon le fichier texte initial
        File fichierCatalogue = new File(CATALOGUE_FILE);
        if (fichierCatalogue.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichierCatalogue))) {
                catalogue = (Catalogue) ois.readObject();
                vue.afficherMessage("Catalogue chargé (" + catalogue.getMorceaux().size() + " morceaux).");
            } catch (Exception e) {
                vue.afficherErreur("Sauvegarde corrompue. Rechargement depuis catalogue.txt.");
                catalogue = new Catalogue();
                chargerCatalogueTxt();
            }
        } else {
            catalogue = new Catalogue();
            chargerCatalogueTxt();
        }

        // Abonnés
        File fichierAbonnes = new File(ABONNES_FILE);
        if (fichierAbonnes.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichierAbonnes))) {
                abonnes = (ArrayList<Abonne>) ois.readObject();
            } catch (Exception e) {
                vue.afficherErreur("Impossible de charger les abonnés.");
                abonnes = new ArrayList<>();
            }
        }
    }

    /**
     * Charge le catalogue depuis data/catalogue.txt.
     * Si le fichier est introuvable, utilise les données codées en dur comme fallback.
     */
    private void chargerCatalogueTxt() {
        if (DataLoader.chargerCatalogueDepuisTxt(catalogue)) {
            vue.afficherMessage("Catalogue chargé depuis catalogue.txt (" + catalogue.getMorceaux().size() + " morceaux).");
        } else {
            vue.afficherInfo("catalogue.txt introuvable — chargement des données par défaut.");
            DataLoader.initialiserDonneesDemo(catalogue);
        }
    }

    /** Sauvegarde le catalogue et la liste des abonnés dans des fichiers. */
    private void sauvegarderDonnees() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CATALOGUE_FILE))) {
            oos.writeObject(catalogue);
        } catch (IOException e) {
            vue.afficherErreur("Erreur lors de la sauvegarde du catalogue : " + e.getMessage());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ABONNES_FILE))) {
            oos.writeObject(abonnes);
        } catch (IOException e) {
            vue.afficherErreur("Erreur lors de la sauvegarde des abonnés : " + e.getMessage());
        }
        vue.afficherMessage("Données sauvegardées.");
    }

}

