package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(name = "comment")
public class Comment implements Serializable {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public Comment(){

    }
    public Comment(String comentari){
        text=comentari;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cm_user")
    private User user;

    @Column(name = "cm_user", insertable = false, updatable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_publication")
    private Publication publication;

    @Column(name = "fk_publication", insertable = false, updatable = false)
    private Long publicationId;

    @JsonView(Views.Public.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonView(Views.Public.class)
    public String getUserUsername() {
        return user.getUsername();
    }

    @JsonView(Views.Public.class)
    public String getText() {
        return text;
    }

    public void setText(String text){ this.text = text;}

    @JsonIgnore
    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    @JsonIgnore
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }



}
