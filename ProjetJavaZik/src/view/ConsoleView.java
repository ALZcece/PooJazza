package view;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

/**
 * Vue console de l'application JavaZic.
 * Gère l'affichage et la lecture des entrées utilisateur.
 * Ne contient aucune logique métier.
 */
public class ConsoleView {

    private final Scanner scanner;
    private static final String SEP  = "══════════════════════════════════════════════════";
    private static final String SEP2 = "──────────────────────────────────────────────────";

    public ConsoleView() {
        scanner = new Scanner(System.in);
    }

    // =========================================================
    //  MENUS PRINCIPAUX
    // =========================================================

    /** Affiche le menu principal et retourne le choix (1-5). */
    public int afficherMenuPrincipal() {
        System.out.println("\n" + SEP);
        System.out.println("        ♪  J A V A Z I C  ♪");
        System.out.println(SEP);
        System.out.println("  1. Se connecter (administrateur)");
        System.out.println("  2. Se connecter (abonné)");
        System.out.println("  3. Créer un compte abonné");
        System.out.println("  4. Continuer en tant que visiteur");
        System.out.println("  5. Quitter");
        System.out.println(SEP);
        return lireChoix(1, 5);
    }

    /** Affiche le menu visiteur et retourne le choix (1-3). */
    public int afficherMenuVisiteur(int ecoutesRestantes) {
        System.out.println("\n" + SEP);
        System.out.println("  MODE VISITEUR  (écoutes restantes : " + ecoutesRestantes + "/" + Visiteur.MAX_ECOUTES_SESSION + ")");
        System.out.println(SEP);
        System.out.println("  1. Parcourir le catalogue");
        System.out.println("  2. Rechercher dans le catalogue");
        System.out.println("  3. Retour au menu principal");
        System.out.println(SEP);
        return lireChoix(1, 3);
    }

    /** Affiche le menu abonné et retourne le choix (1-5). */
    public int afficherMenuAbonne(Abonne a) {
        System.out.println("\n" + SEP);
        System.out.println("  Bonjour, " + a.getPrenom() + " !");
        System.out.println(SEP);
        System.out.println("  1. Parcourir le catalogue");
        System.out.println("  2. Rechercher dans le catalogue");
        System.out.println("  3. Mes playlists");
        System.out.println("  4. Mon historique d'écoute");
        System.out.println("  5. Déconnexion");
        System.out.println(SEP);
        return lireChoix(1, 5);
    }

    /** Affiche le menu administrateur et retourne le choix (1-4). */
    public int afficherMenuAdmin() {
        System.out.println("\n" + SEP);
        System.out.println("  ESPACE ADMINISTRATEUR");
        System.out.println(SEP);
        System.out.println("  1. Gérer le catalogue");
        System.out.println("  2. Gérer les abonnés");
        System.out.println("  3. Statistiques");
        System.out.println("  4. Déconnexion");
        System.out.println(SEP);
        return lireChoix(1, 4);
    }

    // =========================================================
    //  SOUS-MENUS CATALOGUE
    // =========================================================

    /** Retourne le choix dans le menu de navigation du catalogue (1-5). */
    public int afficherMenuCatalogue() {
        System.out.println("\n" + SEP2);
        System.out.println("  CATALOGUE");
        System.out.println(SEP2);
        System.out.println("  1. Tous les morceaux");
        System.out.println("  2. Tous les albums");
        System.out.println("  3. Tous les artistes");
        System.out.println("  4. Tous les groupes");
        System.out.println("  5. Retour");
        System.out.println(SEP2);
        return lireChoix(1, 5);
    }

    /** Retourne le choix dans le menu de recherche (1-5). */
    public int afficherMenuRecherche() {
        System.out.println("\n" + SEP2);
        System.out.println("  RECHERCHE");
        System.out.println(SEP2);
        System.out.println("  1. Rechercher un morceau");
        System.out.println("  2. Rechercher un album");
        System.out.println("  3. Rechercher un artiste");
        System.out.println("  4. Rechercher un groupe");
        System.out.println("  5. Retour");
        System.out.println(SEP2);
        return lireChoix(1, 5);
    }

    /** Retourne le choix dans les options d'un morceau (1-3 ou 1-5 selon le type d'utilisateur). */
    public int afficherMenuOptionsMorceau(boolean estAbonne) {
        System.out.println("\n  Options :");
        System.out.println("  1. Écouter");
        System.out.println("  2. Voir les avis");
        if (estAbonne) {
            System.out.println("  3. Laisser / modifier mon avis");
            System.out.println("  4. Supprimer mon avis");
            System.out.println("  5. Retour");
            return lireChoix(1, 5);
        } else {
            System.out.println("  3. Retour");
            return lireChoix(1, 3);
        }
    }

    /** Retourne le choix dans les options d'un album. */
    public int afficherMenuOptionsAlbum() {
        System.out.println("\n  Options album :");
        System.out.println("  1. Sélectionner un morceau");
        System.out.println("  2. Voir l'artiste / groupe");
        System.out.println("  3. Retour");
        return lireChoix(1, 3);
    }

    /** Retourne le choix dans les options d'un artiste/groupe. */
    public int afficherMenuOptionsAuteur() {
        System.out.println("\n  Options :");
        System.out.println("  1. Voir les albums");
        System.out.println("  2. Voir tous les morceaux");
        System.out.println("  3. Retour");
        return lireChoix(1, 3);
    }

    // =========================================================
    //  SOUS-MENUS PLAYLISTS
    // =========================================================

    /** Retourne le choix dans le menu playlists (1-8). */
    public int afficherMenuPlaylists() {
        System.out.println("\n" + SEP2);
        System.out.println("  MES PLAYLISTS");
        System.out.println(SEP2);
        System.out.println("  1. Voir mes playlists");
        System.out.println("  2. Créer une playlist");
        System.out.println("  3. Renommer une playlist");
        System.out.println("  4. Supprimer une playlist");
        System.out.println("  5. Gérer les morceaux d'une playlist");
        System.out.println("  6. Playlists partagées avec moi");
        System.out.println("  7. Gérer les collaborateurs d'une playlist");
        System.out.println("  8. Retour");
        System.out.println(SEP2);
        return lireChoix(1, 8);
    }

    /** Retourne le choix dans le menu détail d'une playlist (1-5). */
    public int afficherMenuDetailPlaylist(Playlist p) {
        System.out.println("\n  Playlist : \"" + p.getNom() + "\"");
        System.out.println(SEP2);
        System.out.println("  1. Voir les morceaux");
        System.out.println("  2. Ajouter un morceau (catalogue)");
        System.out.println("  3. Retirer un morceau");
        System.out.println("  4. Écouter un morceau");
        System.out.println("  5. Retour");
        System.out.println(SEP2);
        return lireChoix(1, 5);
    }

    // =========================================================
    //  SOUS-MENUS ADMIN
    // =========================================================

    /** Retourne le choix dans le menu gestion du catalogue (1-9). */
    public int afficherMenuGestionCatalogue() {
        System.out.println("\n" + SEP2);
        System.out.println("  GESTION DU CATALOGUE");
        System.out.println(SEP2);
        System.out.println("  1. Ajouter un artiste");
        System.out.println("  2. Ajouter un groupe");
        System.out.println("  3. Ajouter un album");
        System.out.println("  4. Ajouter un morceau");
        System.out.println("  5. Supprimer un morceau");
        System.out.println("  6. Supprimer un album");
        System.out.println("  7. Supprimer un artiste");
        System.out.println("  8. Supprimer un groupe");
        System.out.println("  9. Retour");
        System.out.println(SEP2);
        return lireChoix(1, 9);
    }

    /** Retourne le choix dans le menu gestion des abonnés (1-4). */
    public int afficherMenuGestionAbonnes() {
        System.out.println("\n" + SEP2);
        System.out.println("  GESTION DES ABONNÉS");
        System.out.println(SEP2);
        System.out.println("  1. Lister tous les abonnés");
        System.out.println("  2. Suspendre / Réactiver un compte");
        System.out.println("  3. Supprimer un compte");
        System.out.println("  4. Retour");
        System.out.println(SEP2);
        return lireChoix(1, 4);
    }

    // =========================================================
    //  AFFICHAGE DES LISTES
    // =========================================================

    public void afficherListeMorceaux(ArrayList<Morceau> morceaux) {
        if (morceaux.isEmpty()) { afficherInfo("Aucun morceau à afficher."); return; }
        System.out.println();
        for (int i = 0; i < morceaux.size(); i++)
            System.out.printf("  %3d. %s%n", i + 1, morceaux.get(i));
    }

    public void afficherListeAlbums(ArrayList<Album> albums) {
        if (albums.isEmpty()) { afficherInfo("Aucun album à afficher."); return; }
        System.out.println();
        for (int i = 0; i < albums.size(); i++)
            System.out.printf("  %3d. %s%n", i + 1, albums.get(i));
    }

    public void afficherListeArtistes(ArrayList<Artiste> artistes) {
        if (artistes.isEmpty()) { afficherInfo("Aucun artiste à afficher."); return; }
        System.out.println();
        for (int i = 0; i < artistes.size(); i++)
            System.out.printf("  %3d. %s%n", i + 1, artistes.get(i).getNom());
    }

    public void afficherListeGroupes(ArrayList<Groupe> groupes) {
        if (groupes.isEmpty()) { afficherInfo("Aucun groupe à afficher."); return; }
        System.out.println();
        for (int i = 0; i < groupes.size(); i++)
            System.out.printf("  %3d. %s%n", i + 1, groupes.get(i));
    }

    public void afficherListeAbonnes(ArrayList<Abonne> abonnes) {
        if (abonnes.isEmpty()) { afficherInfo("Aucun abonné enregistré."); return; }
        System.out.println();
        for (int i = 0; i < abonnes.size(); i++)
            System.out.printf("  %3d. %s%n", i + 1, abonnes.get(i));
    }

    public void afficherPlaylists(ArrayList<Playlist> playlists) {
        if (playlists.isEmpty()) { afficherInfo("Vous n'avez aucune playlist."); return; }
        System.out.println();
        for (int i = 0; i < playlists.size(); i++)
            System.out.printf("  %3d. %s%n", i + 1, playlists.get(i));
    }

    // =========================================================
    //  AFFICHAGE DES DÉTAILS
    // =========================================================

    public void afficherDetailMorceau(Morceau m) {
        System.out.println("\n" + SEP2);
        System.out.println("  ♪ " + m.getTitre());
        System.out.println(SEP2);
        System.out.println("  Auteur  : " + (m.getAuteur() != null ? m.getAuteur().getNom() : "Inconnu"));
        System.out.println("  Durée   : " + m.getDureeFormatee());
        System.out.println("  Écoutes : " + m.getNbEcoutes());
        if (!m.getAlbums().isEmpty()) {
            System.out.print("  Albums  : ");
            m.getAlbums().forEach(a -> System.out.print("\"" + a.getTitre() + "\"  "));
            System.out.println();
        }
        if (!m.getAvis().isEmpty())
            System.out.printf("  Note    : %.1f/5 (%d avis)%n", m.getNoteMoyenne(), m.getAvis().size());
        System.out.println(SEP2);
    }

    public void afficherDetailAlbum(Album a) {
        System.out.println("\n" + SEP2);
        System.out.println("  Album  : " + a.getTitre());
        System.out.println("  Auteur : " + (a.getAuteur() != null ? a.getAuteur().getNom() : "Inconnu"));
        System.out.println("  Année  : " + a.getAnnee());
        System.out.println("  Durée  : " + a.getDureeTotaleFormatee());
        System.out.println("  Titres : " + a.getMorceaux().size());
        System.out.println(SEP2);
        afficherListeMorceaux(a.getMorceaux());
        System.out.println(SEP2);
    }

    public void afficherDetailArtiste(Artiste a) {
        System.out.println("\n" + SEP2);
        System.out.println("  Artiste : " + a.getNom());
        if (a.getGroupe() != null)
            System.out.println("  Groupe  : " + a.getGroupe().getNom());
        if (!a.getBiographie().isEmpty())
            System.out.println("  Bio     : " + a.getBiographie());
        System.out.println("  Albums  : " + a.getAlbums().size());
        System.out.println("  Titres  : " + a.getMorceaux().size());
        System.out.println(SEP2);
    }

    public void afficherDetailGroupe(Groupe g) {
        System.out.println("\n" + SEP2);
        System.out.println("  Groupe  : " + g.getNom());
        System.out.print("  Membres : ");
        g.getMembres().forEach(m -> System.out.print(m.getNom() + "  "));
        System.out.println();
        System.out.println("  Albums  : " + g.getAlbums().size());
        System.out.println("  Titres  : " + g.getMorceaux().size());
        System.out.println(SEP2);
    }

    public void afficherHistorique(HistoriqueEcoute h) {
        LinkedList<Morceau> morceaux = h.getMorceaux();
        if (morceaux.isEmpty()) { afficherInfo("Votre historique est vide."); return; }
        System.out.println("\n" + SEP2);
        System.out.println("  HISTORIQUE D'ÉCOUTE (du plus récent)");
        System.out.println(SEP2);
        int i = 1;
        for (Morceau m : morceaux)
            System.out.printf("  %3d. %s%n", i++, m);
        System.out.println(SEP2);
    }

    public void afficherAvis(ArrayList<Avis> avisList) {
        if (avisList.isEmpty()) { afficherInfo("Aucun avis pour ce morceau."); return; }
        System.out.println("\n  Avis :");
        avisList.forEach(a -> System.out.println("    • " + a));
    }

    public void afficherStatistiques(Catalogue catalogue, ArrayList<Abonne> abonnes) {
        System.out.println("\n" + SEP);
        System.out.println("  STATISTIQUES JAVAZIC");
        System.out.println(SEP);

        // Vue d'ensemble
        int actifs = 0;
        for (Abonne a : abonnes) if (a.isActif()) actifs++;
        System.out.println("  Abonnés inscrits  : " + abonnes.size() + " (" + actifs + " actifs, " + (abonnes.size() - actifs) + " suspendus)");
        System.out.println("  Morceaux          : " + catalogue.getMorceaux().size());
        System.out.println("  Albums            : " + catalogue.getAlbums().size());
        System.out.println("  Artistes          : " + catalogue.getArtistes().size());
        System.out.println("  Groupes           : " + catalogue.getGroupes().size());
        System.out.println("  Écoutes totales   : " + catalogue.getNbEcoutesTotales());
        int dureeTotale = catalogue.getMorceaux().stream().mapToInt(Morceau::getDuree).sum();
        System.out.printf("  Durée catalogue   : %dh%02dm%n", dureeTotale / 3600, (dureeTotale % 3600) / 60);
        int totalPl = 0;
        for (Abonne a : abonnes) totalPl += a.getPlaylists().size();
        System.out.println("  Playlists créées  : " + totalPl);
        int totalAvis = 0;
        for (Morceau m : catalogue.getMorceaux()) totalAvis += m.getAvis().size();
        System.out.println("  Avis déposés      : " + totalAvis);

        // Top 5 morceaux par écoutes
        System.out.println("\n" + SEP2);
        System.out.println("  Top 5 morceaux (par écoutes) :");
        System.out.println(SEP2);
        ArrayList<Morceau> tops = catalogue.getMorceauxParEcoutes();
        for (int i = 0; i < Math.min(5, tops.size()); i++)
            System.out.printf("    %d. %-30s %d écoute(s)%n", i + 1, tops.get(i).getTitre(), tops.get(i).getNbEcoutes());

        // Top 5 albums par écoutes cumulées
        System.out.println("\n" + SEP2);
        System.out.println("  Top 5 albums (par écoutes cumulées) :");
        System.out.println(SEP2);
        ArrayList<Album> albumsTries = new ArrayList<>(catalogue.getAlbums());
        albumsTries.sort((a1, a2) -> {
            int e1 = a1.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            int e2 = a2.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            return Integer.compare(e2, e1);
        });
        for (int i = 0; i < Math.min(5, albumsTries.size()); i++) {
            Album a = albumsTries.get(i);
            int ec = a.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            System.out.printf("    %d. %-30s %d écoute(s)%n", i + 1, a.getTitre(), ec);
        }

        // Top 5 artistes/groupes par écoutes cumulées
        System.out.println("\n" + SEP2);
        System.out.println("  Top 5 artistes/groupes (par écoutes cumulées) :");
        System.out.println(SEP2);
        ArrayList<AuteurMusical> auteurs = new ArrayList<>();
        auteurs.addAll(catalogue.getArtistes());
        auteurs.addAll(catalogue.getGroupes());
        auteurs.sort((a1, a2) -> {
            int e1 = a1.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            int e2 = a2.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            return Integer.compare(e2, e1);
        });
        for (int i = 0; i < Math.min(5, auteurs.size()); i++) {
            AuteurMusical a = auteurs.get(i);
            int ec = a.getMorceaux().stream().mapToInt(Morceau::getNbEcoutes).sum();
            String type = a instanceof Artiste ? "Artiste" : "Groupe";
            System.out.printf("    %d. %-25s [%s]  %d écoute(s)%n", i + 1, a.getNom(), type, ec);
        }

        // Top 5 morceaux les mieux notés
        System.out.println("\n" + SEP2);
        System.out.println("  Top 5 morceaux (par note moyenne) :");
        System.out.println(SEP2);
        ArrayList<Morceau> morceauxNotes = new ArrayList<>();
        for (Morceau m : catalogue.getMorceaux()) if (!m.getAvis().isEmpty()) morceauxNotes.add(m);
        morceauxNotes.sort((a1, a2) -> Double.compare(a2.getNoteMoyenne(), a1.getNoteMoyenne()));
        for (int i = 0; i < Math.min(5, morceauxNotes.size()); i++) {
            Morceau m = morceauxNotes.get(i);
            System.out.printf("    %d. %-30s %.1f/5 (%d avis)%n", i + 1, m.getTitre(), m.getNoteMoyenne(), m.getAvis().size());
        }
        if (morceauxNotes.isEmpty()) System.out.println("    (aucun avis déposé)");

        // Top 5 morceaux les plus ajoutés aux playlists
        System.out.println("\n" + SEP2);
        System.out.println("  Top 5 morceaux (les plus ajoutés aux playlists) :");
        System.out.println(SEP2);
        HashMap<Morceau, Integer> plCount = new HashMap<>();
        for (Abonne a : abonnes)
            for (Playlist p : a.getPlaylists())
                for (Morceau m : p.getMorceaux())
                    plCount.merge(m, 1, Integer::sum);
        ArrayList<Map.Entry<Morceau, Integer>> plRank = new ArrayList<>(plCount.entrySet());
        plRank.sort((a1, a2) -> Integer.compare(a2.getValue(), a1.getValue()));
        for (int i = 0; i < Math.min(5, plRank.size()); i++) {
            Morceau m = plRank.get(i).getKey();
            System.out.printf("    %d. %-30s dans %d playlist(s)%n", i + 1, m.getTitre(), plRank.get(i).getValue());
        }
        if (plRank.isEmpty()) System.out.println("    (aucun morceau dans les playlists)");

        // Abonnés les plus actifs
        System.out.println("\n" + SEP2);
        System.out.println("  Abonnés les plus actifs :");
        System.out.println(SEP2);
        ArrayList<Abonne> abonnesTries = new ArrayList<>(abonnes);
        abonnesTries.sort((a1, a2) -> Integer.compare(a2.getHistorique().getMorceaux().size(), a1.getHistorique().getMorceaux().size()));
        for (int i = 0; i < Math.min(5, abonnesTries.size()); i++) {
            Abonne a = abonnesTries.get(i);
            System.out.printf("    %d. %-20s %d écoutes, %d playlist(s)%n",
                    i + 1, a.getLogin(), a.getHistorique().getMorceaux().size(), a.getPlaylists().size());
        }

        System.out.println("\n" + SEP);
    }

    // =========================================================
    //  SIMULATION D'ÉCOUTE
    // =========================================================

    /**
     * Simule la lecture d'un morceau avec une barre de progression ASCII.
     */
    public void simulerEcoute(Morceau m) {
        System.out.println("\n  ▶  " + m.getTitre() + "  (" + m.getDureeFormatee() + ")");
        System.out.print("  [");
        for (int i = 0; i < 30; i++) {
            System.out.print("█");
            try { Thread.sleep(60); } catch (InterruptedException ignored) {}
        }
        System.out.println("]  ✓");
    }

    // =========================================================
    //  SAISIE UTILISATEUR
    // =========================================================

    /**
     * Lit une chaîne non vide saisie par l'utilisateur.
     */
    public String lireChaine(String prompt) {
        String val;
        do {
            System.out.print(prompt);
            val = scanner.nextLine().trim();
            if (val.isEmpty()) afficherErreur("La saisie ne peut pas être vide.");
        } while (val.isEmpty());
        return val;
    }

    /**
     * Lit une chaîne qui peut être vide (touche Entrée = chaîne vide).
     */
    public String lireChaineOptionnelle(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Lit un entier quelconque.
     */
    public int lireEntier(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { afficherErreur("Veuillez entrer un nombre entier."); }
        }
    }

    /**
     * Lit un entier dans l'intervalle [min, max] (bornes incluses).
     */
    public int lireEntier(String prompt, int min, int max) {
        while (true) {
            int val = lireEntier(prompt);
            if (val >= min && val <= max) return val;
            afficherErreur("Entrez un nombre entre " + min + " et " + max + ".");
        }
    }

    /**
     * Raccourci pour lire un choix de menu entre min et max.
     */
    public int lireChoix(int min, int max) {
        return lireEntier("  Votre choix : ", min, max);
    }

    /**
     * Demande de sélectionner un élément dans une liste (0 = retour).
     * @param taille taille de la liste
     * @return numéro sélectionné (1 à taille) ou 0 pour revenir
     */
    public int choisirDansListe(int taille) {
        if (taille == 0) return 0;
        return lireEntier("  Sélectionner (0 = retour) : ", 0, taille);
    }

    // =========================================================
    //  MESSAGES
    // =========================================================

    public void afficherMessage(String msg) { System.out.println("  ✓ " + msg); }
    public void afficherInfo(String msg)    { System.out.println("  " + msg); }
    public void afficherErreur(String msg)  { System.out.println("  ✗ " + msg); }
    public void attendreEntree() {
        System.out.print("  [Appuyez sur Entrée pour continuer...]");
        scanner.nextLine();
    }
}
