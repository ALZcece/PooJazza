package model;

import model.exceptions.ElementIntrouvableException;
import model.exceptions.LimiteEcoutesAtteinte;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Représente un abonné : utilisateur connecté avec un compte persistant.
 * Hérite de {@link Visiteur} mais dispose d'écoutes illimitées, de playlists et d'un historique.
 */
public class Abonne extends Visiteur {
    private static final long serialVersionUID = 1L;

    private String id;
    private String login;
    private String motDePasse;
    private ArrayList<Playlist> playlists;
    private HistoriqueEcoute historique;
    private boolean actif;

    /**
     * @param nom        nom de famille de l'abonné
     * @param prenom     prénom de l'abonné
     * @param login      identifiant de connexion unique
     * @param motDePasse mot de passe (stocké en clair ici ; à hasher en production)
     */
    public Abonne(String nom, String prenom, String login, String motDePasse) {
        super();
        this.nom = nom;
        this.prenom = prenom;
        this.id = UUID.randomUUID().toString();
        this.login = login;
        this.motDePasse = motDePasse;
        this.playlists = new ArrayList<>();
        this.historique = new HistoriqueEcoute();
        this.actif = true;
    }

    public String getId() { return id; }
    public String getLogin() { return login; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public ArrayList<Playlist> getPlaylists() { return playlists; }
    public HistoriqueEcoute getHistorique() { return historique; }
    public boolean isActif() { return actif; }
    public void setActif(boolean actif) { this.actif = actif; }

    /** @return true si le mot de passe fourni correspond */
    public boolean verifierMotDePasse(String mdp) { return motDePasse.equals(mdp); }

    /** Un abonné actif peut toujours écouter (pas de limite de session). */
    @Override
    public boolean peutEcouter() { return actif; }

    /** Pour un abonné, seul le statut actif est vérifié (pas de compteur de session). */
    @Override
    public void incrementerEcoutes() throws LimiteEcoutesAtteinte {
        if (!actif)
            throw new LimiteEcoutesAtteinte("Votre compte est suspendu. Contactez un administrateur.");
    }

    /**
     * Crée une nouvelle playlist et l'ajoute à la liste de l'abonné.
     * @param nom nom de la playlist
     * @return la playlist créée
     */
    public Playlist creerPlaylist(String nom) {
        Playlist p = new Playlist(nom, this);
        playlists.add(p);
        return p;
    }

    /**
     * Supprime une playlist de l'abonné.
     * @throws ElementIntrouvableException si la playlist n'appartient pas à cet abonné
     */
    public void supprimerPlaylist(Playlist p) throws ElementIntrouvableException {
        if (!playlists.remove(p))
            throw new ElementIntrouvableException("Playlist introuvable.");
    }

    /**
     * Recherche une playlist par son nom (insensible à la casse).
     * @throws ElementIntrouvableException si aucune playlist ne correspond
     */
    public Playlist getPlaylistParNom(String nom) throws ElementIntrouvableException {
        return playlists.stream()
                .filter(p -> p.getNom().equalsIgnoreCase(nom))
                .findFirst()
                .orElseThrow(() -> new ElementIntrouvableException("Aucune playlist nommée \"" + nom + "\"."));
    }

    @Override
    public String toString() {
        return login + " (" + getNomComplet() + ")" + (actif ? "" : " [SUSPENDU]");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Abonne)) return false;
        return id.equals(((Abonne) o).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }
}
