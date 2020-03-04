package org.udg.pds.springtodo.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.udg.pds.springtodo.entity.IdObject;
import org.udg.pds.springtodo.entity.Tag;
import org.udg.pds.springtodo.entity.Group;
import org.udg.pds.springtodo.entity.Views;
import org.udg.pds.springtodo.serializer.JsonDateDeserializer;
import org.udg.pds.springtodo.service.GroupService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Date;

@RequestMapping(path = "/groups")
@RestController
public class GroupController extends BaseController {

    @Autowired
    GroupService groupService;

    @GetMapping(path = "/{id}")
    public MappingJacksonValue getGroup(HttpSession session,
                                        @PathVariable("id") Long id) {
        Long userId = getLoggedUser(session);

        Group group = groupService.getGroup(id);
        if (group.getOwnerId() == userId)
            return this.toResponse(group, Views.Private.class);
        else
            return this.toResponse(group, Views.Public.class);
    }

    @PostMapping(consumes = "application/json")
    public IdObject addGroup(HttpSession session, @Valid @RequestBody R_Group group) {

        Long userId = getLoggedUser(session);

        return groupService.addGroup(userId, group.name, group.desciption);
    }

    @DeleteMapping(path = "/{id}")
    public String deleteGroup(HttpSession session,
                              @PathVariable("id") Long groupId) {
        Long userId = getLoggedUser(session);
        groupService.delete(groupId, userId);
        return BaseController.OK_MESSAGE;
    }

    @PostMapping(path = "/{groupId}/members/{userId}")
    public void addMemberToGroup(HttpSession session,
                                 @PathVariable("groupId") Long groupId,
                                 @PathVariable("userId") Long userId) {

        Long ownerId = getLoggedUser(session);

        groupService.addMemberToGroup(ownerId, userId, groupId);
    }

    static class R_Group {

        @NotNull
        public String name;

        public String desciption;
    }

}
