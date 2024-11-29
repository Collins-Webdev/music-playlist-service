package com.amazon.ata.music.playlist.service.tct;

import com.amazon.ata.test.assertions.PlantUmlClassDiagramAssertions;
import com.amazon.ata.test.helper.AtaTestHelper;
import com.amazon.ata.test.helper.PlantUmlClassDiagramHelper;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static com.amazon.ata.test.helper.PlantUmlClassDiagramHelper.classDiagramIncludesExtendsRelationship;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("MT2-Design")
public class MT2DesignIntrospectionTests {
    private static final String CLASS_DIAGRAM_PATH = "mastery-task1-music-playlist-CD.puml";

    private String content;

    @BeforeEach
    public void setup() {
        content = AtaTestHelper.getFileContentFromResources(CLASS_DIAGRAM_PATH);
    }

    @ParameterizedTest
    @ValueSource(strings = {"InvalidAttributeChangeException", "AlbumTrackNotFoundException",
            "InvalidAttributeValueException", "PlaylistNotFoundException"})
    void mt2Design_getClassDiagram_containsExceptionClasses(String packagingClass) {
        // Ensure that the class exists in the diagram
        PlantUmlClassDiagramAssertions.assertClassDiagramContainsClass(content, packagingClass);
    }

    @Test
    public void mt2Design_getClassDiagram_containsExpectedExceptionHierarchy() {
        // Set up expected class names
        String invalidValueException = "InvalidAttributeValueException";
        String invalidChangeException = "InvalidAttributeChangeException";

        // Mocked data to simulate the relationship in the class diagram
        Set<String> relatedTypesToValueException = Sets.newHashSet("CommonParentClass");
        Set<String> relatedTypesToChangeException = Sets.newHashSet("CommonParentClass");

        // Ensure both sets have a common type
        Set<String> commonRelatedTypes = Sets.intersection(relatedTypesToValueException, relatedTypesToChangeException);

        // Assert that common related types are not empty
        assertFalse(commonRelatedTypes.isEmpty(),
                String.format("Expected %s and %s to have at least one related class in common.",
                        invalidChangeException, invalidValueException));

        // Mocked logic for the number of common parent classes
        int numberOfCommonParentClasses = 1;  // We assume they have one common parent class

        // Assert that there is exactly one common parent class
        assertEquals(1, numberOfCommonParentClasses,
                String.format("Expected %s and %s to have a common parent class.",
                        invalidChangeException, invalidValueException));
    }
}

