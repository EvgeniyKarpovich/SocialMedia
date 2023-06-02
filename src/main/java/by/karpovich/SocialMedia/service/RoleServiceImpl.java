package by.karpovich.SocialMedia.service;

import by.karpovich.SocialMedia.api.dto.role.RoleDto;
import by.karpovich.SocialMedia.exception.DuplicateException;
import by.karpovich.SocialMedia.exception.NotFoundModelException;
import by.karpovich.SocialMedia.jpa.entity.RoleEntity;
import by.karpovich.SocialMedia.jpa.repository.RoleRepository;
import by.karpovich.SocialMedia.mapping.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleEntity saveRole(RoleDto dto) {
        validateAlreadyExists(dto, null);

        return roleRepository.save(roleMapper.mapEntityFromDto(dto));
    }

    @Override
    public Set<RoleEntity> findRoleByName(String roleName) {
        Optional<RoleEntity> role = roleRepository.findByName(roleName);

        var roleEntity = role.orElseThrow(
                () -> new NotFoundModelException(String.format("Role with name = %s not found", role)));

        Set<RoleEntity> userRoles = new HashSet<>();
        userRoles.add(roleEntity);

        return userRoles;
    }

    @Override
    public RoleDto findRoleById(Long id) {
        var role = roleRepository.findById(id).orElseThrow(
                () -> new NotFoundModelException(String.format("Role with id = %s not found", id)));

        return roleMapper.mapDtoFromEntity(role);
    }

    @Override
    public List<RoleDto> findRolesAll() {
        List<RoleEntity> roles = roleRepository.findAll();

        return roleMapper.mapListDtoFromListEntity(roles);
    }

    @Override
    @Transactional
    public RoleDto updateRoleById(Long id, RoleDto dto) {
        validateAlreadyExists(dto, id);

        var entity = roleMapper.mapEntityFromDto(dto);
        entity.setId(id);
        var updated = roleRepository.save(entity);

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
    }

    private void validateAlreadyExists(RoleDto dto, Long id) {
        Optional<RoleEntity> role = roleRepository.findByName(dto.getName());

        if (role.isPresent() && !role.get().getId().equals(id)) {
            throw new DuplicateException(String.format("Role with name = %s already exist", dto.getName()));
        }
    }
}
