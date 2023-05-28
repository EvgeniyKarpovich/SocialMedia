package by.karpovich.SocialMedia.api.controller;

import by.karpovich.SocialMedia.api.dto.role.RoleDto;
import by.karpovich.SocialMedia.jpa.entity.RoleEntity;
import by.karpovich.SocialMedia.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins/roles")
@RequiredArgsConstructor
public class AdminRolesController {

    private final RoleService roleService;

    @PostMapping
    public RoleEntity save(@RequestBody RoleDto dto) {
        return roleService.saveRole(dto);
    }

    @GetMapping("/{id}")
    public RoleDto findById(@PathVariable("id") Long id) {
        return roleService.findRoleById(id);
    }

    @GetMapping
    public List<RoleDto> findAll() {
        return roleService.findRolesAll();
    }

    @PutMapping("/{id}")
    public RoleDto update(@RequestBody RoleDto dto,
                          @PathVariable("id") Long id) {
        return roleService.updateRoleById(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        roleService.deleteRoleById(id);
    }
}
