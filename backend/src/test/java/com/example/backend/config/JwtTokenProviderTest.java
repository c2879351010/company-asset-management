// java
package com.example.backend.config;

import com.example.backend.entity.Users;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private Users user;
    private final String secretKey = "8V7r5bHkSlKLT8";
    private final int expiration = 86400000;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setUserId("user123");
        user.setEmail("test@example.com");
        user.setRole("ADMIN");

        // 设置私有字段值
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret", secretKey);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", expiration);
    }

    @Test
    void generateToken_WithValidUser_ShouldReturnValidToken() {
        // Act
        String token = jwtTokenProvider.generateToken(user);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // 验证token可以被解析
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        assertEquals("user123", userId);
    }

    @Test
    void generateToken_WithNullUser_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            jwtTokenProvider.generateToken(null);
        });
    }

    @Test
    void generateToken_WithUserHavingNullFields_ShouldGenerateToken() {
        // Arrange
        Users userWithNullFields = new Users();
        userWithNullFields.setUserId("user456");
        // email and role are null

        // Act
        String token = jwtTokenProvider.generateToken(userWithNullFields);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // 验证token可以被解析
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        assertEquals("user456", userId);
    }

    @Test
    void getUserIdFromToken_WithValidToken_ShouldReturnUserId() {
        // Arrange
        String token = jwtTokenProvider.generateToken(user);

        // Act
        String userId = jwtTokenProvider.getUserIdFromToken(token);

        // Assert
        assertEquals("user123", userId);
    }

    @Test
    void getUserIdFromToken_WithInvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(JwtException.class, () -> {
            jwtTokenProvider.getUserIdFromToken(invalidToken);
        });
    }

    @Test
    void getUserIdFromToken_WithMalformedToken_ShouldThrowException() {
        // Arrange
        String malformedToken = "malformed.token";

        // Act & Assert
        assertThrows(JwtException.class, () -> {
            jwtTokenProvider.getUserIdFromToken(malformedToken);
        });
    }

    @Test
    void getUserIdFromToken_WithExpiredToken_ShouldThrowException() throws Exception {
        // Arrange - 创建一个过期的token
        JwtTokenProvider expiredTokenProvider = new JwtTokenProvider();

        // 使用反射设置很短的过期时间
        Field secretField = JwtTokenProvider.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(expiredTokenProvider, secretKey);

        Field expField = JwtTokenProvider.class.getDeclaredField("jwtExpirationInMs");
        expField.setAccessible(true);
        expField.set(expiredTokenProvider, -1000); // 负值表示已经过期

        String expiredToken = expiredTokenProvider.generateToken(user);

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> {
            jwtTokenProvider.getUserIdFromToken(expiredToken);
        });
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String token = jwtTokenProvider.generateToken(user);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() throws Exception {
        // Arrange - 创建一个过期的token
        JwtTokenProvider expiredTokenProvider = new JwtTokenProvider();

        Field secretField = JwtTokenProvider.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(expiredTokenProvider, secretKey);

        Field expField = JwtTokenProvider.class.getDeclaredField("jwtExpirationInMs");
        expField.setAccessible(true);
        expField.set(expiredTokenProvider, -1000);

        String expiredToken = expiredTokenProvider.generateToken(user);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithMalformedToken_ShouldReturnFalse() {
        // Arrange
        String malformedToken = "malformed";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // Arrange
        String emptyToken = "";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(emptyToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // Arrange
        String nullToken = null;

        // Act
        boolean isValid = jwtTokenProvider.validateToken(nullToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void getSigningKey_WithValidSecret_ShouldReturnKey() {
        // Act - 使用反射调用私有方法
        Object key = ReflectionTestUtils.invokeMethod(jwtTokenProvider, "getSigningKey");

        // Assert
        assertNotNull(key);
    }

    @Test
    void getSigningKey_WithEmptySecret_ShouldReturnKey() throws Exception {
        // Arrange
        JwtTokenProvider emptySecretProvider = new JwtTokenProvider();
        Field secretField = JwtTokenProvider.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(emptySecretProvider, "");

        // Act
        Object key = ReflectionTestUtils.invokeMethod(emptySecretProvider, "getSigningKey");

        // Assert
        assertNotNull(key);
    }

    @Test
    void getSigningKey_WithNullSecret_ShouldThrowException() throws Exception {
        // Arrange
        JwtTokenProvider nullSecretProvider = new JwtTokenProvider();
        Field secretField = JwtTokenProvider.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(nullSecretProvider, null);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            ReflectionTestUtils.invokeMethod(nullSecretProvider, "getSigningKey");
        });
    }

    @Test
    void constructor_Injection_ShouldWork() {
        // Arrange & Act - 通过@InjectMocks已经完成注入

        // Assert - 验证没有异常抛出，且对象不为空
        assertNotNull(jwtTokenProvider);
    }

    @Test
    void generateToken_WithCustomProperties_ShouldUseProperties() throws Exception {
        // Arrange
        JwtTokenProvider customProvider = new JwtTokenProvider();
        Field secretField = JwtTokenProvider.class.getDeclaredField("jwtSecret");
        secretField.setAccessible(true);
        secretField.set(customProvider, "customSecretKey");

        Field expField = JwtTokenProvider.class.getDeclaredField("jwtExpirationInMs");
        expField.setAccessible(true);
        expField.set(customProvider, 5000); // 5 seconds

        Users testUser = new Users();
        testUser.setUserId("customUser");
        testUser.setEmail("custom@example.com");
        testUser.setRole("USER");

        // Act
        String token = customProvider.generateToken(testUser);

        // Assert
        assertNotNull(token);

        // 验证token可以被解析
        String userId = customProvider.getUserIdFromToken(token);
        assertEquals("customUser", userId);
    }

    @Test
    void validateToken_WithUnsupportedJwtException_ShouldReturnFalse() {
        // Arrange
        String token = "unsupported.jwt.token";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithMalformedJwtException_ShouldReturnFalse() {
        // Arrange
        String token = "malformed.jwt.token";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithSignatureException_ShouldReturnFalse() {
        // Arrange - 使用不同的密钥生成token
        JwtTokenProvider differentKeyProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(differentKeyProvider, "jwtSecret", "differentSecretKey");
        ReflectionTestUtils.setField(differentKeyProvider, "jwtExpirationInMs", expiration);

        String tokenWithDifferentKey = differentKeyProvider.generateToken(user);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(tokenWithDifferentKey);

        // Assert
        assertFalse(isValid);
    }
}
