package model;

import model.exceptions.ElementIntrouvableException;
import model.exceptions.MorceauDejaExistantException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Représente une playlist appartenant à un {@link Abonne}.
 * Supporte la collaboration : le propriétaire peut partager la playlist
 * avec d'autres abonnés en leur attribuant des droits de lecture seule
 * ou de modification (ajout/suppression de morceaux).
 */
public class Playlist implements Serializable {
    private static final long serialVersionUID = 2L;

    private String id;
    private String nom;
    private Abonne proprietaire;
    private ArrayList<Morceau> morceaux;

    /** Collaborateurs : clé = abonné, valeur = true si peut modifier, false si lecture seule. */
    private HashMap<Abonne, Boolean> collaborateurs;

    /**
     * @param nom          nom de la playlist
     * @param proprietaire abonne proprietaire de la playlist
     */
    public Playlist(String nom, Abonne proprietaire) {
        this.id = UUID.randomUUID().toString();
        this.nom = nom;
        this.proprietaire = proprietaire;
        this.morceaux = new ArrayList<>();
        this.collaborateurs = new HashMap<>();
    }

    // --- Getters / Setters ---

    public String getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public Abonne getProprietaire() { return proprietaire; }
    public ArrayList<Morceau> getMorceaux() { return morceaux; }
    public HashMap<Abonne, Boolean> getCollaborateurs() {
        if (collaborateurs == null) collaborateurs = new HashMap<>();
        return collaborateurs;
    }

    // --- Gestion des morceaux ---

    /**
     * Ajoute un morceau a la playlist.
     * @throws MorceauDejaExistantException si le morceau est deja present
     */
    public void ajouterMorceau(Morceau m) throws MorceauDejaExistantException {
        if (morceaux.contains(m))
            throw new MorceauDejaExistantException(
                    "'" + m.getTitre() + "' est deja dans la playlist '" + nom + "'.");
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

    // --- Collaboration ---

    /**
     * Ajoute un collaborateur a la playlist.
     * @param abonne       l'abonne a ajouter comme collaborateur
     * @param peutModifier true = droits d'edition (ajout/suppression de morceaux), false = lecture seule
     */
    public void ajouterCollaborateur(Abonne abonne, boolean peutModifier) {
        if (abonne.equals(proprietaire)) return;
        getCollaborateurs().put(abonne, peutModifier);
    }

    /**
     * Retire un collaborateur de la playlist.
     * @param abonne l'abonne a retirer
     */
    public void retirerCollaborateur(Abonne abonne) {
        getCollaborateurs().remove(abonne);
    }

    /**
     * Verifie si un abonne est collaborateur de cette playlist.
     */
    public boolean estCollaborateur(Abonne abonne) {
        return getCollaborateurs().containsKey(abonne);
    }

    /**
     * Verifie si un collaborateur a les droits de modification.
     * @return true si l'abonne peut modifier, false sinon (y compris s'il n'est pas collaborateur)
     */
    public boolean peutModifier(Abonne abonne) {
        if (abonne.equals(proprietaire)) return true;
        Boolean droit = getCollaborateurs().get(abonne);
        return droit != null && droit;
    }

    /**
     * Verifie si un abonne a acces a cette playlist (proprietaire ou collaborateur).
     */
    public boolean aAcces(Abonne abonne) {
        return abonne.equals(proprietaire) || estCollaborateur(abonne);
    }

    // --- Utilitaires ---

    /** @return duree totale de la playlist en secondes */
    public int getDureeTotal() {
        return morceaux.stream().mapToInt(Morceau::getDuree).sum();
    }

    /** @return duree totale formatee "mm:ss" */
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
