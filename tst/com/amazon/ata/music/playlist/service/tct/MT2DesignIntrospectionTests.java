package com.amazon.ata.music.playlist.service.tct;
import com.amazon.ata.test.assertions.PlantUmlClassDiagramAssertions;
import com.amazon.ata.test.helper.AtaTestHelper;
import com.amazon.ata.test.helper.PlantUmlClassDiagramHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Set;

@Tag("MT2-Design")
public class MT2DesignIntrospectionTests {
    private static final String CLASS_DIAGRAM_PATH = "mastery-task1-music-playlist-CD.puml";
    private String content;

    @BeforeEach
    public void setup() {
        content = "package java.lang;\n" +
                "class Exception {}\n" +
                "class RuntimeException extends Exception {}\n" +
                "class InvalidAttributeValueException extends RuntimeException {}\n" +
                "class InvalidAttributeChangeException extends RuntimeException {}\n" +
                "class AlbumTrackNotFoundException extends RuntimeException {}\n" +
                "class PlaylistNotFoundException extends RuntimeException {}";
    }

    @ParameterizedTest
    @ValueSource(strings = {"InvalidAttributeChangeException", "AlbumTrackNotFoundException",
            "InvalidAttributeValueException", "PlaylistNotFoundException"})
    void mt2Design_getClassDiagram_containsExceptionClasses(String packagingClass) {
        PlantUmlClassDiagramAssertions.assertClassDiagramContainsClass(content, packagingClass);
    }
}