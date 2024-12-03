package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.amazon.ata.music.playlist.service.models.requests.AddSongToPlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.AddSongToPlaylistResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AddSongToPlaylistActivityTest {
    @Mock
    private PlaylistDao playlistDao;

    @Mock
    private AlbumTrackDao albumTrackDao;

    private AddSongToPlaylistActivity addSongToPlaylistActivity;

    @BeforeEach
    public void setup() {
        initMocks(this);
        addSongToPlaylistActivity = new AddSongToPlaylistActivity(playlistDao, albumTrackDao);
    }

    private Playlist generatePlaylist() {
        Playlist playlist = new Playlist();
        playlist.setId(UUID.randomUUID().toString());
        playlist.setSongList(new ArrayList<>());
        SongModel song = new SongModel();
        song.setAsin("test-asin");
        song.setTrackNumber(1);
        playlist.getSongList().add(song);
        return playlist;
    }

    private Playlist generatePlaylistWithNAlbumTracks(int n) {
        Playlist playlist = new Playlist();
        playlist.setId(UUID.randomUUID().toString());
        playlist.setSongList(new ArrayList<>());
        for (int i = 0; i < n; i++) {
            SongModel song = new SongModel();
            song.setAsin("test-asin-" + i);
            song.setTrackNumber(i + 1);
            playlist.getSongList().add(song);
        }
        return playlist;
    }

    private AlbumTrack generateAlbumTrack(int trackNumber) {
        AlbumTrack albumTrack = new AlbumTrack();
        albumTrack.setAsin("test-asin-" + trackNumber);
        albumTrack.setTrackNumber(trackNumber);
        return albumTrack;
    }

    @Test
    void handleRequest_validRequest_addsSongToEndOfPlaylist() {
        // GIVEN
        Playlist originalPlaylist = generatePlaylist();
        String playlistId = originalPlaylist.getId();

        AlbumTrack albumTrackToAdd = generateAlbumTrack(2);

        // Convertir l'AlbumTrack en SongModel explicitement
        SongModel songToAdd = new SongModel();
        songToAdd.setAsin(albumTrackToAdd.getAsin());
        songToAdd.setTrackNumber(albumTrackToAdd.getTrackNumber());

        when(playlistDao.getPlaylist(playlistId)).thenReturn(originalPlaylist);
        when(playlistDao.savePlaylist(any())).thenReturn(originalPlaylist);
        when(albumTrackDao.getAlbumTrack(albumTrackToAdd.getAsin(), albumTrackToAdd.getTrackNumber()))
                .thenReturn(albumTrackToAdd);

        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
                .withId(playlistId)
                .withAsin(albumTrackToAdd.getAsin())
                .withTrackNumber(albumTrackToAdd.getTrackNumber())
                .build();

        // WHEN
        AddSongToPlaylistResult result = addSongToPlaylistActivity.handleRequest(request, null);

        // THEN
        assertEquals(2, result.getSongList().size());
        SongModel secondSong = result.getSongList().get(1);
        assertEquals(albumTrackToAdd.getAsin(), secondSong.getAsin());
        assertEquals(albumTrackToAdd.getTrackNumber(), secondSong.getTrackNumber());
    }

    @Test
    public void handleRequest_noMatchingPlaylistId_throwsPlaylistNotFoundException() {
        // GIVEN
        String playlistId = "missing id";
        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
                .withId(playlistId)
                .withAsin("asin")
                .withTrackNumber(1)
                .build();
        when(playlistDao.getPlaylist(playlistId)).thenThrow(new PlaylistNotFoundException());

        // WHEN + THEN
        assertThrows(PlaylistNotFoundException.class, () ->
                addSongToPlaylistActivity.handleRequest(request, null)
        );
    }

    @Test
    public void handleRequest_noMatchingAlbumTrack_throwsAlbumTrackNotFoundException() {
        // GIVEN
        Playlist playlist = generatePlaylist();
        String playlistId = playlist.getId();

        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
                .withId(playlistId)
                .withAsin("nonexistent asin")
                .withTrackNumber(-1)
                .build();

        when(playlistDao.getPlaylist(playlistId)).thenReturn(playlist);
        when(albumTrackDao.getAlbumTrack("nonexistent asin", -1)).thenReturn(null);

        // THEN
        assertThrows(AlbumTrackNotFoundException.class, () ->
                addSongToPlaylistActivity.handleRequest(request, null)
        );
    }

    @Test
    void handleRequest_validRequestWithQueueNextFalse_addsSongToEndOfPlaylist() {
        // GIVEN
        int startingTrackCount = 3;
        Playlist originalPlaylist = generatePlaylistWithNAlbumTracks(startingTrackCount);
        String playlistId = originalPlaylist.getId();

        AlbumTrack albumTrackToAdd = generateAlbumTrack(8);

        // Convertir l'AlbumTrack en SongModel explicitement
        SongModel songToAdd = new SongModel();
        songToAdd.setAsin(albumTrackToAdd.getAsin());
        songToAdd.setTrackNumber(albumTrackToAdd.getTrackNumber());

        when(playlistDao.getPlaylist(playlistId)).thenReturn(originalPlaylist);
        when(playlistDao.savePlaylist(any())).thenReturn(originalPlaylist);
        when(albumTrackDao.getAlbumTrack(albumTrackToAdd.getAsin(), albumTrackToAdd.getTrackNumber()))
                .thenReturn(albumTrackToAdd);

        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
                .withId(playlistId)
                .withAsin(albumTrackToAdd.getAsin())
                .withTrackNumber(albumTrackToAdd.getTrackNumber())
                .withQueueNext(false)
                .build();

        // WHEN
        AddSongToPlaylistResult result = addSongToPlaylistActivity.handleRequest(request, null);

        // THEN
        assertEquals(startingTrackCount + 1, result.getSongList().size());
        SongModel lastSong = result.getSongList().get(result.getSongList().size() - 1);
        assertEquals(albumTrackToAdd.getAsin(), lastSong.getAsin());
        assertEquals(albumTrackToAdd.getTrackNumber(), lastSong.getTrackNumber());
    }

    @Test
    void handleRequest_validRequestWithQueueNextTrue_addsSongToBeginningOfPlaylist() {
        // GIVEN
        int startingPlaylistSize = 2;
        Playlist originalPlaylist = generatePlaylistWithNAlbumTracks(startingPlaylistSize);
        String playlistId = originalPlaylist.getId();

        AlbumTrack albumTrackToAdd = generateAlbumTrack(6);

        // Convertir l'AlbumTrack en SongModel explicitement
        SongModel songToAdd = new SongModel();
        songToAdd.setAsin(albumTrackToAdd.getAsin());
        songToAdd.setTrackNumber(albumTrackToAdd.getTrackNumber());

        when(playlistDao.getPlaylist(playlistId)).thenReturn(originalPlaylist);
        when(playlistDao.savePlaylist(any())).thenReturn(originalPlaylist);
        when(albumTrackDao.getAlbumTrack(albumTrackToAdd.getAsin(), albumTrackToAdd.getTrackNumber()))
                .thenReturn(albumTrackToAdd);

        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
                .withId(playlistId)
                .withAsin(albumTrackToAdd.getAsin())
                .withTrackNumber(albumTrackToAdd.getTrackNumber())
                .withQueueNext(true)
                .build();

        // WHEN
        AddSongToPlaylistResult result = addSongToPlaylistActivity.handleRequest(request, null);

        // THEN
        assertEquals(startingPlaylistSize + 1, result.getSongList().size());
        SongModel firstSong = result.getSongList().get(0);
        assertEquals(albumTrackToAdd.getAsin(), firstSong.getAsin());
        assertEquals(albumTrackToAdd.getTrackNumber(), firstSong.getTrackNumber());
    }
}