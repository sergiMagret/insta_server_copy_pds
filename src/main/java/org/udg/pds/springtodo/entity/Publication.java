package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.udg.pds.springtodo.serializer.JsonDateDeserializer;
import org.udg.pds.springtodo.serializer.JsonDateSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.text.SimpleDateFormat;


@Entity(name = "publications")
// This tells JAXB that it has to ignore getters and setters and only use fields for JSON marshaling/unmarshaling
public class Publication implements Serializable {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1L;

    public Publication(){

    }

    public Publication(String photo, String description, Date date){
        this.photo = photo;
        this.description = description;
        this.date = date;
    }


    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String photo;

    private String description;

    private Date date;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> likes = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private Collection <User> taggedUsers;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_user")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publication")
    private Collection<Comment> comments;

    @Column(name = "fk_user", insertable = false, updatable = false)
    private Long userId;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Hashtag> hashtags = new HashSet<>(); // Set because a publication can't have the same hashtag more than once.

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
    public String getPhoto() {
        return photo;
    }

    @JsonView(Views.Public.class)
    public String getDescription() {
        return description;
    }

    @JsonView(Views.Public.class)
    public int getLikes() {
        return likes.size();
    }

    @JsonView(Views.Public.class)
    public Collection<User> getTaggedUsers(){
        return taggedUsers;
    }

    @JsonView(Views.Public.class)
    public void tagUser(User u){
        taggedUsers.add(u);
    }

    @JsonView(Views.Public.class)
    public boolean alreadyTagged(User u){
        return taggedUsers.contains(u);
    }

    public void addHashtag(Hashtag h){
        this.hashtags.add(h);
    }

    @JsonView(Views.Public.class)
    public Collection<String> getHashtags(){
        List<String> hs = new ArrayList<>();
        for(Hashtag h : hashtags){
            hs.add(h.getName());
        }

        return hs;
    }

    @JsonView(Views.Public.class)
    public int getNComments() {
        return comments.size();
    }

    @JsonIgnore
    public Collection<Comment> getComments(){ return comments;}

    @JsonView(Views.Public.class)
    public void addLike(User u) {
        this.likes.add(u);
    }

    @JsonView(Views.Public.class)
    public void delLikes(User u) {
        this.likes.remove(u);
    }

    @JsonView(Views.Public.class)
    public int nTaggedUsers() {
        return this.taggedUsers.size();
    }

    @JsonView(Views.Public.class)
    public boolean hasLiked(User u){
        boolean trobat = false;
        if (likes.contains(u)){
            trobat = true;
        }
        return trobat;
    }



    @JsonView(Views.Public.class)
    public long getUserId() {
        return userId;
    }

    @JsonView(Views.Public.class)
    @JsonSerialize(using = JsonDateSerializer.class)
    @JsonDeserialize(as= JsonDateDeserializer.class)
    public Date getDate() {
        return date;
    }


}


