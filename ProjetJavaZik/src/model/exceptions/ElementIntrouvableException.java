package model.exceptions;

/** Exception levée quand un élément recherché est introuvable. */
public class ElementIntrouvableException extends Exception {
    private static final long serialVersionUID = 1L;
    public ElementIntrouvableException(String message) { super(message); }
}
