package in.harshitkumar.centsaiapi.security;

import in.harshitkumar.centsaiapi.exception.UserNotFound;
import in.harshitkumar.centsaiapi.models.User;
import in.harshitkumar.centsaiapi.repository.UserRepository;
import in.harshitkumar.centsaiapi.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        Long userId;
        try {
            userId = jwtUtil.getUserIdFromJwtToken(token);
        } catch (Exception e) {
            log.error("JwtAuthenticationFilter: invalid token: {}", e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (!jwtUtil.validateJwtToken(token)) {
                log.warn("JwtAuthenticationFilter: token invalid/expired for userId {}", userId);
                filterChain.doFilter(request, response);
                return;
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFound("User not found with id: " + userId));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, token, Collections.emptyList());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JwtAuthenticationFilter: authenticated userId {}", userId);
        }

        filterChain.doFilter(request, response);
    }
}
