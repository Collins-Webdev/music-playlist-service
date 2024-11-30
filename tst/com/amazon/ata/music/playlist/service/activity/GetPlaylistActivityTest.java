//package com.amazon.ata.music.playlist.service.activity;
//
//import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
//import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
//import com.amazon.ata.music.playlist.service.models.requests.GetPlaylistRequest;
//import com.amazon.ata.music.playlist.service.models.results.GetPlaylistResult;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//import static org.mockito.MockitoAnnotations.initMocks;
//
//public class GetPlaylistActivityTest {
//    @Mock
//    private PlaylistDao playlistDao;
//
//    private GetPlaylistActivity getPlaylistActivity;
//
//    @BeforeEach
//    public void setUp() {
//        initMocks(this);
//        getPlaylistActivity = new GetPlaylistActivity(playlistDao);
//    }
//
//    @Test
//    public void handleRequest_savedPlaylistFound_returnsPlaylistModelInResult() {
//        // GIVEN
//        String expectedId = "expectedId";
//        String expectedName = "expectedName";
//        String expectedCustomerId = "expectedCustomerId";
//        int expectedSongCount = 0;
//        List<String> expectedTags = Lists.newArrayList("tag");
//
//        Playlist playlist = new Playlist();
//        playlist.setId(expectedId);
//        playlist.setName(expectedName);
//        playlist.setCustomerId(expectedCustomerId);
//        playlist.setSongCount(expectedSongCount);
//        playlist.setTags(Sets.newHashSet(expectedTags));
//
//        when(playlistDao.getPlaylist(expectedId)).thenReturn(playlist);
//
//        GetPlaylistRequest request = GetPlaylistRequest.builder()
//            .withId(expectedId)
//            .build();
//
//        // WHEN
//        GetPlaylistResult result = getPlaylistActivity.handleRequest(request, null);
//
//        // THEN
//        assertEquals(expectedId, result.getPlaylist().getId());
//        assertEquals(expectedName, result.getPlaylist().getName());
//        assertEquals(expectedCustomerId, result.getPlaylist().getCustomerId());
//        assertEquals(expectedSongCount, result.getPlaylist().getSongCount());
//        assertEquals(expectedTags, result.getPlaylist().getTags());
//    }
//}
