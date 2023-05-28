package by.karpovich.SocialMedia.exception;

public class NotFoundModelException extends RuntimeException {
    public NotFoundModelException() {
    }

    public NotFoundModelException(String message) {
        super(message);
    }
}