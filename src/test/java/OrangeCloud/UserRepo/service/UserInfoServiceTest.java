package OrangeCloud.UserRepo.service;

import OrangeCloud.UserRepo.entity.UserInfo;
import OrangeCloud.UserRepo.repository.UserInfoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @Mock
    private UserInfoRepository userInfoRepository;

    @InjectMocks
    private UserInfoService userInfoService;

    @Test
    @DisplayName("유저 정보 생성 성공")
    void createUser_success() {
        // given
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String role = "USER";
        UserInfo userInfo = UserInfo.builder()
                .groupId(groupId)
                .userId(userId)
                .role(role)
                .isActive(true)
                .build();

        when(userInfoRepository.save(any(UserInfo.class))).thenReturn(userInfo);

        // when
        UserInfo createdUserInfo = userInfoService.createUser(groupId, role, userId);

        // then
        assertThat(createdUserInfo).isNotNull();
        assertThat(createdUserInfo.getGroupId()).isEqualTo(groupId);
        assertThat(createdUserInfo.getUserId()).isEqualTo(userId);
        assertThat(createdUserInfo.getRole()).isEqualTo(role);
        verify(userInfoRepository).save(any(UserInfo.class));
    }
}
