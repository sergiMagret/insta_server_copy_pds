package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.entity.Comment;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.repository.CommentRepository;
import org.udg.pds.springtodo.repository.PublicationRepository;

import java.text.SimpleDateFormat;
import java.util.Collection;

@Service
public class CommentService {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PublicationService publicationService;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    public CommentRepository crud() {
        return commentRepository;
    }

    public IdObject addComment(Comment c){
        commentRepository.save(c);
        return new IdObject(c.getId());
    }

    public Collection<Comment> getComments(Long publicationId,Integer page, Integer size){
        Pageable p = PageRequest.of(page, size);
        return commentRepository.getComments(publicationId,p);
    }


}
