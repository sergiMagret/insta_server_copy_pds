package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.io.Serializable;

@Entity(name="hashtags")
public class Hashtag implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will.
     **/
    private static final long serialVersionUID = 1L;

    public Hashtag(){
    }

    public Hashtag(String name){
        this.name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "hashtags")
    private Set<Publication> publications = new HashSet<>(); // Set because a publication can't have the same hashtag more than once.

    @JsonView(Views.Public.class)
    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setId(Long id){
        this.id = id;
    }
    @JsonIgnore
    public Long getId(){
        return this.id;
    }

    public void addPublication(Publication p) {
        this.publications.add(p);
    }

    @JsonIgnore
    public Collection<Publication> getPublications(){
        return this.publications;
    }

}
