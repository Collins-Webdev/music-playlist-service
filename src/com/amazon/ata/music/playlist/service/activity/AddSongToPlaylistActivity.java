package com.amazon.ata.music.playlist.service.activity;

import com.amazon.ata.music.playlist.service.models.requests.AddSongToPlaylistRequest;
import com.amazon.ata.music.playlist.service.models.results.AddSongToPlaylistResult;
import com.amazon.ata.music.playlist.service.models.SongModel;
import com.amazon.ata.music.playlist.service.dynamodb.AlbumTrackDao;
import com.amazon.ata.music.playlist.service.dynamodb.PlaylistDao;
import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;
import com.amazon.ata.music.playlist.service.dynamodb.models.Playlist;
import com.amazon.ata.music.playlist.service.converters.ModelConverter;
import com.amazon.ata.music.playlist.service.exceptions.AlbumTrackNotFoundException;
import com.amazon.ata.music.playlist.service.exceptions.PlaylistNotFoundException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddSongToPlaylistActivity implements RequestHandler<AddSongToPlaylistRequest, AddSongToPlaylistResult> {
    private final Logger log = LogManager.getLogger();
    private final PlaylistDao playlistDao;
    private final AlbumTrackDao albumTrackDao;
    private final ModelConverter modelConverter;

    public AddSongToPlaylistActivity(PlaylistDao playlistDao, AlbumTrackDao albumTrackDao) {
        this.playlistDao = playlistDao;
        this.albumTrackDao = albumTrackDao;
        this.modelConverter = new ModelConverter();
    }

    @Override
    public AddSongToPlaylistResult handleRequest(final AddSongToPlaylistRequest addSongToPlaylistRequest, Context context) {
        log.info("Received AddSongToPlaylistRequest {} ", addSongToPlaylistRequest);

        if (addSongToPlaylistRequest == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        // Retrieve the playlist, throw exception if not found
        Playlist playlist = playlistDao.getPlaylist(addSongToPlaylistRequest.getId());
        if (playlist == null) {
            throw new PlaylistNotFoundException("Playlist not found with ID: " + addSongToPlaylistRequest.getId());
        }

        // Retrieve the album track, throw exception if not found
        AlbumTrack albumTrack = albumTrackDao.getAlbumTrack(
                addSongToPlaylistRequest.getAsin(),
                addSongToPlaylistRequest.getTrackNumber()
        );
        if (albumTrack == null) {
            throw new AlbumTrackNotFoundException("Album track not found with ASIN: " +
                    addSongToPlaylistRequest.getAsin() + " and Track Number: " +
                    addSongToPlaylistRequest.getTrackNumber());
        }

        // Create a new list of songs if the current list is null
        List<AlbumTrack> songList = (playlist.getSongList() != null)
                ? new ArrayList<>(playlist.getSongList())
                : new ArrayList<>();

        // Add the song based on queueNext parameter
        if (addSongToPlaylistRequest.isQueueNext()) {
            songList.add(0, albumTrack);
        } else {
            songList.add(albumTrack);
        }

        playlist.setSongList(songList);

        // Save the updated playlist
        playlistDao.savePlaylist(playlist);

        // Convert the updated song list to SongModels
        List<SongModel> updatedSongList = songList.stream()
                .map(modelConverter::toSongModel)
                .collect(Collectors.toList());

        return AddSongToPlaylistResult.builder()
                .withSongList(updatedSongList)
                .build();
    }
}