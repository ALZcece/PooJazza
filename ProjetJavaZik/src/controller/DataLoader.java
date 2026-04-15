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
    /* Cette fonction chargerCatalogueDepuisTxt permet de remplir le catalogue musical
     à partir d’un fichier texte externe nommé catalogue.txt.
     Elle commence par rechercher le fichier dans différents emplacements possibles,
     ce qui rend le programme plus souple selon le dossier depuis lequel il est exécuté.
     Si le fichier est introuvable, la fonction affiche un message d’erreur
     et retourne false pour signaler l’échec du chargement.
     Si le fichier est trouvé, la fonction l’ouvre en lecture avec l’encodage UTF-8
     afin de préserver correctement les accents et caractères spéciaux.
     Trois HashMap temporaires sont ensuite créées pour mémoriser les artistes,
     les groupes et les albums déjà rencontrés pendant la lecture du fichier.
     Ces structures servent à résoudre les références entre objets par leur nom.
     La lecture se fait ligne par ligne.
     Les lignes vides ainsi que les lignes de commentaire commençant par # sont ignorées.
     Chaque ligne utile est découpée avec le séparateur "|" afin d’extraire ses informations.
     Le premier champ indique le type de donnée à créer : ARTISTE, GROUPE, ALBUM ou MORCEAU.
     Pour un artiste, la fonction crée l’objet correspondant puis l’ajoute au catalogue
     et à la map des artistes pour pouvoir le retrouver plus tard.
     Pour un groupe, elle crée le groupe, ajoute éventuellement ses membres
     s’ils ont déjà été déclarés comme artistes, puis l’enregistre dans le catalogue.
     Pour un album, elle convertit l’année, résout son auteur grâce à la fonction resoudreAuteur,
     puis crée l’album et l’ajoute au catalogue ainsi qu’à la map des albums.
     Pour un morceau, elle lit le titre, la durée, l’auteur et éventuellement l’album associé.
     Elle crée ensuite le morceau puis tente de l’ajouter au catalogue.
     Si un titre d’album est fourni et que cet album existe,
     le morceau est également ajouté à cet album.
     La fonction ignore silencieusement les exceptions de doublon de morceau
     afin d’éviter qu’un doublon dans le fichier ne bloque entièrement le chargement.
     Si tout se passe bien, elle retourne true.
     En cas d’erreur de lecture ou de conversion numérique,
     elle affiche un message et retourne false.*/

    
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
    
    /* Cette fonction trouverFichierCatalogue a pour rôle de localiser le fichier catalogue.txt
     dans plusieurs dossiers possibles.
     Elle est utile car le programme peut être lancé depuis différents environnements :
     depuis un IDE, depuis un dossier projet, ou depuis un répertoire parent.
     Pour cela, elle définit un tableau de chemins potentiels.
     Elle parcourt ensuite ces chemins un par un.
     Pour chaque chemin, elle crée un objet File
     et vérifie si le fichier existe réellement.
     Dès qu’un fichier valide est trouvé, il est immédiatement retourné.
     Cela permet au reste du programme d’utiliser le bon fichier sans se soucier de son emplacement exact.
     Si aucun des chemins testés ne contient le fichier recherché,
     la fonction retourne null.
     Cette valeur null indique clairement au programme appelant
     qu’aucun fichier catalogue n’a pu être localisé.*/
    
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
        // Cette fonction initialiserDonneesDemo sert à remplir automatiquement le catalogue
    /*avec un ensemble de données de démonstration codées directement dans le programme.
    Elle est utilisée comme solution de secours lorsque le fichier catalogue.txt est absent
    ou impossible à exploiter.
    Son objectif est de permettre à l’application de rester fonctionnelle même sans fichier externe.
    Elle crée plusieurs artistes et groupes célèbres afin de fournir une base de test réaliste.
    Elle commence par créer le groupe The Beatles ainsi que ses différents membres.
    Ces artistes sont ajoutés individuellement au catalogue,
    puis le groupe lui-même est ajouté également.
    La fonction appelle ensuite ajouterAlbumAvecMorceaux
    pour créer rapidement plusieurs albums et morceaux liés au groupe.
    Le même principe est appliqué à Michael Jackson, Adele, Daft Punk et Queen.
    Cela permet d’obtenir un catalogue varié contenant des artistes solo et des groupes.
    Les albums créés couvrent plusieurs périodes et plusieurs styles,
    ce qui rend les démonstrations plus intéressantes.
    Toute la logique est placée dans un bloc try/catch
    pour éviter qu’une erreur inattendue stoppe complètement l’initialisation.
    Si une exception survient, un message d’erreur est affiché sur la sortie d’erreur.
    Cette fonction joue donc un rôle important pour les tests,
    les démonstrations en soutenance,
    et les premières exécutions du programme.*/

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
    /* Cette fonction ajouterAlbumAvecMorceaux est une méthode utilitaire
     destinée à simplifier la création d’un album et de tous ses morceaux.
     Elle reçoit en paramètre le catalogue à enrichir,
     le titre de l’album, son année, son auteur,
     ainsi que deux tableaux parallèles contenant les titres et durées des morceaux.
     Elle commence par créer l’objet Album correspondant.
     Cet album est ensuite ajouté au catalogue général.
     La fonction parcourt ensuite tous les morceaux grâce à une boucle for.
     À chaque itération, elle crée un nouveau morceau à partir du titre et de la durée correspondants.
     Le morceau est d’abord ajouté au catalogue,
     puis associé à l’album avec la méthode ajouterMorceau.
     Cette méthode évite d’écrire plusieurs fois le même bloc de création
     dans la fonction d’initialisation des données de démonstration.
     Elle améliore donc la lisibilité et la réutilisabilité du code.
     Les exceptions liées aux doublons sont capturées et ignorées
     afin de ne pas interrompre l’ajout global des données.
     Cette fonction contribue donc à factoriser le code
     et à garantir la cohérence entre le catalogue et les albums.*/

    
    /** Résout un auteur (artiste ou groupe) à partir de son nom. */
    private static AuteurMusical resoudreAuteur(String nom,
                                                HashMap<String, Artiste> artistesMap,
                                                HashMap<String, Groupe> groupesMap) {
        String key = nom.toLowerCase();
        if (groupesMap.containsKey(key))  return groupesMap.get(key);
        if (artistesMap.containsKey(key)) return artistesMap.get(key);
        return null;
    }
    /* Cette fonction resoudreAuteur permet de retrouver un auteur musical
     à partir d’un simple nom sous forme de chaîne de caractères.
     L’auteur recherché peut être soit un groupe, soit un artiste individuel.
     La fonction commence par convertir le nom en minuscules
     afin d’effectuer une recherche insensible à la casse.
     Elle consulte d’abord la map des groupes.
     Si un groupe correspondant existe, il est immédiatement retourné.
     Sinon, elle consulte la map des artistes.
     Si un artiste portant ce nom existe, il est retourné à son tour.
     Si aucune correspondance n’est trouvée dans les deux maps,
     la fonction retourne null.
     Cette méthode est essentielle lors du chargement du fichier texte,
     car les albums et les morceaux stockent seulement le nom de leur auteur dans le fichier.
     Elle permet donc de transformer cette information textuelle
     en une vraie référence objet utilisable dans le modèle.
     Elle garantit ainsi la cohérence des liens entre les différentes entités du catalogue.*/
}
