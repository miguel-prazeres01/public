package infrastructure.authentication.jwt;

import domain.authentication.jwt.JwtService;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtServiceImpl implements JwtService{
    public String generateSessionToken(String sessionId) {
        return Jwt.issuer("ISSUER")
                  .upn(sessionId)          // use the session ID as the principal
                  .audience("payment")
                  .expiresIn(10)         // minutes
                  .sign();
    }
}
