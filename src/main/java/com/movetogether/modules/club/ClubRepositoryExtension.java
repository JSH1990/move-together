package com.movetogether.modules.club;

import com.movetogether.modules.tag.Tag;
import com.movetogether.modules.zone.Zone;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface ClubRepositoryExtension {

    List<Club> findByAccount(Set<Tag> tags, Set<Zone> zones);
}
