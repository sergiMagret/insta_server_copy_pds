package org.udg.pds.springtodo.controller;


import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.udg.pds.springtodo.entity.Publication;
import org.udg.pds.springtodo.service.PublicationService;
import org.udg.pds.springtodo.entity.Views;


import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Date;

@RequestMapping(path="/publications")
@RestController
public class PublicationController  extends BaseController{
    @Autowired
    PublicationService publicationService;

    @GetMapping
    @JsonView(Views.Private.class)
    public Collection<Publication> listAllPublications(HttpSession session,
                                                       @RequestParam(value = "from", required = false) Date from) {
        Long userId = getLoggedUser(session);

        return publicationService.getPublications();
    }
}

