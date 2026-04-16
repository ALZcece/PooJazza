package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Représente un morceau de musique du catalogue.
 * Un morceau appartient à un {@link AuteurMusical} et peut figurer dans plusieurs {@link Album}.
 */
public class Morceau implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String titre;
    private int duree; // en secondes
    private AuteurMusical auteur;
    private ArrayList<Album> albums;
    private int nbEcoutes;
    private ArrayList<Avis> avis;

    /**
     * @param titre  titre du morceau
     * @param duree  durée en secondes
     * @param auteur artiste ou groupe auteur du morceau
     */
    public Morceau(String titre, int duree, AuteurMusical auteur) {
        this.id = UUID.randomUUID().toString();
        this.titre = titre;
        this.duree = duree;
        this.auteur = auteur;
        this.albums = new ArrayList<>();
        this.nbEcoutes = 0;
        this.avis = new ArrayList<>();
        if (auteur != null) auteur.ajouterMorceau(this);
    }

    public String getId() { return id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public int getDuree() { return duree; }
    public AuteurMusical getAuteur() { return auteur; }
    public ArrayList<Album> getAlbums() { return albums; }
    public int getNbEcoutes() { return nbEcoutes; }
    public ArrayList<Avis> getAvis() { return avis; }

    public void incrementerEcoutes() { nbEcoutes++; }

    public void ajouterDansAlbum(Album album) {
        if (!albums.contains(album)) albums.add(album);
    }

    public void retirerDeAlbum(Album album) { albums.remove(album); }

    /**
     * Ajoute ou remplace l'avis d'un abonné sur ce morceau (un seul avis par abonné).
     */
    public void ajouterAvis(Avis nouvelAvis) {
        avis.removeIf(a -> a.getAuteur().equals(nouvelAvis.getAuteur()));
        avis.add(nouvelAvis);
    }

    /**
     * Supprime l'avis d'un abonné sur ce morceau.
     */
    public void supprimerAvis(Abonne abonne) {
        avis.removeIf(a -> a.getAuteur().equals(abonne));
    }

    /** @return note moyenne (0.0 si aucun avis) */
    public double getNoteMoyenne() {
        if (avis.isEmpty()) return 0.0;
        return avis.stream().mapToInt(Avis::getNote).average().orElse(0.0);
    }

    /** @return durée formatée "mm:ss" */
    public String getDureeFormatee() {
        int min = duree / 60;
        int sec = duree % 60;
        return String.format("%d:%02d", min, sec);
    }

    @Override
    public String toString() {
        return titre + " - " + (auteur != null ? auteur.getNom() : "Inconnu") + " (" + getDureeFormatee() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Morceau)) return false;
        return id.equals(((Morceau) o).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
