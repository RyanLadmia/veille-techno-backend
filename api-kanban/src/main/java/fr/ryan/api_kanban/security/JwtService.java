package fr.ryan.api_kanban.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.ryan.api_kanban.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey secretKey;
	private final long expirationMs;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-ms}") long expirationMs) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMs = expirationMs;
	}

	public String createToken(User user) {
		Date now = new Date();
		Date expiresAt = new Date(now.getTime() + expirationMs);
		return Jwts.builder()
			.subject(user.getId().toString())
			.issuedAt(now)
			.expiration(expiresAt)
			.signWith(secretKey)
			.compact();
	}

	public UUID extractUserId(String token) {
		Claims claims = parseClaims(token);
		return UUID.fromString(claims.getSubject());
	}

	public boolean isValid(String token) {
		try {
			Claims claims = parseClaims(token);
			return claims.getExpiration().after(new Date());
		}
		catch (Exception ex) {
			return false;
		}
	}

	public Claims parseClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}
}
