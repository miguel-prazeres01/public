package domain.authentication.jwt;

public interface JwtService {
    String generateSessionToken(String sessionId);
}
