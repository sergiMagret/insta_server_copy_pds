package org.udg.pds.springtodo.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.udg.pds.springtodo.entity.Group;
import org.udg.pds.springtodo.entity.Task;

@Component
public interface GroupRepository extends CrudRepository<Group, Long> {}
