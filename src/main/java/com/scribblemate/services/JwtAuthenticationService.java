package com.scribblemate.services;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtAuthenticationService {
	
	
	@Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-expiration-time}")
    private long jwtExpiration;
    
    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpiration; 
	
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = extractAllClaims(token);
	        return claimsResolver.apply(claims);
	    }

	  private Claims extractAllClaims(String token) {
	        return Jwts
	                .parserBuilder()
	                .setSigningKey(getSignInKey())
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	    }

	  private Key getSignInKey() {
	        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
	  
	    public String generateToken(UserDetails userDetails) {
	        return generateToken(new HashMap<>(), userDetails);
	    }

	    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
	        return buildToken(extraClaims, userDetails, jwtExpiration);
	    }

	    public long getExpirationTime() {
	        return jwtExpiration;
	    }

	    private String buildToken(
	            Map<String, Object> extraClaims,
	            UserDetails userDetails,
	            long expiration
	    ) {
	        return Jwts
	                .builder()
	                .setClaims(extraClaims)
	                .setSubject(userDetails.getUsername())
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + expiration))
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
	    
	    
	    /**
	     * referesh token generation methods
	     * 
	     */
	    
	    public String generateRefreshToken(UserDetails userDetails) {
	        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
	    }

	    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
	        final String username = extractUsername(token);
	        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
	    }

	    // You might want to add a method to check if a token is a refresh token
	    public boolean isRefreshToken(String token) {
	        final Claims claims = extractAllClaims(token);
	        return claims.getExpiration().getTime() - claims.getIssuedAt().getTime() == refreshExpiration;
	    }

}
