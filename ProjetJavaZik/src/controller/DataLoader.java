package controller;

import model.*;
import model.exceptions.MorceauDejaExistantException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Charge le catalogue musical depuis le fichier texte {@code data/catalogue.txt}.
 * Permet d'initialiser le catalogue avec des données lisibles et modifiables sans recompiler.
 */
public class DataLoader {

    /**
     * Lit {@code data/catalogue.txt} et peuple le catalogue fourni.
     * Cherche le fichier dans plusieurs emplacements possibles selon le contexte d'exécution.
     * Ignore les lignes vides et les commentaires (lignes commençant par #).
     *
     * @param catalogue le catalogue à remplir
     * @return true si le chargement a réussi, false sinon
     */
    public static boolean chargerCatalogueDepuisTxt(Catalogue catalogue) {
        File f = trouverFichierCatalogue();
        if (f == null) {
            System.err.println("[DataLoader] catalogue.txt introuvable. Répertoire courant : "
                    + new File(".").getAbsolutePath());
            return false;
        }
        System.out.println("[DataLoader] Chargement depuis : " + f.getAbsolutePath());

        // Maps temporaires pour résoudre les références par nom
        HashMap<String, Artiste> artistesMap = new HashMap<>();
        HashMap<String, Groupe>  groupesMap  = new HashMap<>();
        HashMap<String, Album>   albumsMap   = new HashMap<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            String ligne;
            while ((ligne = br.readLine()) != null) {
                ligne = ligne.trim();
                if (ligne.isEmpty() || ligne.startsWith("#")) continue;

                String[] parts = ligne.split("\\|", -1);
                String type = parts[0].toUpperCase();

                switch (type) {
                    case "ARTISTE":
                        if (parts.length >= 2) {
                            String nom = parts[1].trim();
                            String bio = parts.length >= 3 ? parts[2].trim() : "";
                            Artiste a = new Artiste(nom, bio);
                            catalogue.ajouterArtiste(a);
                            artistesMap.put(nom.toLowerCase(), a);
                        }
                        break;

                    case "GROUPE":
                        if (parts.length >= 2) {
                            String nom = parts[1].trim();
                            Groupe g = new Groupe(nom);
                            if (parts.length >= 3) {
                                String[] membres = parts[2].split(";");
                                for (String membreNom : membres) {
                                    Artiste membre = artistesMap.get(membreNom.trim().toLowerCase());
                                    if (membre != null) g.ajouterMembre(membre);
                                }
                            }
                            catalogue.ajouterGroupe(g);
                            groupesMap.put(nom.toLowerCase(), g);
                        }
                        break;

                    case "ALBUM":
                        if (parts.length >= 4) {
                            String titre  = parts[1].trim();
                            int annee     = Integer.parseInt(parts[2].trim());
                            String auteurNom = parts[3].trim();
                            AuteurMusical auteur = resoudreAuteur(auteurNom, artistesMap, groupesMap);
                            if (auteur != null) {
                                Album album = new Album(titre, annee, auteur);
                                catalogue.ajouterAlbum(album);
                                albumsMap.put(titre.toLowerCase(), album);
                            }
                        }
                        break;

                    case "MORCEAU":
                        if (parts.length >= 4) {
                            String titre     = parts[1].trim();
                            int duree        = Integer.parseInt(parts[2].trim());
                            String auteurNom = parts[3].trim();
                            String albumTitre = parts.length >= 5 ? parts[4].trim() : "";
                            String genreStr   = parts.length >= 6 ? parts[5].trim() : "";
                            Genre genre       = Genre.fromString(genreStr);
                            AuteurMusical auteur = resoudreAuteur(auteurNom, artistesMap, groupesMap);
                            if (auteur != null) {
                                Morceau m = new Morceau(titre, duree, auteur, genre);
                                try {
                                    catalogue.ajouterMorceau(m);
                                    if (!albumTitre.isEmpty()) {
                                        Album album = albumsMap.get(albumTitre.toLowerCase());
                                        if (album != null) album.ajouterMorceau(m);
                                    }
                                } catch (MorceauDejaExistantException ignored) {}
                            }
                        }
                        break;

                    default:
                        // Ligne inconnue, ignorée
                        break;
                }
            }
            return true;

        } catch (IOException | NumberFormatException e) {
            System.err.println("Erreur lors du chargement du catalogue : " + e.getMessage());
            return false;
        }
    }

    /**
     * Cherche catalogue.txt dans plusieurs emplacements possibles selon le contexte d'exécution.
     * @return le File trouvé, ou null si introuvable
     */
    private static File trouverFichierCatalogue() {
        String[] chemins = {
            "data/catalogue.txt",
            "ProjetJavaZik/data/catalogue.txt",
            "../data/catalogue.txt",
            System.getProperty("user.dir") + "/data/catalogue.txt",
            System.getProperty("user.dir") + "/ProjetJavaZik/data/catalogue.txt"
        };
        for (String chemin : chemins) {
            File f = new File(chemin);
            if (f.exists()) return f;
        }
        return null;
    }

    // =========================================================
    //  DONNÉES DE DÉMONSTRATION (fallback statique)
    // =========================================================

    /**
     * Initialise le catalogue avec des données de démonstration codées en dur.
     * Utilisé quand catalogue.txt est introuvable.
     */
    public static void initialiserDonneesDemo(Catalogue catalogue) {
        try {
            Groupe beatles = new Groupe("The Beatles");
            Artiste john   = new Artiste("John Lennon",   "Auteur-compositeur et leader des Beatles.");
            Artiste paul   = new Artiste("Paul McCartney","Bassiste et co-auteur principal.");
            Artiste george = new Artiste("George Harrison","Guitariste des Beatles.");
            Artiste ringo  = new Artiste("Ringo Starr",   "Batteur des Beatles.");
            beatles.ajouterMembre(john); beatles.ajouterMembre(paul);
            beatles.ajouterMembre(george); beatles.ajouterMembre(ringo);
            catalogue.ajouterArtiste(john); catalogue.ajouterArtiste(paul);
            catalogue.ajouterArtiste(george); catalogue.ajouterArtiste(ringo);
            catalogue.ajouterGroupe(beatles);
            ajouterAlbumAvecMorceaux(catalogue, "Abbey Road", 1969, beatles,
                new String[]{"Come Together","Something","Here Comes the Sun","Oh! Darling","Let It Be"},
                new int[]{259,183,185,207,243}, Genre.ROCK);
            ajouterAlbumAvecMorceaux(catalogue, "Let It Be", 1970, beatles,
                new String[]{"Get Back","The Long and Winding Road"},
                new int[]{191,218}, Genre.ROCK);

            Artiste mj = new Artiste("Michael Jackson","Roi de la Pop.");
            catalogue.ajouterArtiste(mj);
            ajouterAlbumAvecMorceaux(catalogue, "Thriller", 1982, mj,
                new String[]{"Thriller","Billie Jean","Beat It"},
                new int[]{358,294,258}, Genre.POP);
            ajouterAlbumAvecMorceaux(catalogue, "Bad", 1987, mj,
                new String[]{"Bad","The Way You Make Me Feel","Man in the Mirror"},
                new int[]{247,300,318}, Genre.POP);

            Artiste adele = new Artiste("Adele","Chanteuse britannique récompensée aux Grammy Awards.");
            catalogue.ajouterArtiste(adele);
            ajouterAlbumAvecMorceaux(catalogue, "21", 2011, adele,
                new String[]{"Rolling in the Deep","Someone Like You","Set Fire to the Rain"},
                new int[]{228,285,242}, Genre.SOUL);

            Groupe daftPunk = new Groupe("Daft Punk");
            daftPunk.ajouterMembre(new Artiste("Thomas Bangalter"));
            daftPunk.ajouterMembre(new Artiste("Guy-Manuel de Homem-Christo"));
            catalogue.ajouterGroupe(daftPunk);
            ajouterAlbumAvecMorceaux(catalogue, "Random Access Memories", 2013, daftPunk,
                new String[]{"Get Lucky","Lose Yourself to Dance","Instant Crush","Within"},
                new int[]{369,353,338,228}, Genre.ELECTRO);

            Groupe queen = new Groupe("Queen");
            queen.ajouterMembre(new Artiste("Freddie Mercury","Chanteur légendaire et frontman de Queen."));
            queen.ajouterMembre(new Artiste("Brian May","Guitariste de Queen."));
            catalogue.ajouterGroupe(queen);
            ajouterAlbumAvecMorceaux(catalogue, "A Night at the Opera", 1975, queen,
                new String[]{"Bohemian Rhapsody","You're My Best Friend","Love of My Life"},
                new int[]{354,170,218}, Genre.ROCK);
            ajouterAlbumAvecMorceaux(catalogue, "Greatest Hits", 1981, queen,
                new String[]{"We Will Rock You","We Are the Champions","Don't Stop Me Now"},
                new int[]{122,179,209}, Genre.ROCK);

        } catch (Exception e) {
            System.err.println("Erreur initialisation démo : " + e.getMessage());
        }
    }

    private static void ajouterAlbumAvecMorceaux(Catalogue catalogue, String titre, int annee,
                                                  AuteurMusical auteur, String[] titres, int[] durees, Genre genre) {
        Album album = new Album(titre, annee, auteur);
        catalogue.ajouterAlbum(album);
        for (int i = 0; i < titres.length; i++) {
            try {
                Morceau m = new Morceau(titres[i], durees[i], auteur, genre);
                catalogue.ajouterMorceau(m);
                album.ajouterMorceau(m);
            } catch (model.exceptions.MorceauDejaExistantException ignored) {}
        }
    }

    /** Résout un auteur (artiste ou groupe) à partir de son nom. */
    private static AuteurMusical resoudreAuteur(String nom,
                                                HashMap<String, Artiste> artistesMap,
                                                HashMap<String, Groupe> groupesMap) {
        String key = nom.toLowerCase();
        if (groupesMap.containsKey(key))  return groupesMap.get(key);
        if (artistesMap.containsKey(key)) return artistesMap.get(key);
        return null;
    }
}
