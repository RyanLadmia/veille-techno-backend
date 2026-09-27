package fr.ryan.api_kanban.security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import fr.ryan.api_kanban.entity.User;
import fr.ryan.api_kanban.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserRepository userRepository;

	public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			if (jwtService.isValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
				UUID userId = jwtService.extractUserId(token);
				userRepository.findById(userId).ifPresent(user -> setAuthentication(user));
			}
		}
		filterChain.doFilter(request, response);
	}

	private void setAuthentication(User user) {
		var authentication = new UsernamePasswordAuthenticationToken(
			user.getId(),
			null,
			List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
