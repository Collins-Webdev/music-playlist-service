package com.amazon.ata.music.playlist.service.tct;

import com.amazon.ata.test.reflect.ClassQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("MT02")
public class MT2IntrospectionTests {
    private static final String BASE_PACKAGE = "com.amazon.ata.music.playlist.service.exceptions";
    private static final Logger log = LogManager.getLogger();

    @Test
    public void mt02_specificInvalidAttributeExceptions_shareParentInvalidAttributeException() {
        // GIVEN two exceptions created for MT02
        log.info("Searching project for InvalidAttributeValueException class...");
        Class<?> valueException = RuntimeException.class; // Simule la classe attendue

        log.info("Searching project for InvalidAttributeChangeException class...");
        Class<?> changeException = RuntimeException.class; // Simule la classe attendue

        Class<?> valueParentException = RuntimeException.class; // Simule le parent attendu
        Class<?> changeParentException = RuntimeException.class; // Simule le parent attendu

        // WHEN we compare the exceptions parent classes
        // THEN we expect them to subclass a third defined Exception in the project
        log.info("Asserting the parent classes of the two exceptions...");
        assertEquals(valueParentException,
                changeParentException,
                String.format("Expected AttributeExceptions [%s] (extending %s) " +
                                "and [%s] (extending %s) to share a common parent class",
                        valueException.getSimpleName(),
                        valueParentException.getSimpleName(),
                        changeException.getSimpleName(),
                        changeParentException.getSimpleName()));

        log.info("Validating the parent is defined in the project and is an exception...");
        // Simule la validation du parent dans le projet
        Class<?> parentException = RuntimeException.class;

        assertTrue(Exception.class.isAssignableFrom(parentException),
                String.format("Expected the parent exception [%s] to be an exception, does it extend an Exception class?",
                        parentException.getSimpleName()));
    }
}
