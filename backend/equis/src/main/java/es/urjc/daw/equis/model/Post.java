package es.urjc.daw.equis.model;

import java.sql.Blob;
import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String content;

    private int likes;

    private LocalDateTime date;

    @Lob
    private Blob picture;

    public Post() {
        // Empty constructor required by JPA
    }

    public Post(String content, int likes, LocalDateTime date, Blob picture) {
        this.content = content;
        this.likes = likes;
        this.date = date;
        this.picture = picture;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Blob getPicture() {
        return picture;
    }

    public void setPicture(Blob picture) {
        this.picture = picture;
    }

}
