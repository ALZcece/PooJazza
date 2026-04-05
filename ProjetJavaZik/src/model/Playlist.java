package model;

import model.exceptions.ElementIntrouvableException;
import model.exceptions.MorceauDejaExistantException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Représente une playlist appartenant à un {@link Abonne}.
 * Contient une liste ordonnée de {@link Morceau}.
 */
public class Playlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nom;
    private Abonne proprietaire;
    private ArrayList<Morceau> morceaux;

    /**
     * @param nom          nom de la playlist
     * @param proprietaire abonné propriétaire de la playlist
     */
    public Playlist(String nom, Abonne proprietaire) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.proprietaire = proprietaire;
        this.morceaux = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Abonne getProprietaire() { return proprietaire; }
    public ArrayList<Morceau> getMorceaux() { return morceaux; }

    /**
     * Ajoute un morceau à la playlist.
     * @throws MorceauDejaExistantException si le morceau est déjà présent
     */
    public void ajouterMorceau(Morceau m) throws MorceauDejaExistantException {
        if (morceaux.contains(m))
            throw new MorceauDejaExistantException(
                    "'" + m.getTitre() + "' est déjà dans la playlist '" + nom + "'.");
        morceaux.add(m);
    }

    /**
     * Retire un morceau de la playlist.
     * @throws ElementIntrouvableException si le morceau n'est pas dans la playlist
     */
    public void retirerMorceau(Morceau m) throws ElementIntrouvableException {
        if (!morceaux.remove(m))
            throw new ElementIntrouvableException(
                    "'" + m.getTitre() + "' n'est pas dans la playlist '" + nom + "'.");
    }

    /** @return durée totale de la playlist en secondes */
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
        return "\"" + nom + "\" — " + morceaux.size() + " morceau(x), " + getDureeTotaleFormatee();
    }
}
