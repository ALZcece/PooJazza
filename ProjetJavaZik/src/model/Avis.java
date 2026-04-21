package model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Représente un avis (note + commentaire) laissé par un {@link Abonne} sur un {@link Morceau}.
 * Fonctionnalité supplémentaire : système de notation.
 */
public class Avis implements Serializable {
    private static final long serialVersionUID = 1L;

    private Abonne auteur;
    private int note; // entre 1 et 5
    private String commentaire;
    private LocalDate date;

    /**
     * @param auteur      l'abonné qui laisse l'avis
     * @param note        note de 1 à 5
     * @param commentaire texte de l'avis
     * @throws IllegalArgumentException si la note est hors de [1, 5]
     */
    public Avis(Abonne auteur, int note, String commentaire) {
        if (note < 1 || note > 5) throw new IllegalArgumentException("La note doit être entre 1 et 5.");
        this.auteur = auteur;
        this.note = note;
        this.commentaire = commentaire;
        this.date = LocalDate.now();
    }

    public Abonne getAuteur() { return auteur; }
    public int getNote() { return note; }

    public void setNote(int note) {
        if (note < 1 || note > 5) throw new IllegalArgumentException("La note doit être entre 1 et 5.");
        this.note = note;
    }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
    public LocalDate getDate() { return date; }

    @Override
    public String toString() {
        return auteur.getLogin() + " - " + note + "/5 : \"" + commentaire + "\" (" + date + ")";
    }
}
