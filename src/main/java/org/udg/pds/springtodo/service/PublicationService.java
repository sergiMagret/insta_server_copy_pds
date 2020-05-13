package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.repository.PublicationRepository;


import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PublicationService {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    PublicationRepository publicationRepository;

    @Autowired
    PublicationService publicationService;
    @Autowired
    UserService userService;
    @Autowired
    HashtagService hashtagService;

    public PublicationRepository crud() {
        return publicationRepository;
    }

    public Collection<Publication> getPublications() {
        return publicationRepository.getAll();
    }

    public Collection<Publication> getUserPublications(Long userId, Integer page, Integer size) {
        Pageable p = PageRequest.of(page, size);
        Optional<User> ou = userService.crud().findById(userId);
        if(!ou.isPresent()) throw new ServiceException("User does not exist!");
        return publicationRepository.getAllfromUser(ou.get(), p);
    }

    public Collection<Publication> getHashtagPublications(Long id,Integer page,Integer size){
        Pageable p = PageRequest.of(page, size);
        Optional<Hashtag> oh = hashtagService.crud().findById(id);
        if(!oh.isPresent()) throw new ServiceException("User does not exist!");
        return publicationRepository.getAllfromHastag(oh.get().getPublications(), p);
    }

    public IdObject addPublication (Publication p) {
        publicationRepository.save(p);
        return new IdObject(p.getId());
    }
    public Publication getPublication(Long id) {
        Optional<Publication> po = publicationRepository.findById(id);
        if (po.isPresent())
            return po.get();
        else
            throw new ServiceException(String.format("Publication with id = % dos not exists", id));
    }

    public Publication addLike (Long userId, Long publicationId){
        Optional<User> ou = userService.crud().findById(userId);
        if(!ou.isPresent()) throw new ServiceException("User does not exist!");

        Optional<Publication> pb = publicationService.crud().findById(publicationId);
        if(!pb.isPresent()){
            throw new ServiceException("Publication does not exist!");
        }
        if(! pb.get().hasLiked(ou.get())){
            pb.get().addLike(ou.get());
        }
        this.publicationRepository.save(pb.get());
        return pb.get();
    }

    public Publication deleteLike (Long userId, Long publicationId){
        Optional<User> ou = userService.crud().findById(userId);
        if(!ou.isPresent()) throw new ServiceException("User does not exist!");

        Optional<Publication> pb = publicationService.crud().findById(publicationId);
        if(!pb.isPresent()){
            throw new ServiceException("Publication does not exist!");
        }
        pb.get().delLikes(ou.get());
        this.publicationRepository.save(pb.get());
        return pb.get();
    }

    public Collection<Publication> publicationsFollowed(Collection<User> followed, Integer page, Integer size){
        Pageable p = PageRequest.of(page, size);
        return publicationRepository.getFollowersPublications(followed, p);
    }

    public void addHashtagTo(Publication p, Hashtag h){
        // These two lines are added to check if both the hashtag and the publication exists.
        hashtagService.getHashtag(h.getId());
        publicationService.getPublication(p.getId());

        p.addHashtag(h);
        publicationRepository.save(p);
    }

}
