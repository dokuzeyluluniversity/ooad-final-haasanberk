package oop.libapp.token;

public interface ITokenService {
    String generateTokenForUser(String username) throws FailedTokenGenerationException;
    String extractUsernameFromToken(String token);
    Boolean isValidToken(String token);
}
