package com.movetogether.modules.club;

import com.movetogether.modules.tag.QTag;
import com.movetogether.modules.tag.Tag;
import com.movetogether.modules.zone.QZone;
import com.movetogether.modules.zone.Zone;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

public class ClubRepositoryExtensionImpl extends QuerydslRepositorySupport implements ClubRepositoryExtension{

    public ClubRepositoryExtensionImpl() {
        super(Club.class);
    }

    @Override
    public List<Club> findByAccount(Set<Tag> tags, Set<Zone> zones) {
        QClub club = QClub.club;
        JPQLQuery<Club> query = from(club).where(club.published.isTrue()
                        .and(club.closed.isFalse())
                        .and(club.tags.any().in(tags))
                        .and(club.zones.any().in(zones)))
                .leftJoin(club.tags, QTag.tag).fetchJoin()
                .leftJoin(club.zones, QZone.zone).fetchJoin()
                .orderBy(club.publishedDateTime.desc())
                .distinct()
                .limit(9);

        return query.fetch();
    }
}
