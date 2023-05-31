package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.role.RoleDto;
import by.karpovich.SocialMedia.exception.DuplicateException;
import by.karpovich.SocialMedia.exception.NotFoundModelException;
import by.karpovich.SocialMedia.jpa.entity.RoleEntity;
import by.karpovich.SocialMedia.jpa.repository.RoleRepository;
import by.karpovich.SocialMedia.mapping.RoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleEntity saveRole(RoleDto dto) {
        validateAlreadyExists(dto, null);

        log.info("method saveRole - Role with name = {} saved", dto.getName());
        return roleRepository.save(roleMapper.mapEntityFromDto(dto));
    }

    @Override
    public Set<RoleEntity> findRoleByName(String roleName) {
        Optional<RoleEntity> role = roleRepository.findByName(roleName);

        var roleEntity = role.orElseThrow(
                () -> new NotFoundModelException(String.format("Role with name = %s not found", role)));

        Set<RoleEntity> userRoles = new HashSet<>();
        userRoles.add(roleEntity);

        log.info("method findRoleByName - Role with name = {} find", role);
        return userRoles;
    }

    @Override
    public RoleDto findRoleById(Long id) {
        var role = roleRepository.findById(id).orElseThrow(
                () -> new NotFoundModelException(String.format("Role with id = %s not found", id)));

        log.info("method findRoleById - Role found with id = {} ", role.getId());
        return roleMapper.mapDtoFromEntity(role);
    }

    @Override
    public List<RoleDto> findRolesAll() {
        List<RoleEntity> roles = roleRepository.findAll();

        log.info("method findRolesAll - the number of roles found  = {} ", roles.size());
        return roleMapper.mapListDtoFromListEntity(roles);
    }

    @Override
    @Transactional
    public RoleDto updateRoleById(Long id, RoleDto dto) {
        validateAlreadyExists(dto, id);

        var entity = roleMapper.mapEntityFromDto(dto);
        entity.setId(id);
        var updated = roleRepository.save(entity);

        log.info("method updateRoleById - Role {} updated", updated.getName());
        return roleMapper.mapDtoFromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteRoleById(Long id) {
        if (roleRepository.findById(id).isPresent()) {
            roleRepository.deleteById(id);
        } else {
            throw new NotFoundModelException(String.format("Role with id = %s not found", id));
        }
        log.info("method deleteRoleById - Role with id = {} deleted", id);
    }

    private void validateAlreadyExists(RoleDto dto, Long id) {
        Optional<RoleEntity> role = roleRepository.findByName(dto.getName());

        if (role.isPresent() && !role.get().getId().equals(id)) {
            throw new DuplicateException(String.format("Role with name = %s already exist", dto.getName()));
        }
    }
}
