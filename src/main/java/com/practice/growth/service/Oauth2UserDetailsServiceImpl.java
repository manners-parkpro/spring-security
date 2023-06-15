package com.practice.growth.service;

import com.practice.growth.authentication.PrincipalDetails;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.types.ProviderType;
import com.practice.growth.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class Oauth2UserDetailsServiceImpl extends DefaultOAuth2UserService {

    private final AccountRepository repository;
    @Value("${aes256.iv-key}")
    private String ivKey;

    // Oauth2 Login 후처리 되는 되는 Method
    // 함수 종료 시 @AuthenticationPrincipal 애노테이션이 만들어 진다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("oauth loadUser");

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Account account = createOAuthUser(userRequest, oAuth2User);
        return new PrincipalDetails(account, oAuth2User.getAttributes());
    }

    @Transactional
    public Account createOAuthUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String providerType = userRequest.getClientRegistration().getRegistrationId();
        String provider = userRequest.getClientRegistration().getClientId();
        String providerId = oAuth2User.getAttribute("sub");


        Account account;
        Optional<Account> optAccount = repository.findByProviderId(providerId);
        if (optAccount.isEmpty()) {
            account = new Account();
            account.setEmail(oAuth2User.getAttribute("email"));
            account.setUsername(provider + "_" + providerId);
            account.setRole("ROLE_USER");

            if (ProviderType.GOOGLE.getDesc().equals(providerType))
                account.setProvider(ProviderType.GOOGLE);
            else
                account.setProvider(ProviderType.NAVER);

            account.setProviderId(oAuth2User.getAttribute("sub"));
        } else {
            account = optAccount.get();
            account.setLastLoginAt(LocalDateTime.now());
        }

        return repository.save(account);
    }
}