package com.movetogether.modules.club;

import com.movetogether.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface ClubRepository extends JpaRepository<Club, Long>, ClubRepositoryExtension {

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Club findByPath(String path);

    List<Club> findByManagersContains(Account account);

    @EntityGraph(attributePaths = "members")
    Club findClubWithMembersByPath(String path);

    @EntityGraph(attributePaths = "managers")
    Club findClubWithManagersByPath(String path);

    Club findClubOnlyByPath(String path);

    @EntityGraph(attributePaths = {"zones", "tags"})
    Club findClubWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"managers", "members"})
    Club findStudyWithManagersAndMembersById(Long id);
}
