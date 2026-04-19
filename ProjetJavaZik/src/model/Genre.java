package model;

/**
 * Genres musicaux disponibles dans le catalogue.
 * Utilisés pour catégoriser les morceaux et permettre la recherche par genre.
 */
public enum Genre {
    ROCK("Rock"),
    POP("Pop"),
    JAZZ("Jazz"),
    CLASSIQUE("Classique"),
    HIP_HOP("Hip-Hop"),
    RAP("Rap"),
    ELECTRO("Electro"),
    METAL("Metal"),
    COUNTRY("Country"),
    BLUES("Blues"),
    FOLK("Folk"),
    REGGAE("Reggae"),
    RNB("R&B"),
    SOUL("Soul"),
    INCONNU("Inconnu");

    private final String label;

    Genre(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    /**
     * Convertit une chaîne de caractères en Genre, insensible à la casse.
     * @param s chaîne à convertir (ex: "rock", "Pop", "HIP_HOP")
     * @return le Genre correspondant, ou INCONNU si non reconnu
     */
    public static Genre fromString(String s) {
        if (s == null || s.trim().isEmpty()) return INCONNU;
        String norm = s.trim().toUpperCase().replace("-", "_").replace(" ", "_").replace("&", "");
        for (Genre g : values()) {
            if (g.name().equals(norm) || g.label.equalsIgnoreCase(s.trim())) {
                return g;
            }
        }
        return INCONNU;
    }
}
