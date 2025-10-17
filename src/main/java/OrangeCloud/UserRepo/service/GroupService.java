package OrangeCloud.UserRepo.service;

import OrangeCloud.UserRepo.entity.Group;
import OrangeCloud.UserRepo.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GroupService {
    
    @Autowired
    private GroupRepository groupRepository;
    
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }
    
    public Optional<Group> getGroupById(UUID id) {
        return groupRepository.findById(id);
    }
    
    public Group saveGroup(Group group) {
        return groupRepository.save(group);
    }
    
    public void deleteGroup(UUID id) {
        groupRepository.deleteById(id);
    }
}