package com.amazon.ata.music.playlist.service.helpers;

import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.models.SongModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class AlbumTrackTestHelper {
    private AlbumTrackTestHelper() {
    }

    public static AlbumTrack generateAlbumTrack(int sequenceNumber) {
        AlbumTrack albumTrack = new AlbumTrack();
        albumTrack.setAsin("asin" + sequenceNumber);
        albumTrack.setTrackNumber(sequenceNumber);
        albumTrack.setAlbumName("album" + sequenceNumber);
        albumTrack.setSongTitle("title" + sequenceNumber);
        return albumTrack;
    }

    public static void assertAlbumTracksEqualSongModels(List<AlbumTrack> albumTracks, List<SongModel> songModels) {
        assertEquals(albumTracks.size(),
                songModels.size(),
                String.format("Expected album tracks (%s) and song models (%s) to match",
                        albumTracks,
                        songModels));
        for (int i = 0; i < albumTracks.size(); i++) {
            assertAlbumTrackEqualsSongModel(
                    albumTracks.get(i),
                    songModels.get(i),
                    String.format("Expected %dth album track (%s) to match corresponding song model (%s)",
                            i,
                            albumTracks.get(i),
                            songModels.get(i)));
        }
    }

    public static void assertAlbumTrackEqualsSongModel(AlbumTrack albumTrack, SongModel songModel) {
        String message = String.format("Expected album track %s to match song model %s", albumTrack, songModel);
        assertAlbumTrackEqualsSongModel(albumTrack, songModel, message);
    }

    public static void assertAlbumTrackEqualsSongModel(AlbumTrack albumTrack, SongModel songModel, String message) {
        assertEquals(albumTrack.getAsin(), songModel.getAsin(), message);
        assertEquals(albumTrack.getTrackNumber(), songModel.getTrackNumber(), message);
        assertEquals(albumTrack.getAlbumName(), songModel.getAlbum(), message);
        assertEquals(albumTrack.getSongTitle(), songModel.getTitle(), message);
    }
}
