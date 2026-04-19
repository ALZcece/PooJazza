package model.exceptions;

/** Exception levée quand un visiteur dépasse sa limite d'écoutes par session. */
public class LimiteEcoutesAtteinte extends Exception {
    private static final long serialVersionUID = 1L;
    public LimiteEcoutesAtteinte(String message) { super(message); }
}
