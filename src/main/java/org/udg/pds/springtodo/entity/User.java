package org.udg.pds.springtodo.entity;

import com.fasterxml.jackson.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.udg.pds.springtodo.service.PublicationService;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

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

    public User(String username, String name, String email, String password, String description, String profilePicture) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.description = description;
        this.profilePicture = profilePicture;
        this.publications = new ArrayList<>();
        this.followsUser = false;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String username;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private String profilePicture;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private Boolean followsUser;
    /* followsUser is modified by the method isFollowedBy(User u), this variable is used to know if the logged user is following the asked user. */
    /* Let's say you are the logged user A, and you request for the profile of user B, then if you are following B, the value of followsUser will be true */
    /* because you (A) are following (B). */

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Task> tasks;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Publication> publications;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<Comment> Comments;

    // Use Set<> to avoid duplicates. A group cannot be owned more than once
    @OneToMany(mappedBy = "owner")
    private Set<Group> ownedGroups = new HashSet<>();

    // Use Set<> to avoid duplicates. A member cannot be duplicated in a group
    @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL)
    private Set<Group> memberGroups = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "followed")
    private List<User> followers = new ArrayList<>(); // Using a Set because a user can't be followed more than once by the same user

    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> followed = new ArrayList<>(); // Using a Set because a user can't follow more than once the same user

   //I use a set to avoid dupliccates as a user can't like a photo more tha once
    @ManyToMany(mappedBy = "likes", cascade = CascadeType.ALL)
    private Set<Publication> liked = new HashSet<>();

    @JsonView(Views.Public.class)
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

    @JsonView(Views.Public.class)
    public void SetName(String newName){
        this.name = newName;
    }

    @JsonView(Views.Public.class)
    public String getName(){
        return name;
    }

    @JsonView(Views.Public.class)
    public void SetDesc(String newDesc){
        this.description = newDesc;
    }

    @JsonView(Views.Public.class)
    public String getDescription(){
        return description;
    }

    @JsonView(Views.Public.class)
    public void SetPicture(String newPic){
        this.profilePicture = newPic;
    }

    @JsonView(Views.Public.class)
    public String getProfilePicture(){
        return profilePicture;
    }

    // The number of people you follow or the number of people who follows you is public
    @JsonView(Views.Public.class)
    public Integer getNumberFollowers(){
        return followers.size();
    }

    @JsonView(Views.Public.class)
    public Integer getNumberFollowed(){
        return followed.size();
    }

    // The exact list of people you follow, or who follows you shouldn't be serialized when serializing the User, there is a URI that allows you to get this.
    @JsonIgnore
    @JsonView(Views.Public.class)
    public List<User> getFollowers(){
        return followers;
    }

    @JsonIgnore
    @JsonView(Views.Public.class)
    public List<User> getFollowed(){
        return followed;
    }

    @JsonIgnore
    @JsonView(Views.Public.class)
    public List<User> getFollowedPage(Pageable pageable){
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<User> list;

        if (followed.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, followed.size());
            list = followed.subList(startItem, toIndex);
        }

        return list;
    }

    @JsonIgnore
    @JsonView(Views.Public.class)
    public List<User> getFollowersPage(Pageable pageable){
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<User> list;

        if (followers.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, followers.size());
            list = followers.subList(startItem, toIndex);
        }
        return list;
    }


    @JsonIgnore
    public void isFollowedBy(User u) {
        followsUser = followers.contains(u);
    }

    @JsonView(Views.Public.class)
    public Boolean getFollowsUser() {
        return this.followsUser;
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

    @JsonIgnore // You only want the full publications list when it's necessary, when serializing the user, it's not necessary.
    public Collection<Publication> getPublications(){
        // Since publications is collection controlled by JPA, it has LAZY loading by default. That means
        // that you have to query the object (calling size(), for example) to get the list initialized
        // More: http://www.javabeat.net/jpa-lazy-eager-loading/
        publications.size();

        return publications;
    }

    @JsonView(Views.Public.class)
    public Integer getNumberPublications(){
        return this.publications.size();
    }

    public void addPublication(Publication p){
        this.publications.add(p);
    }

    public void addFollower(User u) {
        this.followers.add(u);
    }

    public void addFollowed(User u) {
        this.followed.add(u);
    }

    public void deleteFollowed(User u){
        this.followed.remove(u);
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
