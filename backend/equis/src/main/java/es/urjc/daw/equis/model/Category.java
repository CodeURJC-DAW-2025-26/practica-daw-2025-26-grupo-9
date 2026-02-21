package es.urjc.daw.equis.model;

import java.sql.Blob;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Lob
    private Blob picture;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Post> posts;

    @Transient
    private long postsCount;

    public Category() {}

    public Category(String name, String description, Blob picture) {
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    // GETTERS / SETTERS

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Blob getPicture() { return picture; }
    public void setPicture(Blob picture) { this.picture = picture; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    public long getPostsCount() { return postsCount; }
    public void setPostsCount(long postsCount) { this.postsCount = postsCount; }
}
