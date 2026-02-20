package es.urjc.daw.equis.model;

import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 🔹 LIKE A POST
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // 🔹 LIKE A COMMENT
    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public Like() {}

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public Like(User user, Comment comment) {
        this.user = user;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}