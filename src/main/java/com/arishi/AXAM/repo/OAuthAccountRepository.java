package com.arishi.AXAM.repo;


import com.arishi.AXAM.model.OAuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {


    Optional<OAuthAccount> findByProviderAndProviderUserId(String provider, String providerUserId);


    Optional<OAuthAccount> findByProviderAndProviderEmail(String provider, String email);

}
