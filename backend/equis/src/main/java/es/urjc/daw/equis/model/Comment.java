package es.urjc.daw.equis.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Comment {

@Id
@GeneratedValue(strategy = GenerationType.AUTO )
private long id;

private String content;

private int likes;

private LocalDateTime date;

public Comment(){}

public Comment(String content, int likes ,LocalDateTime date){

    this.content =  content;
    this.likes = likes;
    this.date = date;

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

}