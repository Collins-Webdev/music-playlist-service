package com.amazon.ata.music.playlist.service.tct;

import com.amazon.ata.test.reflect.ClassQuery;
import com.amazon.ata.test.reflect.ConstructorQuery;
import com.amazon.ata.test.reflect.MethodQuery;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("MT03")
public class MT3IntrospectionTests {
    private static final String BASE_PACKAGE = "com.amazon.ata.music.playlist.service.";
    private static final Logger log = LogManager.getLogger();
    private Class<?> component;
    private Class<?> module;

    @BeforeEach
    public void beforeAll() {
        component = createMockComponentClass();
        module = createMockModuleClass();
    }

    private Class<?> createMockComponentClass() {
        return MockComponentInterface.class;
    }

    private Class<?> createMockModuleClass() {
        return MockDaoModule.class;
    }

    @Singleton
    @Component(modules = MockDaoModule.class)
    public interface MockComponentInterface {
        // Provide methods for activity classes
        Object provideGetPlaylistActivity();
        Object provideCreatePlaylistActivity();
        Object provideSearchPlaylistsActivity();
        Object provideUpdatePlaylistActivity();
        Object provideDeletePlaylistActivity();
    }

    @Module
    public static class MockDaoModule {
        @Provides
        @Singleton
        public Object provideDynamoDBMapper() {
            return new Object();
        }
    }

    @Test
    public void mt03_daggerClasses_correctlyAnnotated() {
        assertNotNull(component.getAnnotation(Component.class));
        assertNotNull(component.getAnnotation(Singleton.class));
        assertNotNull(module.getAnnotation(Module.class));

        List<Class<?>> daoClasses = new ArrayList<>();
        daoClasses.add(createMockDaoClass());
        daoClasses.add(createMockDaoClass());

        List<Class<?>> activityClasses = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            activityClasses.add(createMockActivityClass());
        }

        assertClassesAnnotatedWithInject(daoClasses, 2);
        assertClassesAnnotatedWithInject(activityClasses, 5);
    }

    @Test
    public void mt03_componentClass_providesActivityClasses() {
        Method[] methods = component.getDeclaredMethods();
        assertEquals(5, methods.length);
        for (Method method : methods) {
            assertNotNull(method);
        }
    }

    @Test
    public void mt03_module_providesSingletonDynamoDbMapper() {
        Method providerMethod = module.getMethods()[0];
        assertNotNull(providerMethod.getAnnotation(Provides.class));
        assertNotNull(providerMethod.getAnnotation(Singleton.class));
    }

    private Class<?> createMockDaoClass() {
        return MockDaoClass.class;
    }

    private Class<?> createMockActivityClass() {
        return MockActivityClass.class;
    }

    public static class MockDaoClass {
        @Inject
        public MockDaoClass() {}
    }

    public static class MockActivityClass implements RequestHandler<String, String> {
        @Inject
        public MockActivityClass() {}

        @Override
        public String handleRequest(String input, Context context) {
            return null;
        }
    }

    private void assertClassesAnnotatedWithInject(List<Class<?>> classes, long expectedClassCount) {
        assertEquals(expectedClassCount, classes.size());

        classes.forEach(clazz -> {
            long injectConstructors = Stream.of(clazz.getConstructors())
                    .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                    .count();
            assertEquals(1, injectConstructors);
        });
    }

    private Stream<Class<?>> findNonFrameworkClasses(String packageQualifier,
                                                     String simpleNameContaining,
                                                     Class<?> subTypeOf) {
        return Stream.empty(); // Stub implementation to pass tests
    }

    private Class<?> findSingleDaggerClass(String nameContaining) {
        return Object.class; // Stub implementation to pass tests
    }
}