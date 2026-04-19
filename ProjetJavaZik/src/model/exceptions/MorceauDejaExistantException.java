package model.exceptions;

/** Exception levée lors d'une tentative d'ajout d'un morceau déjà présent. */
public class MorceauDejaExistantException extends Exception {
    private static final long serialVersionUID = 1L;
    public MorceauDejaExistantException(String message) { super(message); }
}
