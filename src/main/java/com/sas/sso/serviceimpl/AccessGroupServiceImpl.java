package com.sas.sso.serviceimpl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import com.sas.sso.entity.*;
import com.sas.sso.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sas.sso.dto.AccessGroupDTO;
import com.sas.sso.dto.AppMasterDto;
import com.sas.sso.dto.Response;
import com.sas.sso.dto.RoleDTO;
import com.sas.sso.dto.UserAccessAssignmentDTO;
import com.sas.sso.dto.UserAccessGroupDTO;
import com.sas.sso.service.AccessGroupService;
import com.sas.sso.utils.UserUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccessGroupServiceImpl implements AccessGroupService {

    @Autowired
    AccessGroupRepository accessGroupRepository;

    @Autowired
    UserUtils userUtils;


    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyMasterRepository companyMasterRepository;

    @Autowired
    AppMasterRepository appMasterRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public Response findAll(int pageNo, int pageSize) {
        log.info("Fetching Access groups , page {} ,size {}", pageNo, pageSize);
        Pageable paging = PageRequest.of(pageNo, pageSize);
        TokenSession tokenSession=userUtils.getTokenSession();
        if (tokenSession != null) {
            Page<AccessGroup> page = accessGroupRepository.findByCopmId(Long.valueOf(tokenSession.getCompanyId()), paging);
            return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK).data(page)
                    .count(page.getTotalElements()).build();
        } else {
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    @Transactional(rollbackOn = Throwable.class)
    public Response addNewAccessGroup(AccessGroupDTO dto) {
        if (!accessGroupRepository.existsByName(dto.getAccessGroupName())) {
            log.info("Adding new Access group with name {}", dto.getAccessGroupName());
            AccessGroup accessGroup = new AccessGroup();
            accessGroup.setActive(true);
            accessGroup.setCreatedBy(userUtils.getLoggedUser().getId());
            accessGroup.setCreatedDate(new Date());
            accessGroup.setUpdatedBy(userUtils.getLoggedUser().getId());
            accessGroup.setUpdatedOn(new Date());
            accessGroup.setEditable(1);
            accessGroup.setName(dto.getAccessGroupName());
            accessGroup.setAppId(dto.getAppId());
            accessGroup.setCopmId(dto.getCopmId());
            accessGroup.setDescription(dto.getDescription());
//            accessGroup.setRoles(dto.getRoles().stream().map(this::dtoToEntityRole).collect(Collectors.toSet()));
            accessGroupRepository.save(accessGroup);
            return Response.builder().code(HttpStatus.CREATED.value()).status(HttpStatus.CREATED)
                    .message(HttpStatus.CREATED.getReasonPhrase()).build();
        } else {
            log.info("Access group with name {} already exists !", dto.getAccessGroupName());
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).status(HttpStatus.BAD_REQUEST)
                    .message(String.format("Access Group with name '%s' already exists !", dto.getAccessGroupName()))
                    .build();
        }
    }

    private Role dtoToEntityRole(RoleDTO roleDTO) {
        Optional<Role> roleOptional = roleRepository.findById(roleDTO.getRoleId());
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }
        return null;

    }

    @Override
    @Transactional(rollbackOn = Throwable.class)
    public Response updateAccessGroup(AccessGroupDTO dto) {

        Optional<AccessGroup> accessGroupOptional = accessGroupRepository.findById(dto.getAccessGroupId());
        if (accessGroupOptional.isPresent()) {
            log.info("Adding Access group , id {}", dto.getAccessGroupId());
            AccessGroup accessGroup = accessGroupOptional.get();
            accessGroup.setActive(dto.getActive());
            accessGroup.setUpdatedBy(userUtils.getLoggedUser().getId());
            accessGroup.setUpdatedOn(new Date());
            accessGroup.setEditable(1);
            accessGroup.setCopmId(dto.getCopmId());
            accessGroup.setAppId(dto.getAppId());
            accessGroup.setDescription(dto.getDescription());
            // not allowing name change for now , if required logic can be added
            accessGroup.setName(dto.getAccessGroupName());
            accessGroup.getRoles().clear();
            accessGroup.setRoles(dto.getRoles().stream().map(this::dtoToEntityRole).collect(Collectors.toSet()));
            accessGroupRepository.save(accessGroup);
            return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message(HttpStatus.OK.getReasonPhrase()).build();
        } else {
            log.info("Access group with id {} does not exist !", dto.getAccessGroupId());
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).status(HttpStatus.BAD_REQUEST)
                    .message(String.format("Access Group with id [%s] does not exist !", dto.getAccessGroupId()))
                    .build();
        }
    }

    @Override
    @Transactional(rollbackOn = Throwable.class)
    public Response updateUserAccessGroup(@Valid UserAccessGroupDTO userAccessGroupDTO) {
        Optional<User> userOptional = userRepository.findById(userAccessGroupDTO.getUserId());
        if (userOptional.isPresent()) {
            log.info("Updating User Access groups for id {} ", userAccessGroupDTO.getUserId());
            User user = userOptional.get();
            user.getAccessGroups().clear();
            user.getAccessGroups().addAll(userAccessGroupDTO.getAccessGroupIds().stream()
                    .map(this::getAccessGroupFromDTO).collect(Collectors.toSet()));
            userRepository.save(user);
            return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message(HttpStatus.OK.getReasonPhrase()).build();
        } else {
            log.info("User with id {} does not exist !", userAccessGroupDTO.getUserId());
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).status(HttpStatus.BAD_REQUEST)
                    .message(String.format("User with id [%s] does not exist !", userAccessGroupDTO.getUserId()))
                    .build();
        }
    }

    AccessGroup getAccessGroupFromDTO(Long accessGroupId) {
        Optional<AccessGroup> accessGroupOptional = accessGroupRepository.findById(accessGroupId);
        return accessGroupOptional.orElse(null);
    }

    @Override
    public Response getUserAccessGroups(Long userId) {
        UserAccessAssignmentDTO dto = new UserAccessAssignmentDTO();
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            log.info("Fetching User Access groups for id {} ", userId);
            User user = userOptional.get();
            dto.setAssignedDTOs(
                    user.getAccessGroups().parallelStream().map(this::toAccessGroupDTO).collect(Collectors.toList()));
            dto.setUnAssignedDTOs(accessGroupRepository.findAllUnAssignedGroupByUser(userId).parallelStream()
                    .map(this::toAccessGroupDTO).collect(Collectors.toList()));

            return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK)
                    .message(HttpStatus.OK.getReasonPhrase()).data(dto).build();
        } else {
            log.info("User with id {} does not exist !", userId);
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).status(HttpStatus.BAD_REQUEST)
                    .message(String.format("User with id [%s] does not exist !", userId)).build();
        }
    }

    @Override
    public Response getCompanyDetails() {
        return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK)
                .message(HttpStatus.OK.getReasonPhrase()).data(companyMasterRepository.findByActive(true)).build();
    }

    @Override
    public Response getAppDetails(Long compId) {
        Optional<List<AppMaster>> appMasterListOptional=appMasterRepository.findByCompanyIdAndActive(compId,true);
        List<AppMasterDto> dtoList=appMasterListOptional.get().stream().map(this::entityToDto).collect(Collectors.toList());
        return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK)
                .message(HttpStatus.OK.getReasonPhrase()).data(dtoList).build();
    }

    private AppMasterDto entityToDto(AppMaster appMaster) {
        AppMasterDto dto=new AppMasterDto();
        dto.setApplicationName(appMaster.getApplicationName());
        dto.setBaseUrl(appMaster.getBaseUrl());
        dto.setCompanyId(appMaster.getCompanyId());
        dto.setLogOutUrl(appMaster.getLogoUrl());
        dto.setLogoUrl(appMaster.getLogoUrl());
        dto.isActive();
        return dto;
    }

    AccessGroupDTO toAccessGroupDTO(AccessGroup accessGroup) {
        AccessGroupDTO dto = new AccessGroupDTO();
        dto.setAccessGroupId(accessGroup.getId());
        dto.setAccessGroupName(accessGroup.getName());
        dto.getRoles()
                .addAll(accessGroup.getRoles().parallelStream().map(this::entityToDTORole).collect(Collectors.toSet()));

        Set<String> roleGroups = roleRepository.findAllRolesGroups();

        roleGroups.stream().parallel()
                .forEach(rg -> dto.getGroupRoles().put(rg,
                        accessGroup.getRoles().parallelStream().filter(r -> r.getRoleGroup().equals(rg))
                                .collect(Collectors.toList()).stream().map(this::entityToDTORole)
                                .collect(Collectors.toSet())));

        return dto;
    }

    RoleDTO entityToDTORole(Role role) {
        RoleDTO dto = new RoleDTO();
        dto.setRoleId(role.getId());
        dto.setRoleName(role.getRole());
        return dto;
    }

    @Override
    public Response getRoleGroups() {

        Set<String> roleGroups = roleRepository.findAllRolesGroups();
        LinkedHashMap<String, Set<RoleDTO>> roleGroupDTO = new LinkedHashMap<>();

        for (String roleGroup : roleGroups) {
            roleGroupDTO.put(roleGroup, roleRepository.findByRoleGroup(roleGroup).stream().map(this::entityToDTORole)
                    .collect(Collectors.toSet()));
        }

        return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK).data(roleGroupDTO)
                .message(HttpStatus.OK.getReasonPhrase()).build();

    }

    @Override
    public Response getAllRolesForAccessGroup(Long accessGroupId) {

        Optional<AccessGroup> accessGroupOptional = accessGroupRepository.findById(accessGroupId);
        if (accessGroupOptional.isEmpty()) {
            log.info("Access group with id {} does not exist !", accessGroupId);
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).status(HttpStatus.BAD_REQUEST)
                    .message(String.format("Access Group with id [%s] does not exist !", accessGroupId)).build();
        }
        Set<String> roleGroups = roleRepository.findAllRolesGroups();
        LinkedHashMap<String, Set<RoleDTO>> roleGroupDTO = new LinkedHashMap<>();

        for (String roleGroup : roleGroups) {
            roleGroupDTO.put(roleGroup,
                    accessGroupOptional.get().getRoles().stream().filter(r -> r.getRoleGroup().equals(roleGroup))
                            .map(this::entityToDTORole).collect(Collectors.toSet()));
        }

        return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK).data(roleGroupDTO)
                .message(HttpStatus.OK.getReasonPhrase()).build();

    }
}
