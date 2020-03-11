package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "users")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email", "username"}))
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = User.class)
public class User implements Serializable {
    /**
     * Default value included to remove warning. Remove or modify at will. *
     */
    private static final long serialVersionUID = 1L;

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.tasks = new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Task> tasks;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Publication> publications;

    // Use Set<> to avoid duplicates. A group cannot be owned more than once
    @OneToMany(mappedBy = "owner")
    private Set<Group> ownedGroups = new HashSet<>();

    // Use Set<> to avoid duplicates. A member cannot be duplicated in a group
    @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL)
    private Set<Group> memberGroups = new HashSet<>();

    @JsonView(Views.Private.class)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonView(Views.Private.class)
    public String getEmail() {
        return email;
    }

    @JsonView(Views.Public.class)
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonView(Views.Complete.class)
    public Collection<Task> getTasks() {
        // Since tasks is collection controlled by JPA, it has LAZY loading by default. That means
        // that you have to query the object (calling size(), for example) to get the list initialized
        // More: http://www.javabeat.net/jpa-lazy-eager-loading/
        tasks.size();
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    @JsonView(Views.Private.class)
    public Collection<Group> getOwnedGroups() {
        ownedGroups.size();
        return ownedGroups;
    }

    public void addOwnedGroup(Group g) {
        ownedGroups.add(g);
    }

    @JsonView(Views.Complete.class)
    public Collection<Group> getMemberGroups() {
        memberGroups.size();
        return memberGroups;
    }

    public void addMemberGroup(Group g) {
        memberGroups.add(g);
    }

}
