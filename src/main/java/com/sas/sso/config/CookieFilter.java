package com.sas.sso.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sas.sso.dto.Response;
import com.sas.sso.entity.TokenSession;
import com.sas.sso.entity.UserSession;
import com.sas.sso.repository.CompanyMasterRepository;
import com.sas.sso.repository.TokenRedisRepository;
import com.sas.sso.repository.UserRedisRepository;
import com.sas.sso.serviceimpl.JwtService;
import com.sas.sso.utils.UserUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CookieFilter implements Filter {

    @Autowired
    TokenRedisRepository tokenRedisRepository;

    @Autowired
    UserRedisRepository userRedisRepository;

    @Autowired
    UserUtils userUtils;

    @Autowired
    JwtService jwtService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private CompanyMasterRepository companyMasterRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        boolean isValidRequest = true;
        try {
            if (httpServletRequest.getRequestURI().contains("/api/")
                    && !"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
                TokenSession tokenFromCookie = userUtils.getTokenSession();

                if (tokenFromCookie != null) {
                    Optional<UserSession> userSessionOptional = userRedisRepository
                            .findById(tokenFromCookie.getUserId());
                    if (userSessionOptional.isPresent()
                            && jwtService.isTokenValid(tokenFromCookie.getToken(), userSessionOptional.get())) {
                        log.info("token and user valid , passing to controller");

                    } else {
                        isValidRequest = false;
                    }
                } else {
                    isValidRequest = false;
                }
            }
            if (!"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod()) && isValidRequest) {
                chain.doFilter(httpServletRequest, httpServletResponse);
            } else {
                if (httpServletRequest.getRequestURI().contains("/api/")
                        && !"OPTIONS".equalsIgnoreCase(httpServletRequest.getMethod())) {
                    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write(objectMapper.writeValueAsString(
                            new Response(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED)));
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    setCommonHeaderInResponse(httpServletRequest, httpServletResponse);
                } else {
                    httpServletResponse.setStatus(HttpStatus.OK.value());
                    response.getWriter().write(objectMapper
                            .writeValueAsString(new Response(HttpStatus.OK.getReasonPhrase(), HttpStatus.OK)));
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                }

            }
        } catch (ExpiredJwtException | SignatureException e) {
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.getWriter()
                    .write(objectMapper.writeValueAsString(new Response("Jwt Expired or malformed", HttpStatus.UNAUTHORIZED)));
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            setCommonHeaderInResponse(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpServletResponse.getWriter().write(
                    objectMapper.writeValueAsString(new Response(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR)));
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            setCommonHeaderInResponse(httpServletRequest, httpServletResponse);
        }
    }

    private void setCommonHeaderInResponse(HttpServletRequest httpServletRequest,
                                           HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");
    }

}