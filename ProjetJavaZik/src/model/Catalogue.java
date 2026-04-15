package model;

import model.exceptions.ElementIntrouvableException;
import model.exceptions.MorceauDejaExistantException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Catalogue musical central contenant tous les morceaux, albums, artistes et groupes.
 * Point d'entrée principal pour la gestion et la recherche du contenu musical.
 */
public class Catalogue implements Serializable {
    private static final long serialVersionUID = 1L;

    private ArrayList<Morceau> morceaux;
    private ArrayList<Album> albums;
    private ArrayList<Artiste> artistes;
    private ArrayList<Groupe> groupes;
    private int nbEcoutesTotales;

    public Catalogue() {
        this.morceaux = new ArrayList<>();
        this.albums = new ArrayList<>();
        this.artistes = new ArrayList<>();
        this.groupes = new ArrayList<>();
        this.nbEcoutesTotales = 0;
    }

    // --- Getters ---
    public ArrayList<Morceau> getMorceaux() { return morceaux; }
    public ArrayList<Album> getAlbums() { return albums; }
    public ArrayList<Artiste> getArtistes() { return artistes; }
    public ArrayList<Groupe> getGroupes() { return groupes; }
    public int getNbEcoutesTotales() { return nbEcoutesTotales; }
    public void incrementerEcoutesTotales() { nbEcoutesTotales++; }

    // --- Ajout ---

    /**
     * Ajoute un morceau au catalogue.
     * @throws MorceauDejaExistantException si un morceau du même titre et auteur existe déjà
     */
    public void ajouterMorceau(Morceau m) throws MorceauDejaExistantException {
        for (Morceau existing : morceaux) {
            if (existing.getTitre().equalsIgnoreCase(m.getTitre())
                    && existing.getAuteur() == m.getAuteur()) {
                throw new MorceauDejaExistantException(
                        "Le morceau '" + m.getTitre() + "' de cet auteur existe déjà dans le catalogue.");
            }
        }
        morceaux.add(m);
    }

    public void ajouterAlbum(Album a) {
        if (!albums.contains(a)) albums.add(a);
    }

    public void ajouterArtiste(Artiste a) {
        if (!artistes.contains(a)) artistes.add(a);
    }

    public void ajouterGroupe(Groupe g) {
        if (!groupes.contains(g)) groupes.add(g);
    }

    // --- Suppression ---

    /**
     * Supprime un morceau du catalogue.
     * @throws ElementIntrouvableException si le morceau n'est pas dans le catalogue
     */
    public void supprimerMorceau(Morceau m) throws ElementIntrouvableException {
        if (!morceaux.remove(m))
            throw new ElementIntrouvableException("Morceau introuvable dans le catalogue.");
        // Retirer le morceau de tous ses albums
        for (Album a : m.getAlbums()) a.getMorceaux().remove(m);
        m.getAuteur().retirerMorceau(m);
    }

    /**
     * Supprime un album du catalogue (sans supprimer les morceaux associés).
     * @throws ElementIntrouvableException si l'album n'est pas dans le catalogue
     */
    public void supprimerAlbum(Album a) throws ElementIntrouvableException {
        if (!albums.remove(a))
            throw new ElementIntrouvableException("Album introuvable dans le catalogue.");
        for (Morceau m : new ArrayList<>(a.getMorceaux())) m.retirerDeAlbum(a);
        if (a.getAuteur() != null) a.getAuteur().retirerAlbum(a);
    }

    /**
     * Supprime un artiste ainsi que tous ses morceaux et albums.
     * @throws ElementIntrouvableException si l'artiste n'est pas dans le catalogue
     */
    public void supprimerArtiste(Artiste a) throws ElementIntrouvableException {
        if (!artistes.remove(a))
            throw new ElementIntrouvableException("Artiste introuvable dans le catalogue.");
        morceaux.removeIf(m -> m.getAuteur() == a);
        albums.removeIf(alb -> alb.getAuteur() == a);
    }

    /**
     * Supprime un groupe ainsi que tous ses morceaux et albums.
     * @throws ElementIntrouvableException si le groupe n'est pas dans le catalogue
     */
    public void supprimerGroupe(Groupe g) throws ElementIntrouvableException {
        if (!groupes.remove(g))
            throw new ElementIntrouvableException("Groupe introuvable dans le catalogue.");
        morceaux.removeIf(m -> m.getAuteur() == g);
        albums.removeIf(alb -> alb.getAuteur() == g);
    }

    // --- Recherche ---

    public ArrayList<Morceau> rechercherMorceaux(String query) {
        String q = query.toLowerCase();
        return morceaux.stream()
                .filter(m -> m.getTitre().toLowerCase().contains(q))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Recherche tous les morceaux d'un genre donne.
     * @param genre le genre a filtrer
     * @return la liste des morceaux du genre demande
     */
    public ArrayList<Morceau> rechercherMorceauxParGenre(Genre genre) {
        if (genre == null) return new ArrayList<>(morceaux);
        return morceaux.stream()
                .filter(m -> m.getGenre() == genre)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Recherche par titre ET filtre par genre.
     * Si genre est null, on n'applique pas le filtre genre.
     */
    public ArrayList<Morceau> rechercherMorceaux(String query, Genre genre) {
        String q = query == null ? "" : query.toLowerCase();
        return morceaux.stream()
                .filter(m -> m.getTitre().toLowerCase().contains(q))
                .filter(m -> genre == null || m.getGenre() == genre)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Album> rechercherAlbums(String query) {
        String q = query.toLowerCase();
        return albums.stream()
                .filter(a -> a.getTitre().toLowerCase().contains(q))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Artiste> rechercherArtistes(String query) {
        String q = query.toLowerCase();
        return artistes.stream()
                .filter(a -> a.getNom().toLowerCase().contains(q))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Groupe> rechercherGroupes(String query) {
        String q = query.toLowerCase();
        return groupes.stream()
                .filter(g -> g.getNom().toLowerCase().contains(q))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // --- Statistiques ---

    /** @return le morceau le plus écouté, ou null si le catalogue est vide */
    public Morceau getMorceauPlusEcoute() {
        return morceaux.stream()
                .max((a, b) -> Integer.compare(a.getNbEcoutes(), b.getNbEcoutes()))
                .orElse(null);
    }

    /** @return liste des morceaux triés par nombre d'écoutes décroissant */
    public ArrayList<Morceau> getMorceauxParEcoutes() {
        return morceaux.stream()
                .sorted((a, b) -> Integer.compare(b.getNbEcoutes(), a.getNbEcoutes()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
