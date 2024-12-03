package com.amazon.ata.music.playlist.service.models;

import com.amazon.ata.music.playlist.service.dynamodb.models.AlbumTrack;

import java.util.Objects;

public class SongModel extends AlbumTrack {
    private String asin;
    private String album;
    private int trackNumber;
    private String title;

    public SongModel() {

    }

    public SongModel(Builder builder) {
        this.asin = builder.asin;
        this.album = builder.album;
        this.trackNumber = builder.trackNumber;
        this.title = builder.title;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongModel songModel = (SongModel) o;
        return trackNumber == songModel.trackNumber &&
                Objects.equals(asin, songModel.asin) &&
                Objects.equals(album, songModel.album) &&
                Objects.equals(title, songModel.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(asin, album, trackNumber, title);
    }

    @Override
    public String toString() {
        return "SongModel{" +
                "asin='" + asin + '\'' +
                ", album='" + album + '\'' +
                ", trackNumber=" + trackNumber +
                ", title='" + title + '\'' +
                '}';
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String asin;
        private String album;
        private int trackNumber;
        private String title;

        public Builder withAsin(String asinToUse) {
            this.asin = asinToUse;
            return this;
        }

        public Builder withAlbum(String albumToUse) {
            this.album = albumToUse;
            return this;
        }

        public Builder withTrackNumber(int trackNumberToUse) {
            this.trackNumber = trackNumberToUse;
            return this;
        }

        public Builder withTitle(String titleToUse) {
            this.title = titleToUse;
            return this;
        }

        // Corrected methods to match ModelConverter usage
        public Builder withAlbumName(String albumName) {
            this.album = albumName;
            return this;
        }

        public Builder withSongTitle(String songTitle) {
            this.title = songTitle;
            return this;
        }

        public SongModel build() {
            return new SongModel(this);
        }
    }
}