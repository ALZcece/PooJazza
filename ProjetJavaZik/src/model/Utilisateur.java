package model;

import java.io.Serializable;

/**
 * Classe abstraite représentant un utilisateur du système JavaZic.
 * Base commune à {@link Visiteur}, {@link Abonne} et {@link Administrateur}.
 */
public abstract class Utilisateur implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String nom;
    protected String prenom;

    public Utilisateur(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    /** @return prénom + nom concaténés */
    public String getNomComplet() { return prenom + " " + nom; }

    @Override
    public String toString() { return getNomComplet(); }
}
