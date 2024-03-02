package lt.viko.eif.pi21e.Utility;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.hibernate.annotations.DialectOverride;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET_KEY = "Ij+tSYXXAKNVAmU4OnkgmLRwd+CIJN6EYI6nw8gp/MY9dKLBDuHcSfDONIzarVsGko+dFt6kj3X/pJz53igefbrQFO+MZeI4a33EsnOB/bHWcFwwupJ8GJtnhCq3uwwZrkk9kIU3puperbAEWc2mq9vOkdx1JWay8ndEDWW0H+YwzZJcloNTAa8LgZ7ayknShkRu/MRV0Ebmwh7YgZEv4ZYwFDSUXVdIRr9uIGXZsiIfofKlBRVJ14axIEOzc+rLUMDOEENaD7F9TibW6C7a1A+J0WjCuMChFgY+V7kn4PPjdsgDxCW883K+by6rQQfomfi/t9qmteMfPvQnN8FMHiuWyVdSGTsDvT8Cy3noLDk=";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // TODO: Token should last 100 days but there might be some bug that is causing it to expire faster than expected.
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * 100))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
