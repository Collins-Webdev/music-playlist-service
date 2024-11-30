package com.amazon.ata.music.playlist.service.tct;

import com.amazon.ata.test.reflect.ClassQuery;
import com.amazon.ata.test.reflect.ConstructorQuery;
import com.amazon.ata.test.reflect.MethodQuery;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
        log.info("Recherche d'une seule interface @Component...");
        component = findSingleDaggerClass("Component");

        log.info("Recherche d'une seule classe @Module...");
        module = findSingleDaggerClass("Module");
    }

    @Test
    public void mt03_daggerClasses_correctlyAnnotated() {
        // Ensure component is annotated with @Component and @Singleton
        assertNotNull(component.getAnnotation(Component.class),
                "Component interface should be annotated with @Component");
        assertNotNull(component.getAnnotation(Singleton.class),
                "Component interface should be annotated with @Singleton");

        // Ensure module is annotated with @Module
        assertNotNull(module.getAnnotation(Module.class),
                "Module class should be annotated with @Module");

        // Ensure component includes the module
        assertTrue(ArrayUtils.contains(component.getAnnotation(Component.class).modules(), module),
                "Component should register the module");

        log.info("Validation des classes DAO...");
        // Validate DAO classes
        List<Class<?>> daoClasses = findNonFrameworkClasses("dynamodb", "Dao", Object.class)
                .collect(Collectors.toList());
        assertClassesAnnotatedWithInject(daoClasses, 2);

        log.info("Validation des classes Activity...");
        // Validate Activity classes
        List<Class<?>> activityClasses = findNonFrameworkClasses("activity", "Activity", RequestHandler.class)
                .collect(Collectors.toList());
        assertClassesAnnotatedWithInject(activityClasses, 5);
    }

    @Test
    public void mt03_componentClass_providesActivityClasses() {
        // Ensure component provides methods for all activity classes
        findNonFrameworkClasses("activity", "Activity", RequestHandler.class)
                .forEach(clazz -> {
                    Method method = MethodQuery.inType(component).withReturnType(clazz).findMethodOrFail();
                    assertNotNull(method, "Component should provide method for " + clazz.getSimpleName());
                });
    }

    @Test
    public void mt03_module_providesSingletonDynamoDbMapper() {
        // Find DynamoDBMapper class
        Class<?> dynamoDbMapper = ClassQuery.inExactPackage("com.amazonaws.services.dynamodbv2.datamodeling")
                .withExactSimpleName("DynamoDBMapper")
                .findClassOrFail();

        // Find provider method in module
        Method providerMethod = MethodQuery.inType(module).withReturnType(dynamoDbMapper)
                .findMethodOrFail();

        // Ensure method is annotated with @Provides and @Singleton
        assertNotNull(providerMethod.getAnnotation(Provides.class),
                "Method in Module should be annotated with @Provides");
        assertNotNull(providerMethod.getAnnotation(Singleton.class),
                "Method in Module should be annotated with @Singleton");
    }

    @Test
    public void mt03_appClass_deleted() {
        // Ensure App class is deleted
        Set<Class<?>> classes = ClassQuery.inExactPackage(BASE_PACKAGE + "dependency")
                .withExactSimpleName("App")
                .findClasses();

        assertTrue(classes.isEmpty(),
                "App class should not exist");
    }

    private Class<?> findSingleDaggerClass(String nameContaining) {
        return findNonFrameworkClasses("dependency", nameContaining, Object.class)
                .reduce((first, second) -> fail(String.format("Multiple classes contain '%s' in their name: [%s, %s]",
                        nameContaining, first.getSimpleName(), second.getSimpleName())))
                .orElseThrow(() -> new AssertionError(
                        String.format("No class found containing '%s' in name.", nameContaining)));
    }

    private Stream<Class<?>> findNonFrameworkClasses(String packageQualifier,
                                                     String simpleNameContaining,
                                                     Class<?> subTypeOf) {
        log.info("Searching in package {}", BASE_PACKAGE + packageQualifier);
        return ClassQuery.inExactPackage(BASE_PACKAGE + packageQualifier)
                .withSimpleNameContaining(simpleNameContaining)
                .withSubTypeOf(subTypeOf)
                .findClasses().stream()
                .filter(clazz -> !clazz.getSimpleName().contains("Test"))
                .filter(clazz -> !clazz.getSimpleName().contains("Factory"))
                .filter(clazz -> !clazz.getSimpleName().contains("Dagger"))
                .filter(clazz -> !clazz.getSimpleName().contains("_"))
                .filter(clazz -> !clazz.getSimpleName().contains("ExecuteTctActivity"));
    }

    private void assertClassesAnnotatedWithInject(List<Class<?>> classes,
                                                  long expectedClassCount) {
        // Ensure correct number of classes
        assertEquals(expectedClassCount, classes.size(),
                String.format("Incorrect number of classes: expected %d, found %d",
                        expectedClassCount, classes.size()));

        // Ensure each class has a constructor annotated with @Inject
        classes.stream()
                .peek(clazz -> log.info("Validating that class {} has a constructor annotated with @Inject", clazz))
                .map(ConstructorQuery::inClass)
                .map(ConstructorQuery::findConstructorOrFail)
                .forEach(constructor -> assertNotNull(constructor.getAnnotation(Inject.class),
                        String.format("Constructor of class [%s] should be annotated with @Inject",
                                constructor.getDeclaringClass())));
    }
}