package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.udg.pds.springtodo.entity.Hashtag;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.service.HashtagService;

import javax.servlet.http.HttpSession;
import java.util.Collection;

@RequestMapping(path="/hashtags")
@RestController
public class HashtagController extends BaseController {
    @Autowired
    HashtagService hashtagService;

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

    @GetMapping(path="/{id}/publications")
    @JsonView(Views.Public.class)
    public Collection<Publication> getPublications(HttpSession session, @PathVariable("id") Long id){
        getLoggedUser(session);
        return hashtagService.getHashtag(id).getPublications();
    }
}
