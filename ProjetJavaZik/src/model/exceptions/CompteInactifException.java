package model.exceptions;

/** Exception levée quand un abonné suspendu tente une action réservée aux comptes actifs. */
public class CompteInactifException extends Exception {
    public CompteInactifException(String message) { super(message); }
}
