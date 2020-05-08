package org.udg.pds.springtodo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Hashtag;

import java.util.List;

@Component
public interface HashtagRepository extends CrudRepository<Hashtag, Long> {
    @Query("SELECT h FROM hashtags h WHERE h.name=:hName")
    List<Hashtag> findByName(@Param("hName") String name);

    @Query("SELECT h FROM hashtags h")
    List<Hashtag> getAll();
}
