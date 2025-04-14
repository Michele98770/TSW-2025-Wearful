package model;

public class EmptyPoolException extends RuntimeException{
    public EmptyPoolException(String message) {
        super(message);
    }
}