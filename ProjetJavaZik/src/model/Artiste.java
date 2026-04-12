package model;

import java.util.UUID;

/**
 * Représente un artiste musical solo.
 * Un artiste peut appartenir à un {@link Groupe}.
 */
public class Artiste extends AuteurMusical {
    private static final long serialVersionUID = 1L;

    private String biographie;
    private Groupe groupe;

    public Artiste(String nom) {
        super(UUID.randomUUID().toString(), nom);
        this.biographie = "";
        this.groupe = null;
    }

    public Artiste(String nom, String biographie) {
        this(nom);
        this.biographie = biographie;
    }

    public String getBiographie() { return biographie; }
    public void setBiographie(String biographie) { this.biographie = biographie; }
    public Groupe getGroupe() { return groupe; }
    public void setGroupe(Groupe groupe) { this.groupe = groupe; }

    @Override
    public String toString() {
        return nom + (groupe != null ? " (membre de " + groupe.getNom() + ")" : "");
    }
}
