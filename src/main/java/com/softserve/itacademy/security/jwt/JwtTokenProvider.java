package com.softserve.itacademy.security.jwt;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.softserve.itacademy.exception.JwtAuthenticationException;
import com.softserve.itacademy.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

	@Value("${jwt.token.secret}")
	private String secret;

	@Value("${jwt.token.expired}")
	private long validityInMilliseconds;

	@Autowired
	private UserDetailsService userDetailsService;

//	@Bean
//	JwtEncoder jwtEncoder() {
//		JWK jwk = new RSAKey.Builder(this.key).privateKey(this.priv).build();
//		JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
//		return new NimbusJwtEncoder(jwks);
//	}

	@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @PostConstruct
	protected void init() {secret = Base64.getEncoder().encodeToString(secret.getBytes());}

	public String createToken(String userName) {

		Claims claims = Jwts.claims().setSubject(userName);

		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(SignatureAlgorithm.HS256, secret)
				.compact();
	}

	public Authentication getAuthentication(String token){
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUserName(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUserName(String token){
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest req){
		String bearerToken = req.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer_")){
			return bearerToken.substring(7,bearerToken.length());
		}
		return null;
	}

	public boolean validateToken(String token){
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

			if (claims.getBody().getExpiration().before(new Date())){
				return false;
			}
			return true;
		}catch (JwtException | IllegalArgumentException e){
			throw new JwtAuthenticationException("Token is invalid");
		}
	}


}