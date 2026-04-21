package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Représente un album musical.
 * Un album est associé à un {@link AuteurMusical} et contient une liste de {@link Morceau}.
 */
public class Album implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String titre;
    private int annee;
    private AuteurMusical auteur;
    private ArrayList<Morceau> morceaux;

    /**
     * @param titre  titre de l'album
     * @param annee  année de sortie
     * @param auteur artiste ou groupe auteur de l'album
     */
    public Album(String titre, int annee, AuteurMusical auteur) {
        this.id = UUID.randomUUID().toString();
        this.titre = titre;
        this.annee = annee;
        this.auteur = auteur;
        this.morceaux = new ArrayList<>();
        if (auteur != null) auteur.ajouterAlbum(this);
    }

    public String getId() { return id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }
    public AuteurMusical getAuteur() { return auteur; }
    public ArrayList<Morceau> getMorceaux() { return morceaux; }

    /**
     * Ajoute un morceau à l'album (et met à jour le lien inverse sur le morceau).
     */
    public void ajouterMorceau(Morceau morceau) {
        if (!morceaux.contains(morceau)) {
            morceaux.add(morceau);
            morceau.ajouterDansAlbum(this);
        }
    }

    /**
     * Retire un morceau de l'album (et met à jour le lien inverse).
     */
    public void retirerMorceau(Morceau morceau) {
        if (morceaux.remove(morceau)) {
            morceau.retirerDeAlbum(this);
        }
    }

    /** @return durée totale de l'album en secondes */
    public int getDureeTotal() {
        return morceaux.stream().mapToInt(Morceau::getDuree).sum();
    }

    /** @return durée totale formatée "mm:ss" */
    public String getDureeTotaleFormatee() {
        int total = getDureeTotal();
        int min = total / 60;
        int sec = total % 60;
        return String.format("%d:%02d", min, sec);
    }

    @Override
    public String toString() {
        return titre + " (" + annee + ") - " + (auteur != null ? auteur.getNom() : "Inconnu");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album)) return false;
        return id.equals(((Album) o).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
