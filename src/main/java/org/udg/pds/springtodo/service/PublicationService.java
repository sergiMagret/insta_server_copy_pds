package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.repository.PublicationRepository;


import java.util.Collection;
import java.util.Optional;

@Service
public class PublicationService {
    @Autowired
    PublicationRepository publicationRepository;

    @Autowired
    UserService userService;

    public PublicationRepository crud() {
        return publicationRepository;
    }

    public Collection<Publication> getPublications() {
        return publicationRepository.getAll();
    }
}
