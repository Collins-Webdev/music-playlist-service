package com.amazon.ata.music.playlist.service.models.requests;

import java.util.Objects;

public class AddSongToPlaylistRequest {
    private String id;
    private String asin;
    private int trackNumber;
    private boolean queueNext;

    public AddSongToPlaylistRequest() {
    }

    public AddSongToPlaylistRequest(String id, String asin, int trackNumber, boolean queueNext) {
        this.id = id;
        this.asin = asin;
        this.trackNumber = trackNumber;
        this.queueNext = queueNext;
    }

    public AddSongToPlaylistRequest(Builder builder) {
        this.asin = builder.asin;
        this.id = builder.id;
        this.trackNumber = builder.trackNumber;
        this.queueNext = builder.queueNext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public boolean isQueueNext() {
        return queueNext;
    }

    public void setQueueNext(boolean queueNext) {
        this.queueNext = queueNext;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddSongToPlaylistRequest that = (AddSongToPlaylistRequest) o;
        return trackNumber == that.trackNumber &&
                queueNext == that.queueNext &&
                Objects.equals(id, that.id) &&
                Objects.equals(asin, that.asin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, asin, trackNumber, queueNext);
    }

    @Override
    public String toString() {
        return "AddSongToPlaylistRequest{" +
                "id='" + id + '\'' +
                ", asin='" + asin + '\'' +
                ", trackNumber=" + trackNumber +
                ", queueNext=" + queueNext +
                '}';
    }

    public static Builder builder() {return new Builder();}

    public String getPlaylistId() {
        return "";
    }

    public static final class Builder {
        private String id;
        private String asin;
        private int trackNumber;
        private boolean queueNext;

        private Builder() {

        }

        public Builder withId(String idToUse) {
            this.id = idToUse;
            return this;
        }

        public Builder withAsin(String asinToUse) {
            this.asin = asinToUse;
            return this;
        }

        public Builder withTrackNumber(int trackNumberToUse) {
            this.trackNumber = trackNumberToUse;
            return this;
        }

        public Builder withQueueNext(boolean queueNextToUse) {
            this.queueNext = queueNextToUse;
            return this;
        }

        public AddSongToPlaylistRequest build() { return new AddSongToPlaylistRequest(this); }
    }
}
