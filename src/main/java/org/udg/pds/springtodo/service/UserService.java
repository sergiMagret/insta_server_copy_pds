package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Task;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public UserRepository crud() {
    return userRepository;
  }

  public User matchPassword(String username, String password) {

    List<User> uc = userRepository.findByUsername(username);

    if (uc.size() == 0) throw new ServiceException("User does not exists");

    User u = uc.get(0);

    if (u.getPassword().equals(password))
      return u;
    else
      throw new ServiceException("Password does not match");
  }

  public User register(String username, String name, String email, String password, String description, String profilePicture) {

   List<User> uEmail = userRepository.findByEmail(email);
    if (uEmail.size() > 0)
      throw new ServiceException("Email already exist");


    List<User> uUsername = userRepository.findByUsername(username);
    if (uUsername.size() > 0)
      throw new ServiceException("Username already exists");

    User nu = new User(username, name, email, password, description, profilePicture);
    userRepository.save(nu);
    return nu;
  }

  public User getUser(Long id) {
    Optional<User> uo = userRepository.findById(id);
    if (uo.isPresent())
      return uo.get();
    else
      throw new ServiceException(String.format("User with id = % dos not exists", id));
  }

  public User getUserProfile(long id) {
    User u = this.getUser(id);
    for (Task t : u.getTasks())
        t.getTags();
    return u;
  }

  public Collection<User> getUsers(String text, Integer page, Integer size) {
      Pageable p = PageRequest.of(page, size);
      String filter = "%" + text + "%";
      return userRepository.getFiltered(filter, p);
  }

    public void addFollowed(Long userId, Long followedId) {
        Optional<User> ou = userRepository.findById(userId);
        Optional<User> of = userRepository.findById(followedId);
        if(!ou.isPresent()){
            throwUserDoesNotExist(userId);
        }
        if(!of.isPresent()) {
            throwUserDoesNotExist(followedId);
        }
        if(userId.equals(followedId)) {
            throw new ServiceException("You can't follow yoursef!");
        }
        ou.get().addFollowed(of.get()); // Add user
        userRepository.save(ou.get()); // Update the DB
    }

    public void deleteFollowed(Long userId, Long followedId){
        Optional<User> ou = userRepository.findById(userId);
        Optional<User> of = userRepository.findById(followedId);
        if(!ou.isPresent()){
            throwUserDoesNotExist(userId);
        }
        if(!of.isPresent()) {
            throwUserDoesNotExist(followedId);
        }
        if(userId.equals(followedId)) {
            throw new ServiceException("You can't unfollow yoursef!");
        }
        ou.get().deleteFollowed(of.get()); // Add user
        userRepository.save(ou.get()); // Update the DB
    }

    public Collection<User> getFollowers(Long userId){
        Optional<User> ou = userRepository.findById(userId);
        if(!ou.isPresent()){
            throwUserDoesNotExist(userId);
        }
        return ou.get().getFollowers();
    }

    public Collection<User> getFollowed(Long userId){
        Optional<User> ou = userRepository.findById(userId);
        if(!ou.isPresent()){
            throwUserDoesNotExist(userId);
        }
        return ou.get().getFollowed();
    }

  public Collection<User> getUsers(){
    return userRepository.getAll();
  }

  private void throwUserDoesNotExist(Long userId){
      throw new ServiceException("User with id " + userId + " does not exist!");
  }
}
