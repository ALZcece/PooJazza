package controller;

import model.*;
import model.exceptions.*;

import java.io.*;
import java.util.ArrayList;

/**
 * Contrôleur pour l'interface graphique JavaZic.
 * Expose des méthodes métier appelées par les composants Swing.
 * Partage les mêmes fichiers de sauvegarde que le contrôleur console.
 */
public class GUIController {

    private Catalogue catalogue;
    private ArrayList<Abonne> abonnes;
    private final Administrateur admin;
    private Utilisateur utilisateurCourant;

    private static final String DATA_DIR       = "data";
    private static final String CATALOGUE_FILE = DATA_DIR + "/catalogue.ser";
    private static final String ABONNES_FILE   = DATA_DIR + "/abonnes.ser";

    public GUIController() {
        this.admin   = new Administrateur("Admin", "Super", "admin", "1234");
        this.abonnes = new ArrayList<>();
        chargerDonnees();
    }

    // =========================================================
    //  SESSION
    // =========================================================

    public Utilisateur getUtilisateurCourant() { return utilisateurCourant; }
    public boolean estAdmin()   { return utilisateurCourant instanceof Administrateur; }
    public boolean estAbonne()  { return utilisateurCourant instanceof Abonne; }
    public boolean estVisiteur(){ return utilisateurCourant instanceof Visiteur && !(utilisateurCourant instanceof Abonne); }

    /** Initialise une session visiteur anonyme. */
    public void demarrerSessionVisiteur() { utilisateurCourant = new Visiteur(); }

    public void deconnecter() { utilisateurCourant = null; }

    // =========================================================
    //  AUTHENTIFICATION
    // =========================================================

    /** @return true si les identifiants admin sont corrects */
    public boolean connecterAdmin(String login, String mdp) {
        if (admin.getLogin().equals(login) && admin.verifierMotDePasse(mdp)) {
            utilisateurCourant = admin;
            return true;
        }
        return false;
    }

    /**
     * Connecte un abonné.
     * @return l'abonné connecté, ou null si identifiants incorrects
     * @throws CompteInactifException si le compte est suspendu
     */
    public Abonne connecterAbonne(String login, String mdp) throws CompteInactifException {
        for (Abonne a : abonnes) {
            if (a.getLogin().equalsIgnoreCase(login) && a.verifierMotDePasse(mdp)) {
                if (!a.isActif()) throw new CompteInactifException("Ce compte est suspendu.");
                utilisateurCourant = a;
                return a;
            }
        }
        return null;
    }

    /**
     * Crée un nouveau compte abonné et ouvre sa session.
     * @throws Exception si le login est déjà utilisé
     */
    public Abonne creerCompte(String nom, String prenom, String login, String mdp) throws Exception {
        for (Abonne a : abonnes) {
            if (a.getLogin().equalsIgnoreCase(login))
                throw new Exception("Le login \"" + login + "\" est déjà utilisé.");
        }
        Abonne nouvel = new Abonne(nom, prenom, login, mdp);
        abonnes.add(nouvel);
        utilisateurCourant = nouvel;
        return nouvel;
    }

    // =========================================================
    //  CATALOGUE
    // =========================================================

    public Catalogue getCatalogue() { return catalogue; }

    // =========================================================
    //  LECTURE
    // =========================================================

    /** @return true si l'utilisateur courant peut encore écouter */
    public boolean peutEcouter() {
        if (utilisateurCourant instanceof Visiteur)
            return ((Visiteur) utilisateurCourant).peutEcouter();
        return true;
    }

    /** @return écoutes restantes pour un visiteur, -1 pour un abonné */
    public int getEcoutesRestantes() {
        if (utilisateurCourant instanceof Visiteur && !(utilisateurCourant instanceof Abonne))
            return Visiteur.MAX_ECOUTES_SESSION - ((Visiteur) utilisateurCourant).getNbEcoutesSession();
        return -1;
    }

    /**
     * Enregistre l'écoute d'un morceau pour l'utilisateur courant.
     * @throws LimiteEcoutesAtteinte si le visiteur a atteint sa limite
     */
    public void ecouter(Morceau m) throws LimiteEcoutesAtteinte {
        if (utilisateurCourant instanceof Visiteur)
            ((Visiteur) utilisateurCourant).incrementerEcoutes();
        m.incrementerEcoutes();
        catalogue.incrementerEcoutesTotales();
        if (utilisateurCourant instanceof Abonne)
            ((Abonne) utilisateurCourant).getHistorique().ajouterEcoute(m);
    }

    // =========================================================
    //  AVIS
    // =========================================================

    /** Ajoute ou remplace l'avis de l'abonné courant sur un morceau. */
    public void ajouterAvis(Morceau m, int note, String commentaire) {
        if (!(utilisateurCourant instanceof Abonne)) return;
        m.ajouterAvis(new Avis((Abonne) utilisateurCourant, note, commentaire));
    }

    /** Supprime l'avis de l'abonné courant sur un morceau. */
    public void supprimerAvis(Morceau m) {
        if (!(utilisateurCourant instanceof Abonne)) return;
        m.supprimerAvis((Abonne) utilisateurCourant);
    }

    // =========================================================
    //  PLAYLISTS
    // =========================================================

    /** Crée une playlist pour l'abonné courant. */
    public Playlist creerPlaylist(String nom) {
        if (!(utilisateurCourant instanceof Abonne)) return null;
        return ((Abonne) utilisateurCourant).creerPlaylist(nom);
    }

    public void supprimerPlaylist(Playlist p) throws ElementIntrouvableException {
        if (!(utilisateurCourant instanceof Abonne)) return;
        ((Abonne) utilisateurCourant).supprimerPlaylist(p);
    }

    public void ajouterMorceauPlaylist(Playlist p, Morceau m) throws MorceauDejaExistantException {
        p.ajouterMorceau(m);
    }

    public void retirerMorceauPlaylist(Playlist p, Morceau m) throws ElementIntrouvableException {
        p.retirerMorceau(m);
    }

    // =========================================================
    //  ADMIN — CATALOGUE
    // =========================================================

    public Artiste ajouterArtiste(String nom, String bio) {
        Artiste a = new Artiste(nom, bio);
        catalogue.ajouterArtiste(a);
        return a;
    }

    public Groupe ajouterGroupe(String nom) {
        Groupe g = new Groupe(nom);
        catalogue.ajouterGroupe(g);
        return g;
    }

    public Album ajouterAlbum(String titre, int annee, AuteurMusical auteur) {
        Album a = new Album(titre, annee, auteur);
        catalogue.ajouterAlbum(a);
        return a;
    }

    public Morceau ajouterMorceau(String titre, int duree, AuteurMusical auteur)
            throws MorceauDejaExistantException {
        Morceau m = new Morceau(titre, duree, auteur);
        catalogue.ajouterMorceau(m);
        return m;
    }

    public void supprimerMorceau(Morceau m) throws ElementIntrouvableException {
        catalogue.supprimerMorceau(m);
    }

    public void supprimerAlbum(Album a) throws ElementIntrouvableException {
        catalogue.supprimerAlbum(a);
    }

    public void supprimerArtiste(Artiste a) throws ElementIntrouvableException {
        catalogue.supprimerArtiste(a);
    }

    public void supprimerGroupe(Groupe g) throws ElementIntrouvableException {
        catalogue.supprimerGroupe(g);
    }

    // =========================================================
    //  ADMIN — ABONNÉS
    // =========================================================

    public ArrayList<Abonne> getAbonnes() { return abonnes; }

    public void toggleSuspension(Abonne a) { a.setActif(!a.isActif()); }

    public void supprimerAbonne(Abonne a) { abonnes.remove(a); }

    // =========================================================
    //  PERSISTANCE
    // =========================================================

    /** Sauvegarde le catalogue et les abonnés (appelé à la fermeture). */
    public void sauvegarder() {
        new File(DATA_DIR).mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CATALOGUE_FILE))) {
            oos.writeObject(catalogue);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde catalogue : " + e.getMessage());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ABONNES_FILE))) {
            oos.writeObject(abonnes);
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde abonnés : " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void chargerDonnees() {
        new File(DATA_DIR).mkdirs();
        // Catalogue
        File fc = new File(CATALOGUE_FILE);
        if (fc.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fc))) {
                catalogue = (Catalogue) ois.readObject();
            } catch (Exception e) {
                catalogue = new Catalogue();
                chargerCatalogueTxt();
            }
        } else {
            catalogue = new Catalogue();
            chargerCatalogueTxt();
        }
        // Abonnés
        File fa = new File(ABONNES_FILE);
        if (fa.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fa))) {
                abonnes = (ArrayList<Abonne>) ois.readObject();
            } catch (Exception e) {
                abonnes = new ArrayList<>();
            }
        }
    }

    private void chargerCatalogueTxt() {
        if (!DataLoader.chargerCatalogueDepuisTxt(catalogue)) {
            DataLoader.initialiserDonneesDemo(catalogue);
        }
    }
}
