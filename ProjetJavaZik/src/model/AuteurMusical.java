package model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe abstraite représentant un auteur musical (artiste solo ou groupe).
 * Sert de base commune à {@link Artiste} et {@link Groupe}.
 */
public abstract class AuteurMusical implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected String nom;
    protected ArrayList<Album> albums;
    protected ArrayList<Morceau> morceaux;

    public AuteurMusical(String id, String nom) {
        this.id = id;
        this.nom = nom;
        this.albums = new ArrayList<>();
        this.morceaux = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public ArrayList<Album> getAlbums() { return albums; }
    public ArrayList<Morceau> getMorceaux() { return morceaux; }

    public void ajouterAlbum(Album album) {
        if (!albums.contains(album)) albums.add(album);
    }

    public void ajouterMorceau(Morceau morceau) {
        if (!morceaux.contains(morceau)) morceaux.add(morceau);
    }

    public void retirerAlbum(Album album) { albums.remove(album); }
    public void retirerMorceau(Morceau morceau) { morceaux.remove(morceau); }

    @Override
    public String toString() { return nom; }
}
