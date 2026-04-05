package model.exceptions;

/** Exception levée quand un élément recherché est introuvable. */
public class ElementIntrouvableException extends Exception {
    public ElementIntrouvableException(String message) { super(message); }
}
