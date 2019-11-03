package com.j24.security.template.repository;

import com.j24.security.template.model.GroupUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupUnit, Long> {


    List<GroupUnit> findAllByGroupManager_Username(String name);

    boolean existsByIdAndGroupManager_Username(Long groupId, String username);
}
