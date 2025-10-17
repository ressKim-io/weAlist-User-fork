package OrangeCloud.UserRepo.controller;

import OrangeCloud.UserRepo.entity.Group;
import OrangeCloud.UserRepo.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getGroupById(@PathVariable UUID id) {
        Optional<Group> group = groupService.getGroupById(id);
        return group.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Group createGroup(@RequestBody Group group) {
        return groupService.saveGroup(group);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable UUID id, @RequestBody Group groupDetails) {
        Optional<Group> group = groupService.getGroupById(id);
        if (group.isPresent()) {
            Group existingGroup = group.get();
            existingGroup.setName(groupDetails.getName());
            return ResponseEntity.ok(groupService.saveGroup(existingGroup));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        if (groupService.getGroupById(id).isPresent()) {
            groupService.deleteGroup(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}