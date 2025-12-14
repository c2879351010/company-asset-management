package com.example.backend.config;

import com.example.backend.entity.Users;
import com.example.backend.service.UsersService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private UsersService userService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private com.baomidou.mybatisplus.core.mapper.BaseMapper<Users> baseMapper;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void doFilterInternal_WithValidTokenAndValidUser_ShouldSetAuthentication() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        String userId = "123";
        Users user = new Users();
        user.setUserId(userId);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(validToken)).thenReturn(userId);
        when(userService.getBaseMapper()).thenReturn(baseMapper);
        when(baseMapper.selectById(userId)).thenReturn(user);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider).validateToken(validToken);
        verify(jwtTokenProvider).getUserIdFromToken(validToken);
        verify(baseMapper).selectById(userId);

        // Verify authentication was set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication instanceof UsernamePasswordAuthenticationToken);
        assertEquals(user, authentication.getPrincipal());

        verify(filterChain).doFilter(request, response);

        // Clean up
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidTokenButUserNotFound_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        String userId = "123";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(validToken)).thenReturn(userId);
        when(userService.getBaseMapper()).thenReturn(baseMapper);
        when(baseMapper.selectById(userId)).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider).validateToken(validToken);
        verify(jwtTokenProvider).getUserIdFromToken(validToken);
        verify(baseMapper).selectById(userId);

        // Verify no authentication was set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtTokenProvider.validateToken(invalidToken)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider).validateToken(invalidToken);
        verify(jwtTokenProvider, never()).getUserIdFromToken(anyString());
        verify(userService, never()).getBaseMapper();

        // Verify no authentication was set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoAuthorizationHeader_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(jwtTokenProvider, never()).getUserIdFromToken(anyString());
        verify(userService, never()).getBaseMapper();

        // Verify no authentication was set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMalformedAuthorizationHeader_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(jwtTokenProvider, never()).getUserIdFromToken(anyString());
        verify(userService, never()).getBaseMapper();

        // Verify no authentication was set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithBearerButNoToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(jwtTokenProvider,times(1)).validateToken(anyString());
        verify(jwtTokenProvider, never()).getUserIdFromToken(anyString());
        verify(userService, never()).getBaseMapper();

        // Verify no authentication was set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WhenTokenValidationThrowsException_ShouldContinueFilterChain() throws ServletException, IOException {
        // Arrange
        String token = "problematic.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenThrow(new RuntimeException("Token validation failed"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // The main assertion is that no exception is thrown and filter chain continues
        verify(filterChain).doFilter(request, response);

        // Verify no authentication was set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }

    @Test
    void doFilterInternal_WhenUserServiceThrowsException_ShouldContinueFilterChain() throws ServletException, IOException {
        // Arrange
        String validToken = "valid.jwt.token";
        String userId = "123";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtTokenProvider.validateToken(validToken)).thenReturn(true);
        when(jwtTokenProvider.getUserIdFromToken(validToken)).thenReturn(userId);
        when(userService.getBaseMapper()).thenReturn(baseMapper);
        when(baseMapper.selectById(userId)).thenThrow(new RuntimeException("Database error"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // The main assertion is that no exception is thrown and filter chain continues
        verify(filterChain).doFilter(request, response);

        // Verify no authentication was set
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }

    @Test
    void getJwtFromRequest_WithValidBearerToken_ShouldReturnToken() {
        // Arrange
        String expectedToken = "test.jwt.token";
        HttpServletRequest testRequest = mock(HttpServletRequest.class);
        when(testRequest.getHeader("Authorization")).thenReturn("Bearer " + expectedToken);

        // Act - 使用反射调用私有方法
        String actualToken = ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "getJwtFromRequest", testRequest);

        // Assert
        assertEquals(expectedToken, actualToken);
    }

    @Test
    void getJwtFromRequest_WithNoAuthorizationHeader_ShouldReturnNull() {
        // Arrange
        HttpServletRequest testRequest = mock(HttpServletRequest.class);
        when(testRequest.getHeader("Authorization")).thenReturn(null);

        // Act
        String result = ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "getJwtFromRequest", testRequest);

        // Assert
        assertNull(result);
    }

    @Test
    void getJwtFromRequest_WithMalformedAuthorizationHeader_ShouldReturnNull() {
        // Arrange
        HttpServletRequest testRequest = mock(HttpServletRequest.class);
        when(testRequest.getHeader("Authorization")).thenReturn("InvalidFormat");

        // Act
        String result = ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "getJwtFromRequest", testRequest);

        // Assert
        assertNull(result);
    }

    @Test
    void getJwtFromRequest_WithBearerButNoToken_ShouldReturnEmptyString() {
        // Arrange
        HttpServletRequest testRequest = mock(HttpServletRequest.class);
        when(testRequest.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        String result = ReflectionTestUtils.invokeMethod(jwtAuthenticationFilter, "getJwtFromRequest", testRequest);

        // Assert
        assertEquals("", result);
    }
}
