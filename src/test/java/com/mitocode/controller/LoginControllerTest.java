package com.mitocode.controller;

import com.mitocode.exception.GlobalErrorHandler;
import com.mitocode.model.Rol;
import com.mitocode.model.Usuario;
import com.mitocode.repo.IUsuarioRepo;
import com.mitocode.security.*;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest {

    private MockMvc mockMvc;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private JwtUserDetailsService userDetailsService;
    private IUsuarioRepo usuarioRepo;
    private ObjectMapper objectMapper;

    private static final String SECRET = "MitoAcademyClaveSecretaSuperSeguraDeAlMenos32Bytes2026!";

    @BeforeEach
    void init() {
        SecurityContextHolder.clearContext();
        authenticationManager = mock(AuthenticationManager.class);
        usuarioRepo = mock(IUsuarioRepo.class);
        jwtTokenUtil = new JwtTokenUtil(SECRET, 18000000L);
        userDetailsService = new JwtUserDetailsService(usuarioRepo);
        objectMapper = new ObjectMapper();

        LoginController controller = new LoginController(authenticationManager, jwtTokenUtil, userDetailsService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalErrorHandler())
                .build();
    }

    @Test
    void loginReturnsAccessTokenWhenCredentialsAreValid() throws Exception {
        Rol rol = new Rol(1, "ADMIN", "Administrador");
        Usuario usuario = new Usuario(1, "admin", "$2a$10$hashedPassword", rol, true);

        when(usuarioRepo.findOneByUsername("admin")).thenReturn(usuario);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("admin", "123"));

        JwtRequest request = new JwtRequest("admin", "123");

        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").isNotEmpty());
    }

    @Test
    void loginReturnsUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        JwtRequest request = new JwtRequest("admin", "wrong");

        mockMvc.perform(post("/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("INVALID_CREDENTIALS"));
    }

    @Test
    void jwtFilterSetsAuthenticationWhenBearerTokenIsValid() throws Exception {
        Rol rol = new Rol(1, "ADMIN", "Administrador");
        Usuario usuario = new Usuario(1, "admin", "$2a$10$hashedPassword", rol, true);
        when(usuarioRepo.findOneByUsername("admin")).thenReturn(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
        String token = jwtTokenUtil.generateToken(userDetails);

        JwtRequestFilter filter = new JwtRequestFilter(userDetailsService, jwtTokenUtil);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
