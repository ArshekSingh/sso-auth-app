package com.sas.sso.controller;

import java.util.Optional;

import javax.validation.Valid;

import com.sas.sso.entity.TokenSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.sas.sso.dto.AccessGroupDTO;
import com.sas.sso.dto.Response;
import com.sas.sso.dto.UserAccessGroupDTO;
import com.sas.sso.repository.TokenRedisRepository;
import com.sas.sso.service.AccessGroupService;
import com.sas.sso.utils.UserUtils;

@RestController
public class AccessGroupController {

    @Autowired
    AccessGroupService accessGroupService;

    @Autowired
    TokenRedisRepository repo;
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/api/access_group/{pageNo}/{pageSize}")
    public Response accessGroups(@PathVariable(value = "pageNo") Integer pageNo,
                                 @PathVariable(value = "pageSize") Integer pageSize) {
        
    	if (pageSize > 0)
    		return accessGroupService.findAll(pageNo, pageSize);
        else
            return Response.builder().code(HttpStatus.BAD_REQUEST.value()).message("Page Size cannot be zero")
                    .status(HttpStatus.BAD_REQUEST).build();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/access_group")
    public Response accessGroups(@Valid @RequestBody AccessGroupDTO accessGroupDTO) {

        return accessGroupService.addNewAccessGroup(accessGroupDTO);

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/access_group")
    public Response accessGroupsUpdate(@Valid @RequestBody AccessGroupDTO accessGroupDTO) {

        return accessGroupService.updateAccessGroup(accessGroupDTO);

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/user_access_group")
    public Response userAccessGroupsUpdate(@Valid @RequestBody UserAccessGroupDTO userAccessGroupDTO) {

        return accessGroupService.updateUserAccessGroup(userAccessGroupDTO);

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/user_access_group/{userId}")
    public Response userAccessGroups(@PathVariable Long userId) {

        return accessGroupService.getUserAccessGroups(userId);

    }

    @GetMapping("/company_details")
    public Response getCompanyDetails() {

        return accessGroupService.getCompanyDetails();
    }

    @GetMapping("/app_details/{compId}")
    public Response getAppDetails(@PathVariable Long compId) {

        return accessGroupService.getAppDetails(compId);
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/role_groups")
    public Response roleGroups() {

        return accessGroupService.getRoleGroups();

    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/access_group_role/{accessGroupId}")
    public Response allRolesForAccessGroup(@PathVariable Long accessGroupId) {

        return accessGroupService.getAllRolesForAccessGroup(accessGroupId);

    }
}
