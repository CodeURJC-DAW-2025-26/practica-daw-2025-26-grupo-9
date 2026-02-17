package es.urjc.daw.equis.model;

import java.time.format.DateTimeFormatter;


import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private int likes;

    @Column(nullable = false)
    private LocalDateTime date;
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("dd-MM-yyyy");


    @Lob
    private Blob picture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public Post() {}

    public Post(String content, int likes, LocalDateTime date, Blob picture) {
        this.content = content;
        this.likes = likes;
        this.date = date;
        this.picture = picture;
    }

    // GETTERS / SETTERS
    @PrePersist
    public void prePersist() {
        this.date = LocalDateTime.now();
}

    public Long getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Blob getPicture() { return picture; }
    public void setPicture(Blob picture) { this.picture = picture; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public String getCreatedAtHuman() {
        return this.date.format(FORMATTER);
    }

    public int getCommentsCount() {
        return this.comments != null ? this.comments.size() : 0;
    }


}
