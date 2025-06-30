package com.parkmate.notificationservice.usertoken.application;

import com.parkmate.notificationservice.usertoken.domain.UserToken;
import com.parkmate.notificationservice.usertoken.dto.request.UserTokenSaveRequestDto;
import com.parkmate.notificationservice.usertoken.infrastructure.UserTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {

    private final UserTokenRepository userTokenRepository;

    @Override
    @CacheEvict(value = "userTokens", key = "#userTokenSaveRequestDto.userUuid")
    public void saveToken(UserTokenSaveRequestDto userTokenSaveRequestDto) {
        userTokenRepository.save(userTokenSaveRequestDto.toEntity());
        log.info("User token saved and cache evicted for user: {}", userTokenSaveRequestDto.getUserUuid());
    }

    @Override
    @Cacheable(value = "userTokens", key = "#userUuid", unless = "#result == null")
    public UserToken getTokenByUserUuid(String userUuid) {
        log.debug("Fetching user token from database for user: {}", userUuid);
        return userTokenRepository.findByUserUuid(userUuid);
    }
}
