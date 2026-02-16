package es.urjc.daw.equis.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private int likes;
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Comment() {}

    public Comment(String content, int likes, LocalDateTime date) {
        this.content = content;
        this.likes = likes;
        this.date = date;
    }

    // GETTERS / SETTERS

    public Long getId() { return id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
