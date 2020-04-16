package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.controller.exceptions.ControllerException;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.service.GroupService;
import org.udg.pds.springtodo.service.PublicationService;
import org.udg.pds.springtodo.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Optional;

@RequestMapping(path="/publications")
@RestController
public class PublicationController  extends BaseController{
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    PublicationService publicationService;
    @Autowired
    UserService userService;

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

    @GetMapping(path="/{id}/likes")
    public int getLikes(HttpSession session, @PathVariable("id") Long publicationId) {
        getLoggedUser(session);

        Optional<Publication> op = publicationService.crud().findById(publicationId);
        if(!op.isPresent()){
            throw new ServiceException("Publication does not exist!");
        }
        return op.get().getLikes();
    }

    @PostMapping(path="/{id}/like")
    public Publication addLike(HttpSession session, @PathVariable("id") Long publicationId){
        Long userId = this.getLoggedUser(session);
        Publication pb = publicationService.addLike(userId, publicationId);
        return pb;
    }

    @PostMapping (consumes = "application/json")
    @JsonView(Views.Private.class)
    public String postPublication (HttpSession session,@Valid @RequestBody PublicationPost pub){
        Publication p = new Publication(pub.photo, pub.description, pub.date);
        Long loggedUserId = getLoggedUser(session);
        User u = userService.getUserProfile(loggedUserId);
        p.setUser(u);
        u.addPublication(p);
        publicationService.addPublication(p);
        return BaseController.OK_MESSAGE;
    }

    @DeleteMapping(path="/{id}")
    public String deletePublication(HttpSession session, @PathVariable("id") Long publicationId) {

        publicationService.crud().deleteById(publicationId);
        return BaseController.OK_MESSAGE;
    }

    static class PublicationPost {
        @NotNull
        public String photo;
        @NotNull
        public String description;
        @NotNull
        public Date date;
    }


}

