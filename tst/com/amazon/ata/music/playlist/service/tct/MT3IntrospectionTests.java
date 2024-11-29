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
        assertNotNull(component.getAnnotation(Component.class),
                String.format("L'interface Component [%s] devrait être annotée avec @Component", component));
        assertNotNull(component.getAnnotation(Singleton.class),
                String.format("L'interface Component [%s] devrait être annotée avec @Singleton", component));
        assertNotNull(module.getAnnotation(Module.class),
                String.format("La classe Module [%s] devrait être annotée avec @Module", module));

        assertTrue(ArrayUtils.contains(component.getAnnotation(Component.class).modules(), module),
                String.format("L'interface Component [%s] devrait enregistrer le module [%s].",
                        component.getSimpleName(), module.getSimpleName()));

        log.info("Validation des classes DAO...");
        List<Class<?>> daoClasses = findNonFrameworkClasses("dynamodb", "Dao", Object.class)
                .collect(Collectors.toList());
        assertClassesAnnotatedWithInject(daoClasses, 2);

        log.info("Validation des classes Activity...");
        List<Class<?>> activityClasses = findNonFrameworkClasses("activity", "Activity", RequestHandler.class)
                .collect(Collectors.toList());
        assertClassesAnnotatedWithInject(activityClasses, 5);
    }

    @Test
    public void mt03_componentClass_providesActivityClasses() {
        findNonFrameworkClasses("activity", "Activity", RequestHandler.class)
                .forEach(clazz -> MethodQuery.inType(component).withReturnType(clazz).findMethodOrFail());
    }

    @Test
    public void mt03_module_providesSingletonDynamoDbMapper() {
        Class<?> dynamoDbMapper = ClassQuery.inExactPackage("com.amazonaws.services.dynamodbv2.datamodeling")
                .withExactSimpleName("DynamoDBMapper")
                .findClassOrFail();

        Method providerMethod = MethodQuery.inType(module).withReturnType(dynamoDbMapper)
                .findMethodOrFail();

        assertNotNull(providerMethod.getAnnotation(Provides.class),
                String.format("La méthode [%s] dans le Module [%s] devrait être annotée avec @Provides",
                        providerMethod.getName(), module.getSimpleName()));
        assertNotNull(providerMethod.getAnnotation(Singleton.class),
                String.format("La méthode [%s] dans le Module [%s] devrait être annotée avec @Singleton",
                        providerMethod.getName(), module.getSimpleName()));
    }

    @Test
    public void mt03_appClass_deleted() {
        Set<Class<?>> classes = ClassQuery.inExactPackage(BASE_PACKAGE + "dependency")
                .withExactSimpleName("App")
                .findClasses();

        assertTrue(classes.isEmpty(),
                String.format("La classe App ne devrait pas exister, mais les classes suivantes ont été trouvées : %s", classes));
    }

    private Class<?> findSingleDaggerClass(String nameContaining) {
        return findNonFrameworkClasses("dependency", nameContaining, Object.class)
                .reduce((first, second) -> fail(String.format("Plusieurs classes contiennent '%s' dans leur nom : [%s, %s]",
                        nameContaining, first.getSimpleName(), second.getSimpleName())))
                .orElseThrow(() -> new AssertionError(
                        String.format("Aucune classe trouvée avec '%s' dans le nom.", nameContaining)));
    }

    private Stream<Class<?>> findNonFrameworkClasses(String packageQualifier,
                                                     String simpleNameContaining,
                                                     Class<?> subTypeOf) {
        log.info("Recherche dans le package {}", BASE_PACKAGE + packageQualifier);
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
        assertEquals(expectedClassCount, classes.size(), String.format("Nombre de classes incorrect : %d attendu, %d trouvé.",
                expectedClassCount, classes.size()));

        classes.stream()
                .peek(clazz -> log.info("Validation que la classe {} a un constructeur annoté avec @Inject", clazz))
                .map(ConstructorQuery::inClass)
                .map(ConstructorQuery::findConstructorOrFail)
                .forEach(constructor -> assertNotNull(constructor.getAnnotation(Inject.class),
                        String.format("Le constructeur de la classe [%s] devrait être annoté avec @Inject",
                                constructor.getDeclaringClass())));
    }
}
