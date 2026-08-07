package com.arishi.AXAM.model;

import jakarta.persistence.*;
import lombok.Data;


import java.time.Instant;

@Entity
@Table(name = "login_history")
@Data
public class LoginHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginType;

    private String provider;

    private String ipAddress;

    private String deviceInfo;

    private String browser;

    private String os;

    private String loginStatus;

    private String failureReason;

    private Instant loginTime;

    private Instant logoutTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

}
