package com.arishi.AXAM.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="oauth_accounts")
@Getter
@Setter
public class OAuthAccount extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String provider;


    private String providerUserId;


    private String providerEmail;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private Users user;

}
