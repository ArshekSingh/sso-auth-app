package com.sas.sso.serviceimpl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.sas.sso.entity.AppMaster;
import com.sas.sso.entity.User;
import com.sas.sso.entity.UserSession;
import com.sas.sso.repository.AccessGroupRepository;
import com.sas.sso.repository.AppMasterRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	@Value("${app.jwt.secretkey}")
	String secretKey;

	@Value("${app.jwt.expiry}")
	Long expiry;
	
	@Autowired
	AppMasterRepository appMasterRepository;

	@Autowired
	AccessGroupRepository accessGroupRepository;


	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public String generateToken(User userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}

	public String generateToken(Map<String, Object> extraClaims, User userDetails) {
		Set<String> apps = setApps(userDetails);
		return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expiry))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.claim("authorities", setAuthorities(userDetails, apps)).claim("apps", setApps(userDetails)).claim("appInfo", setAppInfos(userDetails))
				.claim("company", userDetails.getCompanyMaster()).compact();
	}

	private HashMap<String, AppMaster> setAppInfos(User userDetails) {

		HashMap<String, AppMaster> appInfos = new HashMap<>();
		Optional<List<AppMaster>> appMasterOptional = appMasterRepository
				.findByCompanyId(userDetails.getCompanyMaster().getCompanyId());

		if (appMasterOptional.isPresent()) {
			appMasterOptional.get().parallelStream().forEach(app -> {
				appInfos.put(app.getApplicationName(), app);

			});
		}
		return appInfos;
	}
	private Set<String> setApps(User userDetails) {
		Optional<List<AppMaster>> appMasterOptional = appMasterRepository
				.findByCompanyId(userDetails.getCompanyMaster().getCompanyId());

		if (appMasterOptional.isPresent()) {
			return appMasterOptional.get().parallelStream().map(AppMaster::getApplicationName)
					.collect(Collectors.toSet());
		}
		return Set.of();
	}

	private HashMap<String, Set<String>> setAuthorities(User userDetails, Set<String> apps) {

		HashMap<String, Set<String>> appRoles = new HashMap<>();

		apps.parallelStream().forEach(app -> {
			appRoles.put(app, accessGroupRepository.findAllRolesOfAppAndCompany(userDetails.getId(), app));

		});

		return appRoles;
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

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
	}

	private Key getSignInKey() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}

	public boolean isTokenValid(String token, UserSession userSession) {
		final String username = extractUsername(token);
		return (username.equals(userSession.getEmail())) && !isTokenExpired(token);
	}
}
