package OrangeCloud.UserRepo.service;

import OrangeCloud.UserRepo.dto.team.CreateTeamRequest;
import OrangeCloud.UserRepo.dto.team.TeamWithMembersResponse;
import OrangeCloud.UserRepo.entity.Group;
import OrangeCloud.UserRepo.entity.Team;
import OrangeCloud.UserRepo.entity.UserInfo;
import OrangeCloud.UserRepo.repository.TeamRepository;
import OrangeCloud.UserRepo.repository.UserInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private TeamService teamService;

    private UUID leaderId, memberId;
    private UserInfo leaderInfo, memberInfo;
    private Team team;
    private Group group;

    @BeforeEach
    void setUp() {
        leaderId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        leaderInfo = UserInfo.builder().userId(leaderId).isActive(true).build();
        memberInfo = UserInfo.builder().userId(memberId).isActive(true).build();
        group = Group.builder().groupId(UUID.randomUUID()).companyName("Test Company").build();
        team = Team.builder()
                .teamId(UUID.randomUUID())
                .teamName("Test Team")
                .leaderId(leaderId)
                .groupId(group.getGroupId())
                .build();
    }

    @Test
    @DisplayName("팀 생성 성공")
    void createTeam_success() {
        // given
        CreateTeamRequest request = new CreateTeamRequest("Test Team", "Test Company", "Test Group", leaderId, "A great team");
        when(userInfoRepository.findByUserIdAndIsActiveTrue(leaderId)).thenReturn(Optional.of(leaderInfo));
        when(groupService.findOrCreateGroupByCompanyName(anyString(), anyString())).thenReturn(group);
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        TeamWithMembersResponse response = teamService.createTeam(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTeam().getTeamName()).isEqualTo("Test Team");
        assertThat(response.getMembers().get(0).getUserId()).isEqualTo(leaderId);
        assertTrue(response.getTeam().hasMember(leaderId));
    }

    @Test
    @DisplayName("팀원 추가 성공")
    void addMemberToTeam_success() {
        // given
        when(teamRepository.findById(team.getTeamId())).thenReturn(Optional.of(team));
        when(userInfoRepository.findByUserIdAndIsActiveTrue(memberId)).thenReturn(Optional.of(memberInfo));

        // when
        UserInfo addedMember = teamService.addMemberToTeam(team.getTeamId(), leaderId, memberId, "MEMBER");

        // then
        assertThat(addedMember).isNotNull();
        assertThat(addedMember.getUserId()).isEqualTo(memberId);
        verify(teamRepository).save(team);
        assertTrue(team.hasMember(memberId));
    }

    @Test
    @DisplayName("팀장이 아닌 경우 팀원 추가 시 예외 발생")
    void addMemberToTeam_notLeader() {
        // given
        UUID notLeaderId = UUID.randomUUID();
        when(teamRepository.findById(team.getTeamId())).thenReturn(Optional.of(team));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            teamService.addMemberToTeam(team.getTeamId(), notLeaderId, memberId, "MEMBER");
        });
    }

    @Test
    @DisplayName("팀장 권한 확인 - 팀장인 경우")
    void isTeamLeader_true() {
        // given
        when(teamRepository.findByTeamIdAndIsActiveTrue(team.getTeamId())).thenReturn(Optional.of(team));

        // when
        boolean isLeader = teamService.isTeamLeader(team.getTeamId(), leaderId);

        // then
        assertTrue(isLeader);
    }

    @Test
    @DisplayName("팀 삭제 성공")
    void disbandTeam_success() {
        // given
        when(teamRepository.findByTeamIdAndIsActiveTrue(team.getTeamId())).thenReturn(Optional.of(team));

        // when
        boolean result = teamService.disbandTeam(team.getTeamId(), leaderId);

        // then
        assertTrue(result);
        verify(teamRepository).deleteById(team.getTeamId());
    }

    @Test
    @DisplayName("팀장이 아닌 경우 팀 삭제 시 예외 발생")
    void disbandTeam_notLeader() {
        // given
        UUID notLeaderId = UUID.randomUUID();
        when(teamRepository.findByTeamIdAndIsActiveTrue(team.getTeamId())).thenReturn(Optional.of(team));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            teamService.disbandTeam(team.getTeamId(), notLeaderId);
        });
    }
}
