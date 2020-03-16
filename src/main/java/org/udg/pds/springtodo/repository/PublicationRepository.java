package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Publication;


import java.util.List;

@Component
public interface PublicationRepository extends CrudRepository<Publication, Long> {
    /* @Query("SELECT p FROM publications p WHERE p.id=:id")
    List<Publication> findByID(@Param("id") Long id); */

    @Query ("SELECT p FROM publications p")
    List<Publication> getAll();
}

