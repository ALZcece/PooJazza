package model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Historique d'écoute d'un {@link Abonne}.
 * Conserve les derniers morceaux écoutés (du plus récent au plus ancien), limité à 100 entrées.
 */
public class HistoriqueEcoute implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int MAX_HISTORIQUE = 100;
    private LinkedList<Morceau> morceaux;

    public HistoriqueEcoute() {
        this.morceaux = new LinkedList<>();
    }

    /**
     * Enregistre l'écoute d'un morceau (ajout en tête de liste).
     */
    public void ajouterEcoute(Morceau m) {
        morceaux.addFirst(m);
        if (morceaux.size() > MAX_HISTORIQUE) morceaux.removeLast();
    }

    /** @return liste des morceaux du plus récent au plus ancien */
    public LinkedList<Morceau> getMorceaux() { return morceaux; }

    public int getTaille() { return morceaux.size(); }

    public void vider() { morceaux.clear(); }
}
