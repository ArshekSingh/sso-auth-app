package com.sas.sso.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.sas.sso.dto.AccessGroupDTO;
import com.sas.sso.dto.Response;
import com.sas.sso.dto.UserAccessGroupDTO;
import com.sas.sso.repository.TokenRedisRepository;
import com.sas.sso.service.AccessGroupService;

@RestController
public class AccessGroupController {

	@Autowired
	AccessGroupService accessGroupService;

	@Autowired
	TokenRedisRepository repo;

	@GetMapping("/api/access_group/{pageNo}/{pageSize}")
	public Response accessGroups(@PathVariable(value = "pageNo") Integer pageNo,
			@PathVariable(value = "pageSize") Integer pageSize) {

		if (pageSize > 0)
			return accessGroupService.findAll(pageNo, pageSize);
		else
			return Response.builder().code(HttpStatus.BAD_REQUEST.value()).message("Page Size cannot be zero")
					.status(HttpStatus.BAD_REQUEST).build();
	}

	@PostMapping("/api/access_group")
	public Response accessGroups(@Valid @RequestBody AccessGroupDTO accessGroupDTO) {

		return accessGroupService.addNewAccessGroup(accessGroupDTO);

	}

	@PutMapping("/api/access_group")
	public Response accessGroupsUpdate(@Valid @RequestBody AccessGroupDTO accessGroupDTO) {

		return accessGroupService.updateAccessGroup(accessGroupDTO);

	}

	@PutMapping("/api/user_access_group")
	public Response userAccessGroupsUpdate(@Valid @RequestBody UserAccessGroupDTO userAccessGroupDTO) {

		return accessGroupService.updateUserAccessGroup(userAccessGroupDTO);

	}

	@GetMapping("/api/user_access_group/{userId}")
	public Response userAccessGroups(@PathVariable Long userId) {

		return accessGroupService.getUserAccessGroups(userId);

	}

	@GetMapping("/api/company_details")
	public Response getCompanyDetails() {

		return accessGroupService.getCompanyDetails();
	}

	@GetMapping("/api/app_details/{compId}")
	public Response getAppDetails(@PathVariable Long compId) {

		return accessGroupService.getAppDetails(compId);
	}

}
