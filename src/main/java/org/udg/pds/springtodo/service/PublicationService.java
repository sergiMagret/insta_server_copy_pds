package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.repository.PublicationRepository;


import java.util.Collection;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public PublicationRepository crud() {
        return publicationRepository;
    }

    public Collection<Publication> getPublications() {
        return publicationRepository.getAll();
    }

    public Collection<Publication> getUserPublications(Long userId) {
        Optional<User> ou = userService.crud().findById(userId);
        if(!ou.isPresent()) throw new ServiceException("User does not exist!");
        return ou.get().getPublications();
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

}
