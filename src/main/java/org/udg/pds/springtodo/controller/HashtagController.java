package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.Hashtag;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.HashtagService;
import org.udg.pds.springtodo.service.PublicationService;

import javax.servlet.http.HttpSession;
import java.util.*;

@RequestMapping(path="/hashtags")
@RestController
public class HashtagController extends BaseController {
    @Autowired
    HashtagService hashtagService;

       @Autowired
        PublicationService publicationService;

    @GetMapping
    @JsonView(Views.Public.class)
    public Collection<Hashtag> getHashtags(HttpSession session){
        getLoggedUser(session);
        return hashtagService.getHashtags();
    }

    @GetMapping(path="/{id}")
    @JsonView(Views.Public.class)
    public String getHashtag(HttpSession session, @PathVariable("id") Long id){
        getLoggedUser(session);
        return hashtagService.getHashtag(id).getName();
    }

    @GetMapping(path="/self")
    @JsonView(Views.Public.class)
    public Long getHashtagID(HttpSession session, @RequestParam("name") String name){
        getLoggedUser(session);
        return hashtagService.getHashtagId(name);
    }
    /*
    @GetMapping(path="/{id}/publications")
    @JsonView(Views.Public.class)
    public Collection<Publication> getPublications(HttpSession session, @PathVariable("id") Long id){
        getLoggedUser(session);
        return hashtagService.getHashtag(id).getPublications();
    }*/

   @GetMapping(path="/{id}/publications")
    @JsonView(Views.Public.class)
    public Collection<Publication> getPublications(HttpSession session,@PathVariable("id") Long id,@RequestParam Integer page, @RequestParam Integer size){
        getLoggedUser(session);
        Hashtag h = hashtagService.getHashtag(id);
        return publicationService.getHashtagPublications(id,page,size);
    }

    @GetMapping(path="/self/publications")
    @JsonView(Views.Public.class)
    public Collection<Publication> getPublicationsByName(HttpSession session,@RequestParam("id") String name,@RequestParam Integer page, @RequestParam Integer size){
        getLoggedUser(session);
        Long id = hashtagService.getHashtagId(name);
        Hashtag h = hashtagService.getHashtag(id);
        return publicationService.getHashtagPublications(id,page,size);
    }
}
