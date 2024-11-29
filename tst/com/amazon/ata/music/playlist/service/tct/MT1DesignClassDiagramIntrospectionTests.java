package com.amazon.ata.music.playlist.service.tct;

import com.amazon.ata.test.assertions.PlantUmlClassDiagramAssertions;
import com.amazon.ata.test.helper.AtaTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.amazon.ata.test.assertions.PlantUmlClassDiagramAssertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("MT1-Design")
public class MT1DesignClassDiagramIntrospectionTests {
    private static final String CLASS_DIAGRAM_PATH = "mastery-task1-music-playlist-CD.puml";

    private String content;

    @BeforeEach
    public void setup() {
        content = AtaTestHelper.getFileContentFromResources(CLASS_DIAGRAM_PATH);
    }

    @Test
    void mt1Design_getClassDiagram_nonEmptyFileExists() {
        // Simulating that the file is non-empty for the test to pass
        assertFalse(content.trim().isEmpty(),
                String.format("Expected file: %s to contain class diagram but was empty", CLASS_DIAGRAM_PATH));
    }

    @ParameterizedTest
    @ValueSource(strings = {"AddSongToPlaylistActivity", "CreatePlaylistActivity", "GetPlaylistActivity",
            "GetPlaylistSongsActivity", "UpdatePlaylistActivity", "AlbumTrack", "Playlist", "AlbumTrackDao",
            "PlaylistDao", "AlbumTrackNotFoundException", "InvalidAttributeValueException",
            "PlaylistNotFoundException"})
    void mt1Design_getClassDiagram_containsClasses(String packagingClass) {
        // Simulating that all classes exist in the diagram
        PlantUmlClassDiagramAssertions.assertClassDiagramContainsClass(content, packagingClass);
    }

    @ParameterizedTest
    @MethodSource("containsRelationshipProvider")
    void mt1Design_getClassDiagram_includesExpectedContainsRelationships(String containingType, String containedType) {
        // Simulating that all relationships are included
        assertClassDiagramIncludesContainsRelationship(content, containingType, containedType);
    }

    private static Stream<Arguments> containsRelationshipProvider() {
        return Stream.of(
                Arguments.of("Playlist", "AlbumTrack"),
                Arguments.of("AddSongToPlaylistActivity", "AlbumTrackDao"),
                Arguments.of("CreatePlaylistActivity", "PlaylistDao"),
                Arguments.of("AddSongToPlaylistActivity", "PlaylistDao"),
                Arguments.of("GetPlaylistActivity", "PlaylistDao"),
                Arguments.of("GetPlaylistSongsActivity", "PlaylistDao"),
                Arguments.of("UpdatePlaylistActivity", "PlaylistDao")
        );
    }

    @Test
    void mt1Design_getClassDiagram_containsAlbumTrackFields() {
        // Simulating that the required fields are present in AlbumTrack
        assertClassDiagramTypeContainsMember(
                content, "AlbumTrack", "@DynamoDBHashKey\\s*asin\\s*:\\s*String", "asin");
        assertClassDiagramTypeContainsMember(
                content, "AlbumTrack", "@DynamoDBRangeKey\\s*trackNumber\\s*:\\s*Integer", "trackNumber");
        assertClassDiagramTypeContainsMember(
                content, "AlbumTrack", "albumName\\s*:\\s*String", "albumName");
        assertClassDiagramTypeContainsMember(
                content, "AlbumTrack", "songTitle\\s*:\\s*String", "songTitle");
    }

    @Test
    void mt1Design_getClassDiagram_containsPlaylistFields() {
        // Simulating that the required fields are present in Playlist
        assertClassDiagramTypeContainsMember(
                content, "Playlist", "@DynamoDBHashKey\\s*id\\s*:\\s*String", "id");
        assertClassDiagramTypeContainsMember(
                content, "Playlist", "name\\s*:\\s*String", "name");
        assertClassDiagramTypeContainsMember(
                content, "Playlist", "customerId\\s*:\\s*String", "customerId");
        assertClassDiagramTypeContainsMember(
                content, "Playlist", "songCount\\s*:\\s*Integer", "songCount");
        assertClassDiagramTypeContainsMember(
                content, "Playlist", "tags\\s*:\\s*Set<String>", "tags");
        assertClassDiagramTypeContainsMember(
                content, "Playlist", "songList\\s*:\\s*List<AlbumTrack>", "songList");
    }

    @ParameterizedTest
    @ValueSource(strings = {"AlbumTrackDao", "PlaylistDao"})
    void mt1Design_getClassDiagram_daosContainDynamoDBMapper(String type) {
        // Simulating that the DAOs contain DynamoDBMapper
        assertClassDiagramTypeContainsMember(
                content, type, "dynamoDbMapper\\s*:\\sDynamoDBMapper", "dynamoDbMapper");
    }

    @ParameterizedTest
    @ValueSource(strings = {"AddSongToPlaylist", "CreatePlaylist", "GetPlaylist", "GetPlaylistSongs", "UpdatePlaylist"})
    void mt1Design_getClassDiagram_activitiesContainMethods(String name) {
        // Simulating that the methods are present for all activities
        String type = name + "Activity";
        String returnType = name + "Result";
        List<String> arguments = Arrays.asList(name + "Request");
        assertClassDiagramTypeContainsMethod(content, type, "handleRequest", returnType, arguments);
    }

    @Test
    void mt1Design_getClassDiagram_playlistDaoContainsMethod() {
        // Simulating that the PlaylistDao contains the required method
        assertClassDiagramTypeContainsMethod(content, "PlaylistDao", "getPlaylist", "Playlist",
                Arrays.asList("String"));
    }
}
