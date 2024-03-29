package oop.libapp.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import oop.libapp.exception.ResourceNotFoundException;
import oop.libapp.register.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class TokenService implements ITokenService {

    private IUserService userService;

    @Autowired
    public TokenService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public String generateTokenForUser(String username) throws FailedTokenGenerationException {
        String secret;
        Algorithm algorithm;
        String generatedToken;

        try {
            secret = userService.findSecretByUsername(username);
        } catch (ResourceNotFoundException ex) {
            // we do not have the secret of a specific user, we cannot proceed
            throw new FailedTokenGenerationException("Token could not be generated, because the secret was null");
        }

        LocalDateTime threeDaysFromNow = LocalDateTime.now().plusDays(1);
        Date expiresAt = Date.from(threeDaysFromNow.atZone(ZoneId.systemDefault()).toInstant());

        algorithm = Algorithm.HMAC512(secret);
        generatedToken = JWT
                .create()
                .withClaim("username", username)
                .withIssuer("library-app")
                .withExpiresAt(expiresAt)
                .sign(algorithm);

        return generatedToken;
    }

    private String removePrefixIfExists(String token) {
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        } else {
            return token;
        }
    }

    @Override
    public String extractUsernameFromToken(String token) {
        String extractedUsername;
        String tokenWithoutPrefix = removePrefixIfExists(token);
        DecodedJWT jwt;

        try {
            jwt = JWT.decode(tokenWithoutPrefix);
            extractedUsername = jwt.getClaim("username").asString();
        } catch (JWTDecodeException ex) {
            extractedUsername = null;
        }
        return extractedUsername;
    }

    @Override
    public Boolean isValidToken(String token) {
        String tokenWithoutPrefix;
        String username;
        String userSecret;

        tokenWithoutPrefix = removePrefixIfExists(token);
        username = extractUsernameFromToken(token);

        try {
            userSecret = userService.findSecretByUsername(username);
        } catch (ResourceNotFoundException ex) {
            // token cannot be valid if the secret is null
            return false;
        }

        try {
            Algorithm algorithm = Algorithm.HMAC512(userSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .withIssuer("library-app")
                    .build();
            DecodedJWT jwt = verifier.verify(tokenWithoutPrefix);
        } catch (JWTVerificationException exception){
            return false;
        }

        return true;
    }
}
