package com.movetogether.modules.acount;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {
    
    @Id @GeneratedValue
    private Long id;
    
    @Column(unique = true)
    private String email;
    
    @Column(unique = true)
    private String nickname;
    
    private boolean emailVerified;
    
    private String emailCheckToken;
    
    private LocalDateTime joinedAt;
    
    private String bio;
    
    private String url;
    
    private String occupation;
    
    private String location;
    
    @Lob @Basic(fetch = FetchType.EAGER)
    private String profileImage;

    private boolean moveCreatedByEmail;

    private boolean moveCreatedByWeb;

    private boolean moveEnrollmentResultByEmail;

    private boolean moveEnrollmentResultByWeb;

    private boolean moveUpdatedByEmail;

    private boolean moveUpdatedByWeb;
}