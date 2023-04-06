package com.sas.sso.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sas.sso.dto.AccessDTO;
import com.sas.sso.dto.AccessGroupDTO;
import com.sas.sso.dto.AccessGroupRoleDto;
import com.sas.sso.dto.AppMasterDto;
import com.sas.sso.dto.Response;
import com.sas.sso.dto.RoleDTO;
import com.sas.sso.dto.UserAccessAssignmentDTO;
import com.sas.sso.dto.UserAccessGroupDTO;
import com.sas.sso.entity.AccessGroup;
import com.sas.sso.entity.AccessGroupRoles;
import com.sas.sso.entity.AppMaster;
import com.sas.sso.entity.TokenSession;
import com.sas.sso.entity.User;
import com.sas.sso.repository.AccessGroupRepository;
import com.sas.sso.repository.AccessGroupRoleRepository;
import com.sas.sso.repository.AppMasterRepository;
import com.sas.sso.repository.CompanyMasterRepository;
import com.sas.sso.repository.TokenRedisRepository;
import com.sas.sso.repository.UserRepository;
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
	AccessGroupRoleRepository accessGroupRoleRepository;
	
	@Autowired
	TokenRedisRepository tokenRedisRepository;


	@Override
	public Response findAll(int pageNo, int pageSize) {
		log.info("Fetching Access groups , page {} ,size {}", pageNo, pageSize);
		Pageable paging = PageRequest.of(pageNo, pageSize);
		TokenSession tokenSession = userUtils.getTokenSession();
		if (tokenSession != null) {
			Page<AccessGroup> page = accessGroupRepository.findByCompId(Long.valueOf(tokenSession.getCompanyId()),
					paging);
			AccessDTO dto = accessGroupEntityToDto(page);	
			return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK).data(dto)
					.count(page.getTotalElements()).build();
		} else {
			return Response.builder().code(HttpStatus.BAD_REQUEST.value()).status(HttpStatus.BAD_REQUEST).build();
		}
	}

	private AccessDTO accessGroupEntityToDto(Page<AccessGroup> page) {
		AccessDTO dto=new AccessDTO();
		Page<AccessGroupDTO> accessGroupEntityToDto= page.map(this::convertEntityToDto);
		List<AccessGroupDTO> content = accessGroupEntityToDto.getContent();
		dto.setAccessGroupDTOs(content);
		dto.setOffSet(page.getPageable().getOffset());
		dto.setPageCount(page.getPageable().getPageNumber());
		dto.setPageSize(page.getPageable().getPageSize());
		dto.setTotalPage(page.getTotalPages());
		return dto;
	}

	private AccessGroupDTO convertEntityToDto(AccessGroup accessGroup) {
		AccessGroupDTO dto=new AccessGroupDTO();
		List<AccessGroupRoleDto> roleDtoList=new ArrayList<>();
		dto.setActive(accessGroup.isActive());
		dto.setAppId(accessGroup.getAppId());
		dto.setDescription(accessGroup.getDescription());
		dto.setAccessGroupName(accessGroup.getAccessGroupNo());
		dto.setAccessGroupId(accessGroup.getId());
		dto.setCompId(accessGroup.getCompId());
		for(AccessGroupRoles roles : accessGroup.getAccessGroupRoles()){
			AccessGroupRoleDto roleDto =new AccessGroupRoleDto();
			roleDto.setAccessGroupRoleId(roles.getId());
			roleDto.setRoleName(roles.getRole());
			roleDtoList.add(roleDto);
		}
		dto.setAccessGroupRoleDtos(roleDtoList);
		return dto;
	}


	@Override
	@Transactional(rollbackOn = Throwable.class)
	public Response addNewAccessGroup(AccessGroupDTO dto) {
		
		Optional<TokenSession> tokenSessionOptional=tokenRedisRepository.findByToken(userUtils.getAuthTokenFromRequest().get());
		if (tokenSessionOptional.isPresent()) {
			log.info("Adding new Access group with name {}", dto.getAccessGroupName());
			AccessGroup accessGroup = new AccessGroup();
			accessGroup.setActive(true);
			accessGroup.setCreatedBy(tokenSessionOptional.get().getUserId());
			accessGroup.setCreatedDate(new Date());
			accessGroup.setUpdatedBy(tokenSessionOptional.get().getUserId());
			accessGroup.setUpdatedOn(new Date());
			accessGroup.setEditable(1);
			accessGroup.setName(dto.getAccessGroupName());
			accessGroup.setAppId(dto.getAppId());
			accessGroup.setCompId(dto.getCompId());
			accessGroup.setDescription(dto.getDescription());
			accessGroupRepository.save(accessGroup);
			List<AccessGroupRoles> agrs=new ArrayList<>();
			for (RoleDTO roleDTO : dto.getRoles()) {
				agrs.add(convertRoleToAccessGroupRole(accessGroup, roleDTO,tokenSessionOptional.get()));

			}
			
			accessGroupRoleRepository.saveAll(agrs);
			return Response.builder().code(HttpStatus.CREATED.value()).status(HttpStatus.CREATED)
					.message(HttpStatus.CREATED.getReasonPhrase()).build();
		} else {
			log.info("Access group with name {} already exists !", dto.getAccessGroupName());
			return Response.builder().code(HttpStatus.BAD_REQUEST.value()).status(HttpStatus.BAD_REQUEST)
					.message(String.format("Access Group with name '%s' already exists !", dto.getAccessGroupName()))
					.build();
		}
	}

	AccessGroupRoles convertRoleToAccessGroupRole(AccessGroup accessGroup, RoleDTO roleDTO,TokenSession tokenSession) {
		AccessGroupRoles accessGroupRoles=new AccessGroupRoles();
		accessGroupRoles.setAccessGroup(accessGroup);
		accessGroupRoles.setRole(roleDTO.getRoleName());
		accessGroupRoles.setCreatedBy(tokenSession.getUserId());
		accessGroupRoles.setCreatedDate(new Date());
		return accessGroupRoles;
	}

	@Override
	@Transactional(rollbackOn = Throwable.class)
	public Response updateAccessGroup(AccessGroupDTO dto) {
		Optional<TokenSession> tokenSessionOptional=tokenRedisRepository.findByToken(userUtils.getAuthTokenFromRequest().get());
		Optional<AccessGroup> accessGroupOptional = accessGroupRepository.findById(dto.getAccessGroupId());
		if (tokenSessionOptional.isPresent() && accessGroupOptional.isPresent()) {
			log.info("Adding Access group , id {}", dto.getAccessGroupId());
			AccessGroup accessGroup = accessGroupOptional.get();
			accessGroup.setActive(dto.isActive());
			accessGroup.setUpdatedBy(tokenSessionOptional.get().getUserId());
			accessGroup.setUpdatedOn(new Date());
			accessGroup.setEditable(1);
			accessGroup.setCompId(dto.getCompId());
			accessGroup.setAppId(dto.getAppId());
			accessGroup.setDescription(dto.getDescription());
			// not allowing name change for now , if required logic can be added
			// accessGroup.setName(dto.getAccessGroupName());
			accessGroup.getAccessGroupRoles().clear();
			accessGroup=accessGroupRepository.save(accessGroup);
			List<AccessGroupRoles> agrs=new ArrayList<>();
			for (RoleDTO roleDTO : dto.getRoles()) {
				agrs.add(convertRoleToAccessGroupRole(accessGroup, roleDTO,tokenSessionOptional.get()));

			}
			
			accessGroupRoleRepository.saveAll(agrs);
			
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
					.map(this::getAccessGroupFromDTO).collect(Collectors.toList()));
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
		Optional<List<AppMaster>> appMasterListOptional = appMasterRepository.findByCompanyIdAndActive(compId, true);
		List<AppMasterDto> dtoList = appMasterListOptional.get().stream().map(this::entityToDto)
				.collect(Collectors.toList());
		return Response.builder().code(HttpStatus.OK.value()).status(HttpStatus.OK)
				.message(HttpStatus.OK.getReasonPhrase()).data(dtoList).build();
	}

	private AppMasterDto entityToDto(AppMaster appMaster) {
		AppMasterDto dto = new AppMasterDto();
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
		/*
		 * dto.getRoles()
		 * .addAll(accessGroup.getRoles().parallelStream().map(this::entityToDTORole).
		 * collect(Collectors.toSet()));
		 * 
		 * Set<String> roleGroups = roleRepository.findAllRolesGroups();
		 * 
		 * roleGroups.stream().parallel() .forEach(rg -> dto.getGroupRoles().put(rg,
		 * accessGroup.getRoles().parallelStream().filter(r ->
		 * r.getRoleGroup().equals(rg))
		 * .collect(Collectors.toList()).stream().map(this::entityToDTORole)
		 * .collect(Collectors.toSet())));
		 */

		return dto;
	}

}
