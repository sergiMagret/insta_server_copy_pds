package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.udg.pds.springtodo.serializer.JsonUserSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "groups")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Group.class)
public class Group {
    public Group() {
    }

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // This tells JAXB that this field can be used as ID
    // Since XmlID can only be used on Strings, we need to use LongAdapter to transform Long <-> String
    @Id
    // Don't forget to use the extra argument "strategy = GenerationType.IDENTITY" to get AUTO_INCREMENT
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_owner")
    private User owner;

    @Column(name = "fk_owner", insertable = false, updatable = false)
    private Long ownerId;

    @ManyToMany(cascade = CascadeType.MERGE)
    private Set<User> members = new HashSet<>();

    @JsonView(Views.Private.class)
    public Long getId() {
        return id;
    }

    @JsonIgnore
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void addMember(User u) {
        this.members.add(u);
    }

    @JsonView(Views.Private.class)
    public Collection<User> getMembers() {
        members.size();
        return members;
    }

    @JsonView(Views.Complete.class)
    public long getOwnerId() {
        return ownerId;
    }

    @JsonView(Views.Private.class)
    public String getName() {
        return this.name;
    }

    @JsonView(Views.Private.class)
    public String getDescription() {
        return this.description;
    }

}
