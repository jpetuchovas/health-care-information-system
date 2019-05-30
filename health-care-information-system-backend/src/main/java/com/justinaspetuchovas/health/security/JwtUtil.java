package com.justinaspetuchovas.health.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.justinaspetuchovas.health.common.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class used to generate, validate and refresh JSON web tokens.
 */
@Component
public class JwtUtil {
  private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
  private static final String JWT_HEADER_START = "Bearer ";

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration.time.in.milliseconds}")
  private int expirationTimeInMilliseconds;

  @Value("${jwt.header}")
  private String header;

  private final TimeProvider timeProvider;

  @Autowired
  public JwtUtil(TimeProvider timeProvider) {
    this.timeProvider = timeProvider;
  }

  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parser()
        .setSigningKey(secret)
        .parseClaimsJws(token)
        .getBody();
  }

  public String getUsernameFromToken(String token) {
    return getAllClaimsFromToken(token).getSubject();
  }

  public Date getIssuedAtDateFromToken(String token) {
    return getAllClaimsFromToken(token).getIssuedAt();
  }

  public Date getExpirationDateFromToken(String token) {
    return getAllClaimsFromToken(token).getExpiration();
  }

  public Date calculateNewExpirationDate(Date tokenCreationDate) {
    return new Date(tokenCreationDate.getTime() + expirationTimeInMilliseconds);
  }

  public String generateToken(JwtUser user) {
    Date tokenCreationDate = timeProvider.now();
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getRole().name());
    claims.put("name", user.getName());
    claims.put("userId", user.getId());
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getUsername())
        .setIssuedAt(tokenCreationDate)
        .setExpiration(calculateNewExpirationDate(tokenCreationDate))
        .signWith(SIGNATURE_ALGORITHM, secret)
        .compact();
  }

  public String getTokenFromRequestHeader(String requestHeader) {
    if (requestHeader != null && requestHeader.startsWith(JWT_HEADER_START)) {
      return requestHeader.substring(JWT_HEADER_START.length());
    } else {
      return null;
    }
  }

  public String getTokenFromRequest(HttpServletRequest request) {
    return getTokenFromRequestHeader(request.getHeader(header));
  }

  private boolean isTokenCreatedAfterLastPasswordReset(
      Date tokenCreationDate,
      Date lastPasswordResetDate
  ) {
    return !tokenCreationDate.before(lastPasswordResetDate);
  }

  private boolean isTokenUnexpired(String token) {
    return !getExpirationDateFromToken(token).before(timeProvider.now());
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    JwtUser user = (JwtUser) userDetails;
    Claims claims = getAllClaimsFromToken(token);
    String username = claims.getSubject();
    Date tokenCreationDate = claims.getIssuedAt();
    Date lastPasswordResetDate = user.getLastPasswordResetDate();
    return username.equals(user.getUsername())
        && isTokenUnexpired(token)
        && isTokenCreatedAfterLastPasswordReset(tokenCreationDate, lastPasswordResetDate);
  }

  public boolean canTokenBeRefreshed(String token, Date lastPasswordResetDate) {
    Date tokenCreationDate = getIssuedAtDateFromToken(token);
    return isTokenCreatedAfterLastPasswordReset(tokenCreationDate, lastPasswordResetDate)
        && isTokenUnexpired(token);
  }

  public String refreshToken(String token) {
    Date tokenCreationDate = timeProvider.now();
    Claims claims = getAllClaimsFromToken(token);
    claims.setIssuedAt(tokenCreationDate);
    claims.setExpiration(calculateNewExpirationDate(tokenCreationDate));
    return Jwts.builder()
        .setClaims(claims)
        .signWith(SIGNATURE_ALGORITHM, secret)
        .compact();
  }
}
