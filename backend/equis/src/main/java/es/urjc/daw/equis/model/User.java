package es.urjc.daw.equis.model;

import java.sql.Blob;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String nickname;
    private String description;
    private String email;
    private String encodedPassword;

    @Lob
    private Blob profilePicture;

    @Lob
    private Blob coverPicture;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public User() {}

    public User(String name, String surname, String nickname, String description,
                String email, String encodedPassword, Blob profilePicture,
                Blob coverPicture, String... roles) {

        this.name = name;
        this.surname = surname;
        this.nickname = nickname;
        this.description = description;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.profilePicture = profilePicture;
        this.coverPicture = coverPicture;
        this.roles = List.of(roles);
    }

    // GETTERS / SETTERS

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEncodedPassword() { return encodedPassword; }
    public void setEncodedPassword(String encodedPassword) { this.encodedPassword = encodedPassword; }

    public Blob getProfilePicture() { return profilePicture; }
    public void setProfilePicture(Blob profilePicture) { this.profilePicture = profilePicture; }

    public Blob getCoverPicture() { return coverPicture; }
    public void setCoverPicture(Blob coverPicture) { this.coverPicture = coverPicture; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    public List<Post> getPosts() { return posts; }
    public void setPosts(List<Post> posts) { this.posts = posts; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}
