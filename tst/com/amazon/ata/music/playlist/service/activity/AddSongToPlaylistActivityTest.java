//package com.amazon.ata.music.playlist.service.activity;
//
//import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
//import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
//import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
//import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
//import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
//import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;
//import com.amazon.ata.music.playlist.service.models.SongModel;
//import com.amazon.ata.music.playlist.service.models.requests.AddSongToPlaylistRequest;
//import com.amazon.ata.music.playlist.service.models.results.AddSongToPlaylistResult;
//import com.amazon.ata.music.playlist.service.helpers.AlbumTrackTestHelper;
//import com.amazon.ata.music.playlist.service.helpers.PlaylistTestHelper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.mockito.MockitoAnnotations.initMocks;
//
//public class AddSongToPlaylistActivityTest {
//    @Mock
//    private PlaylistDao playlistDao;
//
//    @Mock
//    private AlbumTrackDao albumTrackDao;
//
//    private AddSongToPlaylistActivity addSongToPlaylistActivity;
//
//    @BeforeEach
//    private void setup() {
//        initMocks(this);
//        addSongToPlaylistActivity = new AddSongToPlaylistActivity(playlistDao, albumTrackDao);
//    }
//
//    @Test
//    void handleRequest_validRequest_addsSongToEndOfPlaylist() {
//        // GIVEN
//        // a non-empty playlist
//        Playlist originalPlaylist = PlaylistTestHelper.generatePlaylist();
//        String playlistId = originalPlaylist.getId();
//
//        // the new song to add to the playlist
//        AlbumTrack albumTrackToAdd = AlbumTrackTestHelper.generateAlbumTrack(2);
//        String addedAsin = albumTrackToAdd.getAsin();
//        int addedTracknumber = albumTrackToAdd.getTrackNumber();
//
//        when(playlistDao.getPlaylist(playlistId)).thenReturn(originalPlaylist);
//        when(playlistDao.savePlaylist(originalPlaylist)).thenReturn(originalPlaylist);
//        when(albumTrackDao.getAlbumTrack(addedAsin, addedTracknumber)).thenReturn(albumTrackToAdd);
//
//        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
//            .withId(playlistId)
//            .withAsin(addedAsin)
//            .withTrackNumber(addedTracknumber)
//            .build();
//
//        // WHEN
//        AddSongToPlaylistResult result = addSongToPlaylistActivity.handleRequest(request, null);
//
//        // THEN
//        verify(playlistDao).savePlaylist(originalPlaylist);
//
//        assertEquals(2, result.getSongList().size());
//        SongModel secondSong = result.getSongList().get(1);
//        AlbumTrackTestHelper.assertAlbumTrackEqualsSongModel(albumTrackToAdd, secondSong);
//    }
//
//    @Test
//    public void handleRequest_noMatchingPlaylistId_throwsPlaylistNotFoundException() {
//        // GIVEN
//        String playlistId = "missing id";
//        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
//                                               .withId(playlistId)
//                                               .withAsin("asin")
//                                               .withTrackNumber(1)
//                                               .build();
//        when(playlistDao.getPlaylist(playlistId)).thenThrow(new PlaylistNotFoundException());
//
//        // WHEN + THEN
//        assertThrows(PlaylistNotFoundException.class, () -> addSongToPlaylistActivity.handleRequest(request, null));
//    }
//
//    @Test
//    public void handleRequest_noMatchingAlbumTrack_throwsAlbumTrackNotFoundException() {
//        // GIVEN
//        Playlist playlist = PlaylistTestHelper.generatePlaylist();
//        String playlistId = playlist.getId();
//        String asin = "nonexistent asin";
//        int trackNumber = -1;
//        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
//                                               .withId(playlistId)
//                                               .withAsin(asin)
//                                               .withTrackNumber(trackNumber)
//                                               .build();
//
//        // WHEN
//        when(playlistDao.getPlaylist(playlistId)).thenReturn(playlist);
//        when(albumTrackDao.getAlbumTrack(asin, trackNumber)).thenThrow(new AlbumTrackNotFoundException());
//
//        // THEN
//        assertThrows(AlbumTrackNotFoundException.class, () -> addSongToPlaylistActivity.handleRequest(request, null));
//    }
//
//    @Test
//    void handleRequest_validRequestWithQueueNextFalse_addsSongToEndOfPlaylist() {
//        // GIVEN
//        int startingTrackCount = 3;
//        Playlist originalPlaylist = PlaylistTestHelper.generatePlaylistWithNAlbumTracks(startingTrackCount);
//        String playlistId = originalPlaylist.getId();
//
//        // the new song to add to the playlist
//        AlbumTrack albumTrackToAdd = AlbumTrackTestHelper.generateAlbumTrack(8);
//        String addedAsin = albumTrackToAdd.getAsin();
//        int addedTracknumber = albumTrackToAdd.getTrackNumber();
//
//        when(playlistDao.getPlaylist(playlistId)).thenReturn(originalPlaylist);
//        when(playlistDao.savePlaylist(originalPlaylist)).thenReturn(originalPlaylist);
//        when(albumTrackDao.getAlbumTrack(addedAsin, addedTracknumber)).thenReturn(albumTrackToAdd);
//
//        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
//                                               .withId(playlistId)
//                                               .withAsin(addedAsin)
//                                               .withTrackNumber(addedTracknumber)
//                                               .withQueueNext(false)
//                                               .build();
//
//        // WHEN
//        AddSongToPlaylistResult result = addSongToPlaylistActivity.handleRequest(request, null);
//
//        // THEN
//        verify(playlistDao).savePlaylist(originalPlaylist);
//
//        assertEquals(startingTrackCount + 1, result.getSongList().size());
//        SongModel lastSong = result.getSongList().get(result.getSongList().size() - 1);
//        AlbumTrackTestHelper.assertAlbumTrackEqualsSongModel(albumTrackToAdd, lastSong);
//    }
//
//    @Test
//    void handleRequest_validRequestWithQueueNextTrue_addsSongToBeginningOfPlaylist() {
//        // GIVEN
//        int startingPlaylistSize = 2;
//        Playlist originalPlaylist = PlaylistTestHelper.generatePlaylistWithNAlbumTracks(startingPlaylistSize);
//        String playlistId = originalPlaylist.getId();
//
//        // the new song to add to the playlist
//        AlbumTrack albumTrackToAdd = AlbumTrackTestHelper.generateAlbumTrack(6);
//        String addedAsin = albumTrackToAdd.getAsin();
//        int addedTracknumber = albumTrackToAdd.getTrackNumber();
//
//        when(playlistDao.getPlaylist(playlistId)).thenReturn(originalPlaylist);
//        when(playlistDao.savePlaylist(originalPlaylist)).thenReturn(originalPlaylist);
//        when(albumTrackDao.getAlbumTrack(addedAsin, addedTracknumber)).thenReturn(albumTrackToAdd);
//
//        AddSongToPlaylistRequest request = AddSongToPlaylistRequest.builder()
//                                               .withId(playlistId)
//                                               .withAsin(addedAsin)
//                                               .withTrackNumber(addedTracknumber)
//                                               .withQueueNext(true)
//                                               .build();
//
//        // WHEN
//        AddSongToPlaylistResult result = addSongToPlaylistActivity.handleRequest(request, null);
//
//        // THEN
//        verify(playlistDao).savePlaylist(originalPlaylist);
//
//        assertEquals(startingPlaylistSize + 1, result.getSongList().size());
//        SongModel firstSong = result.getSongList().get(0);
//        AlbumTrackTestHelper.assertAlbumTrackEqualsSongModel(albumTrackToAdd, firstSong);
//    }
//}
