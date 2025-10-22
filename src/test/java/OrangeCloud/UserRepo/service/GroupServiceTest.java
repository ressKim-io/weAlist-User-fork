package OrangeCloud.UserRepo.service;

import OrangeCloud.UserRepo.entity.Group;
import OrangeCloud.UserRepo.repository.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    @DisplayName("그룹 생성 성공")
    void createGroup_success() {
        // given
        String name = "Test Group";
        String companyName = "Test Company";
        Group group = Group.builder()
                .name(name)
                .companyName(companyName)
                .isActive(true)
                .build();

        when(groupRepository.save(any(Group.class))).thenReturn(group);

        // when
        Group createdGroup = groupService.createGroup(name, companyName);

        // then
        assertThat(createdGroup).isNotNull();
        assertThat(createdGroup.getName()).isEqualTo(name);
        assertThat(createdGroup.getCompanyName()).isEqualTo(companyName);
        verify(groupRepository).save(any(Group.class));
    }
}
