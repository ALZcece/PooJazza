package model.exceptions;

/** Exception levée lors d'une tentative d'ajout d'un morceau déjà présent. */
public class MorceauDejaExistantException extends Exception {
    public MorceauDejaExistantException(String message) { super(message); }
}
