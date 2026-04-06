package model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Représente un groupe musical composé de plusieurs {@link Artiste}.
 */
public class Groupe extends AuteurMusical {
    private static final long serialVersionUID = 1L;

    private ArrayList<Artiste> membres;

    public Groupe(String nom) {
        super(UUID.randomUUID().toString(), nom);
        this.membres = new ArrayList<>();
    }

    public ArrayList<Artiste> getMembres() { return membres; }

    /**
     * Ajoute un artiste au groupe et met à jour le lien inverse.
     */
    public void ajouterMembre(Artiste artiste) {
        if (!membres.contains(artiste)) {
            membres.add(artiste);
            artiste.setGroupe(this);
        }
    }

    /**
     * Retire un artiste du groupe et met à jour le lien inverse.
     */
    public void retirerMembre(Artiste artiste) {
        if (membres.remove(artiste)) {
            artiste.setGroupe(null);
        }
    }

    @Override
    public String toString() {
        return nom + " (" + membres.size() + " membre(s))";
    }
}
