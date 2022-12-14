package common;

import java.io.Serial;
import java.io.Serializable;

public class Song implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    private final int id;
    private final String title;
    private final String author;
    private final Integer year;
    private final String album;
    private final String genre;
    private final Integer duration;

    public Song() {
        this.id = 0;
        this.title = "";
        this.author = "";
        this.year = null;
        this.album = "";
        this.genre = "";
        this.duration = null;
    }

    public Song(int id, String title, String author, int year, String album, String genre, Integer duration) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.album = album;
        this.genre = genre;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return (id + "\t" + year + "\t" + author + "\t" + title + "\t" + album + "\t" + genre + "\t" + duration);
    }

    public String getTitle() {

        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public int getYear() {
        return this.year;
    }

    public String getAlbum() {
        return this.album;
    }

    public String getGenre() {
        return this.genre;
    }

    public Integer getDuration() {
        return this.duration;
    }
}
