package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.entity.User;


import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

@Component
public interface PublicationRepository extends CrudRepository<Publication, Long> {

    @Query ("SELECT p FROM publications p")
    List<Publication> getAll();

    @Query ("SELECT p FROM publications p WHERE p.user IN (:followers) ORDER BY p.date")
    List<Publication> getFollowersPublications(@Param("followers") Collection<User> followers, Pageable p);

    @Query("SELECT p FROM  publications p WHERE  p.user=:user ORDER BY p.date")
    List <Publication> getAllfromUser(User user, Pageable p);
}

