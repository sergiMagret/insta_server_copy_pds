package org.udg.pds.springtodo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.udg.pds.springtodo.controller.exceptions.ServiceException;
import org.udg.pds.springtodo.entity.*;
import org.udg.pds.springtodo.repository.GroupRepository;
import org.udg.pds.springtodo.repository.GroupRepository;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

@Service
public class GroupService {

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  UserService userService;

  @Autowired
  protected TagService tagService;

  public GroupRepository crud() {
    return groupRepository;
  }

  public Collection<Group> getOwnedGroups(Long userId) {
    return userService.getUser(userId).getOwnedGroups();
  }

    public Collection<Group> getMemberGroups(Long userId) {
        return userService.getUser(userId).getMemberGroups();
    }


    public Group getGroup(Long id) {
      Optional<Group> oGroup = groupRepository.findById(id);
      if (!oGroup.isPresent()) throw new ServiceException("Group does not exists");
//      if (oGroup.get().getOwnerId() != ownerId)
//        throw new ServiceException("User does not own this group");
      return oGroup.get();
  }

    public Group getOwnedGroup(Long ownerId, Long id) {
        Optional<Group> oGroup = groupRepository.findById(id);
        if (!oGroup.isPresent()) throw new ServiceException("Group does not exists");
      if (oGroup.get().getOwnerId() != ownerId)
        throw new ServiceException("User does not own this group");
        return oGroup.get();
    }


    @Transactional
  public IdObject addGroup(Long ownerId, String name, String description) {
    try {
      User user = userService.getUser(ownerId);

      Group group = new Group(name, description);

      group.setOwner(user);
      group.addMember(user);

      user.addOwnedGroup(group);
      user.addMemberGroup(group);

      groupRepository.save(group);
      return new IdObject(group.getId());
    } catch (Exception ex) {
      // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
      // We catch the normal exception and then transform it in a ServiceException
      throw new ServiceException(ex.getMessage());
    }
  }

    public void delete(Long groupId, Long userId) {
      Group group = this.getOwnedGroup(userId, groupId);
      groupRepository.deleteById(groupId);
    }

    @Transactional
    public void addMemberToGroup(Long ownerId, Long userId, Long groupId) {
        Group group = this.getOwnedGroup(ownerId, groupId);
        User member = userService.getUser(userId);

        group.addMember(member);
        member.addMemberGroup(group);
    }

//  @Transactional
//  public void addTagsToTask(Long userId, Long taskId, Collection<Long> tags) {
//    Task t = this.getTask(userId, taskId);
//
//    if (t.getUser().getId() != userId)
//      throw new ServiceException("This user is not the owner of the task");
//
//    try {
//      for (Long tagId : tags) {
//        Optional<Tag> otag = tagService.crud().findById(tagId);
//        if (otag.isPresent())
//          t.addTag(otag.get());
//        else
//          throw new ServiceException("Tag dos not exists");
//      }
//    } catch (Exception ex) {
//      // Very important: if you want that an exception reaches the EJB caller, you have to throw an ServiceException
//      // We catch the normal exception and then transform it in a ServiceException
//      throw new ServiceException(ex.getMessage());
//    }
//  }
//
//  public Collection<Tag> getTaskTags(Long userId, Long id) {
//    Task t = this.getTask(userId, id);
//    User u = t.getUser();
//
//    if (u.getId() != userId)
//      throw new ServiceException("Logged user does not own the task");
//
//    return t.getTags();
//  }

}
