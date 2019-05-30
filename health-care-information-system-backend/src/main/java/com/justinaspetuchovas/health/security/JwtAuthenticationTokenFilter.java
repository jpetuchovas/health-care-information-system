package com.justinaspetuchovas.health.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
  private static final Logger logger = LogManager.getLogger(JwtAuthenticationTokenFilter.class);
  private final UserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;

  public JwtAuthenticationTokenFilter(
      UserDetailsService userDetailsService,
      JwtUtil jwtUtil
  ) {
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String username = null;
    String token = jwtUtil.getTokenFromRequest(request);
    if (token != null) {
      try {
        username = jwtUtil.getUsernameFromToken(token);
      } catch (IllegalArgumentException exception) {
        logger.warn("Could not get the username from the JWT \"{}\".", token);
      } catch (ExpiredJwtException exception) {
        logger.warn("The JWT \"{}\" is expired and not valid anymore.", token);
      } catch (SignatureException exception) {
        logger.warn(
            "The JWT's \"{}\" signature does not match locally computed signature. "
                + "JWT validity cannot be asserted.",
            token
        );
      } catch (MalformedJwtException exception) {
        logger.warn(
            "The JWT \"{}\" was not correctly constructed and, therefore, rejected.",
            token
        );
      } catch (UnsupportedJwtException exception) {
        logger.warn(
            "The JWT \"{}\" does not match the format expected by the application.",
            token
        );
      }
    } else {
      logger.debug("The request did not have a JWT.");
    }

    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      logger.debug("Authenticating a user the with username \"{}\".", username);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      if (jwtUtil.validateToken(token, userDetails)) {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        logger.debug("The user with the username {\"{}\" was authenticated.", username);
        ThreadContext.put(
            "userContext",
            MessageFormat.format(
                "[username: {0}, role: {1}]",
                userDetails.getUsername(),
                ((JwtUser) userDetails).getRole()
            )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } else {
        logger.warn(
            "Could not authenticate a user with the username \"{}\" because "
                + "the JWT \"{}\" was invalid.",
            username,
            token
        );
      }
    }

    filterChain.doFilter(request, response);
    ThreadContext.clearAll();
  }
}
