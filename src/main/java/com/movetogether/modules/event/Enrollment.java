package com.movetogether.modules.event;

import com.movetogether.modules.account.Account;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@NamedEntityGraph(
    name = "Enrollment.withEventAndClub" ,
        attributeNodes = {
            @NamedAttributeNode(value = "event", subgraph = "club")
        },
        subgraphs = @NamedSubgraph(name = "club", attributeNodes = @NamedAttributeNode("club"))
)
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Enrollment {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private Event event;

    @ManyToOne
    private Account account;

    private LocalDateTime enrolledAt;

    private boolean accepted;

    private boolean attended;
}
