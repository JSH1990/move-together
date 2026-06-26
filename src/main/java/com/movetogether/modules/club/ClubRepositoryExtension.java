package com.movetogether.modules.club;
import com.movetogether.modules.tag.Tag;
import com.movetogether.modules.zone.Zone;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface ClubRepositoryExtension {

    Page<Club> findByKeyword(String keyword, Pageable pageable);

    List<Club> findByAccount(Set<Tag> tags, Set<Zone> zones);
}
