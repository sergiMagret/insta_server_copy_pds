package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.entity.User;
import org.udg.pds.springtodo.entity.Views;
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

    public int tagUser (String userName, Long publicationId){
        Optional<Publication> pb = publicationService.crud().findById(publicationId);
        if(!pb.isPresent()){
            throw new ServiceException("Publication does not exist!");
        }
        int resultat = 0;
        List<User> u = userService.crud().findByUsername(userName);
        boolean jaEtiquetat = pb.get().alreadyTagged(u.get(0));
        if(pb.get().nTaggedUsers()>=20){
            resultat = 1;
        }
        if(jaEtiquetat == true){
            resultat = 2;
        }
        else {
            if(u.size()>0) {
                pb.get().tagUser(u.get(0));
                this.publicationRepository.save(pb.get());
            }
        }
        return resultat;
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

}
