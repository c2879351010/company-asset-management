package com.example.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BackendApplicationTest {
    @InjectMocks
    private BackendApplication backendApplication;

    @Test
    void contextLoads() {
        // 验证应用类可以正常实例化
        assertNotNull(backendApplication);
    }

    @Test
    void mainMethod_ShouldRunSpringApplication() {
        // Arrange
        String[] args = new String[]{"--spring.profiles.active=test"};

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // 设置mock行为 - 当调用run方法时返回一个mock的ApplicationContext
            SpringApplication mockSpringApplication = mock(SpringApplication.class);
            springApplicationMock.when(() -> SpringApplication.run(eq(BackendApplication.class), eq(args)))
                    .thenReturn(null); // 在实际运行中这会返回ConfigurableApplicationContext

            // Act
            BackendApplication.main(args);

            // Assert - 验证SpringApplication.run被调用了一次，且参数正确
            springApplicationMock.verify(
                    () -> SpringApplication.run(BackendApplication.class, args),
                    times(1)
            );
        }
    }

    @Test
    void mainMethod_WithEmptyArgs_ShouldRunSpringApplication() {
        // Arrange
        String[] emptyArgs = new String[]{};

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            springApplicationMock.when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                    .thenReturn(null);

            // Act
            BackendApplication.main(emptyArgs);

            // Assert
            springApplicationMock.verify(
                    () -> SpringApplication.run(BackendApplication.class, emptyArgs),
                    times(1)
            );
        }
    }

    @Test
    void mainMethod_WithNullArgs_ShouldRunSpringApplication() {
        // Arrange
        String[] nullArgs = null;

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            springApplicationMock.when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                    .thenReturn(null);

            // Act
            BackendApplication.main(nullArgs);

            // Assert
            springApplicationMock.verify(
                    () -> SpringApplication.run(BackendApplication.class, nullArgs),
                    times(1)
            );
        }
    }

    @Test
    void mainMethod_WithMultipleArgs_ShouldRunSpringApplication() {
        // Arrange
        String[] multipleArgs = new String[]{"--debug", "--spring.config.location=classpath:/application-test.yml"};

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            springApplicationMock.when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                    .thenReturn(null);

            // Act
            BackendApplication.main(multipleArgs);

            // Assert
            springApplicationMock.verify(
                    () -> SpringApplication.run(BackendApplication.class, multipleArgs),
                    times(1)
            );
        }
    }

    @Test
    void applicationClass_ShouldHaveCorrectAnnotations() {
        // Arrange & Act - 使用反射检查注解
        Class<BackendApplication> clazz = BackendApplication.class;

        // Assert - 验证必要的注解存在
        assertTrue(clazz.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class));
        assertTrue(clazz.isAnnotationPresent(org.mybatis.spring.annotation.MapperScan.class));

        // 验证MapperScan注解的值
        MapperScan mapperScan = clazz.getAnnotation(MapperScan.class);
        assertNotNull(mapperScan);
        assertArrayEquals(new String[]{"com.example.backend.dao"}, mapperScan.value());
    }

    @Test
    void applicationClass_ShouldHaveMainMethod() throws NoSuchMethodException {
        // Arrange & Act
        Class<BackendApplication> clazz = BackendApplication.class;
        java.lang.reflect.Method mainMethod = clazz.getMethod("main", String[].class);

        // Assert
        assertNotNull(mainMethod);
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
        assertEquals(void.class, mainMethod.getReturnType());
    }

    @Test
    void applicationInstance_ShouldBeCreatable() {
        // Act - 直接创建实例（测试默认构造函数）
        BackendApplication application = new BackendApplication();

        // Assert
        assertNotNull(application);
        assertInstanceOf(BackendApplication.class, application);
    }

    @Test
    void mainMethod_WhenSpringApplicationThrowsException_ShouldPropagateIt() {
        // Arrange
        String[] args = new String[]{};

        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            // 模拟SpringApplication.run抛出异常
            springApplicationMock.when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                    .thenThrow(new RuntimeException("Spring application failed to start"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                BackendApplication.main(args);
            });

            // 验证SpringApplication.run确实被调用
            springApplicationMock.verify(
                    () -> SpringApplication.run(BackendApplication.class, args),
                    times(1)
            );
        }
    }

    @Test
    void toStringMethod_ShouldReturnClassName() {
        // Arrange
        BackendApplication application = new BackendApplication();

        // Act
        String toStringResult = application.toString();

        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("BackendApplication"));
    }

    @Test
    void hashCodeMethod_ShouldBeConsistent() {
        // Arrange
        BackendApplication application1 = new BackendApplication();
        BackendApplication application2 = new BackendApplication();

        // Act & Assert
        assertEquals(application1.hashCode(), application1.hashCode());
        // 注意：两个不同实例的hashCode可能相同，这是允许的
    }

    @Test
    void equalsMethod_ShouldWorkCorrectly() {
        // Arrange
        BackendApplication application1 = new BackendApplication();
        BackendApplication application2 = new BackendApplication();

        // Act & Assert
        assertTrue(application1.equals(application1)); // 自反性
        assertFalse(application1.equals(null)); // 非空性
        assertFalse(application1.equals("some string")); // 不同类型
        // 对于两个不同的实例，equals应该返回false
        assertFalse(application1.equals(application2));
    }
}
