package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ControllerException;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.service.GroupService;
import org.udg.pds.springtodo.service.PublicationService;
import org.udg.pds.springtodo.service.UserService;

import javax.servlet.http.HttpSession;
import javax.swing.text.View;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

// This class is used to process all the User related URLs
@RequestMapping(path="/users")
@RestController
public class UserController extends BaseController {

  @Autowired
  UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    PublicationService publicationService;

    @PostMapping(path="/login")
  @JsonView(Views.Private.class)
  public User login(HttpSession session, @Valid @RequestBody LoginUser user) {

    checkNotLoggedIn(session);

    User u = userService.matchPassword(user.username, user.password);
    session.setAttribute("simpleapp_auth_id", u.getId());
    return u;
  }

    @GetMapping
    @JsonView(Views.Private.class)
    public Collection<User> listAllTasks(HttpSession session,
                                         @RequestParam String text, @RequestParam Integer page, @RequestParam Integer size){
        Long userId = getLoggedUser(session);

        return userService.getUsers(text, page, size);
    }

  @PostMapping(path="/logout")
  @JsonView(Views.Private.class)
  public String logout(HttpSession session) {

    getLoggedUser(session);

    session.removeAttribute("simpleapp_auth_id");
    return BaseController.OK_MESSAGE;
  }

    /** For the public view, when a user requests to see another user's basic information. **/
    @GetMapping(path="/{id}")
    @JsonView(Views.Public.class)
    public User getPublicUser(HttpSession session, @PathVariable("id") Long userId) {
        getLoggedUser(session);
        return userService.getUser(userId);
    }

    /** For the user who wants to see another user's publications. **/
    @GetMapping(path="/{id}/publications")
    @JsonView(Views.Public.class)
    public Collection<Publication> getUserPublicationsPublic(HttpSession session, @PathVariable("id") Long userId){
        getLoggedUser(session);
        return publicationService.getUserPublications(userId);
    }

    /** For the private view of your own profile, a user wants to see its own profile with all the information. **/
    @GetMapping(path="/self")
    @JsonView(Views.Private.class)
    public User getPrivateUser(HttpSession session){
        Long userId = getLoggedUser(session);
        return userService.getUser(userId);
    }

    /** For the authenticated user who is trying to see his/her own photos. */
    @GetMapping(path="/self/publications")
    @JsonView(Views.Private.class)
    public Collection<Publication> getUserPublicationsPrivate(HttpSession session) {
        Long userId = getLoggedUser(session);
        return publicationService.getUserPublications(userId);
    }

    @GetMapping(path="/{id}/followed")
    @JsonView(Views.Public.class)
    public Collection<User> getFollowedById(HttpSession session, @PathVariable("id") Long userId){
        getLoggedUser(session);
        return userService.getFollowed(userId);
    }

    @GetMapping(path="/{id}/followers")
    @JsonView(Views.Public.class)
    public Collection<User> getFollowersById(HttpSession session, @PathVariable("id") Long userId){
        getLoggedUser(session);
        return userService.getFollowers(userId);
    }

    @GetMapping(path="/self/followed")
    @JsonView(Views.Private.class)
    public Collection<User> getFollowed(HttpSession session){
        Long userId = getLoggedUser(session);
        return userService.getFollowed(userId);
    }

    @GetMapping(path="/self/followers")
    @JsonView(Views.Private.class)
    public Collection<User> getFollowers(HttpSession session){
        Long userId = getLoggedUser(session);
        return userService.getFollowers(userId);
    }

    @PostMapping(path="/self/followed")
    public String addFollowed(HttpSession session, @Valid @RequestBody NewFollow follow){
        Long userId = getLoggedUser(session);
        User u = userService.getUser(userId); // It's not necessary to acces to de id but done to ensure the user exists.
        User f = userService.getUser(follow.user); // It's not necessary to acces to de id but done to ensure the user exists.
        userService.addFollowed(u.getId(), f.getId());

        return BaseController.OK_MESSAGE;
    }

    @DeleteMapping(path="/{id}")
  public String deleteUser(HttpSession session, @PathVariable("id") Long userId) {

    Long loggedUserId = getLoggedUser(session);

    if (!loggedUserId.equals(userId))
      throw new ControllerException("You cannot delete other users!");

    userService.crud().deleteById(userId);
    session.removeAttribute("simpleapp_auth_id");

    return BaseController.OK_MESSAGE;
  }


  @PostMapping(path="/register", consumes = "application/json")
  @JsonView(Views.Private.class)
  public User register(HttpSession session, @Valid  @RequestBody RegisterUser ru) {

    checkNotLoggedIn(session);
    User u = userService.register(ru.username, ru.name, ru.email, ru.password, ru.description, ru.profilePicture);
    session.setAttribute("simpleapp_auth_id", u.getId());
    return u;
  }

  @GetMapping(path="/me")
  @JsonView(Views.Complete.class)
  public User getUserProfile(HttpSession session) {

    Long loggedUserId = getLoggedUser(session);

    return userService.getUserProfile(loggedUserId);
  }

  @GetMapping(path="/check")
  public String checkLoggedIn(HttpSession session) {

    getLoggedUser(session);

    return BaseController.OK_MESSAGE;
  }

    @GetMapping(path = "/me/ownedGroups")
    @JsonView(Views.Private.class)
    public Collection<Group> getOwnedGroups(HttpSession session) {
        Long userId = getLoggedUser(session);

        return groupService.getOwnedGroups(userId);
    }

    @GetMapping(path = "/me/memberGroups")
    @JsonView(Views.Private.class)
    public Collection<Group> getMemberGroups(HttpSession session) {
        Long userId = getLoggedUser(session);

        return groupService.getMemberGroups(userId);
    }




    static class LoginUser {
        @NotNull
        public String username;
        @NotNull
        public String password;
    }

  static class RegisterUser {
    @NotNull
    public String username;
    @NotNull
    public String name;
    @NotNull
    public String email;
    @NotNull
    public String password;
    @NotNull
      public String description;
    @NotNull
      public String profilePicture;
  }


  static class ID {
    public Long id;

    public ID(Long id) {
      this.id = id;
    }
  }

    static class NewFollow{
        public Long user;
    }

}
