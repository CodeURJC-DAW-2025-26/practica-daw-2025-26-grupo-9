package es.urjc.daw.equis.dto;

public class CurrentUserDTO {

    private Long id;
    private String name;
    private String email;
    private boolean active;
    private String nickname;

    public CurrentUserDTO(Long id, String name, String email, boolean active, String nickname) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.active = active;
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActive() {
        return active;
    }
}