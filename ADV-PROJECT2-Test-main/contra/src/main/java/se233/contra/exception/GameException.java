package se233.contra.exception;

public class GameException extends RuntimeException {
    private final ErrorType errorType;

    public enum ErrorType {
        SPRITE_LOAD_ERROR,
        SOUND_LOAD_ERROR,
        INVALID_GAME_STATE,
        COLLISION_ERROR,
        RESOURCE_NOT_FOUND
    }

    public GameException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public GameException(String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return String.format("GameException[%s]: %s", errorType, getMessage());
    }
}