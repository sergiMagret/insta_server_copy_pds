package org.udg.pds.springtodo.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Hashtag;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.entity.User;


import java.util.Collection;
import java.util.List;

@Component
public interface UserRepository extends CrudRepository<User, Long> {
    @Query("SELECT u FROM users u WHERE u.username=:username")
    List<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM users u WHERE u.email=:email")
    List<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM users u ORDER BY u.username")
    List<User> getAll();

    @Query("SELECT u FROM users u WHERE u.username LIKE :text OR u.name LIKE :text ORDER BY u.username")
    List<User> getFiltered(String text, Pageable pageable);

    @Query("SELECT u FROM  users u WHERE  :usuari member of u.followed ORDER BY u.name")
    List <User> getFollowers(User usuari, Pageable p);

    @Query("SELECT u FROM  users u WHERE  :usuari member of u.followers ORDER BY u.name")
    List <User> getFollowed(User usuari, Pageable p);

}
