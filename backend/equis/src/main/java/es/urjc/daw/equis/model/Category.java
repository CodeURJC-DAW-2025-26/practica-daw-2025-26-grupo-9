package es.urjc.daw.equis.model;

import java.sql.Blob;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;



public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO )
    private long id;

    private String name;
    private String description;
    @Lob
    private Blob picture;

    public Category (){}

    public Category(String name, String description, Blob picture){

        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Blob getPicture() {
        return picture;
    }
    public void setPicture(Blob picture) {
        this.picture = picture;
    }
}