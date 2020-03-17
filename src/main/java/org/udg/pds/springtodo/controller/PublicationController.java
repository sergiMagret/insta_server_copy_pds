package org.udg.pds.springtodo.controller;


import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.service.PublicationService;
import org.udg.pds.springtodo.entity.Views;


import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping(path="/publications")
@RestController
public class PublicationController  extends BaseController{
    @Autowired
    PublicationService publicationService;

    @GetMapping
    @JsonView(Views.Public.class)
    public Collection<Publication> listAllPublications(HttpSession session,
                                                       @RequestParam(value = "from", required = false) Date from) {
        Long userId = getLoggedUser(session);

        return publicationService.getPublications();
    }

    @GetMapping(path="/{id}")
    public Publication getPublication(HttpSession session, @PathVariable("id") Long publicationId) {
         getLoggedUser(session);

         Optional<Publication> op = publicationService.crud().findById(publicationId);
         if(!op.isPresent()){
             throw new ServiceException("Publication does not exist!");
         }
         return op.get();
    }
}

