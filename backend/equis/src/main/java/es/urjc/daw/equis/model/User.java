package es.urjc.daw.equis.model;

import java.sql.Blob;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String nickname;
    private String description;
    private String email;
    private String encodedPassword;

    @Transient
    private boolean currentAdmin;

    private boolean active = true;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private Blob profilePicture;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private Blob coverPicture;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public User() {
    }

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

    public boolean isCurrentAdmin() {
        return currentAdmin;
    }

    public void setCurrentAdmin(boolean currentAdmin) {
        this.currentAdmin = currentAdmin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public Blob getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Blob profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Blob getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(Blob coverPicture) {
        this.coverPicture = coverPicture;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getPassword() {
        return encodedPassword;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
