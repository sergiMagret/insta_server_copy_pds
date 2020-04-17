package org.udg.pds.springtodo.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.udg.pds.springtodo.entity.Comment;

import java.util.List;


public interface CommentRepository extends CrudRepository<Comment, Long> {

    @Query("SELECT u FROM comment u WHERE u.publicationId=:publicationId ORDER BY u.date")
    List<Comment> getComments(Long publicationId, Pageable pageable);
}
