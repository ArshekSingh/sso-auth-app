package com.sas.sso.service;

import com.sas.sso.dto.AccessGroupDTO;
import com.sas.sso.dto.Response;
import com.sas.sso.dto.UserAccessGroupDTO;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
public interface AccessGroupService {

	Response findAll(int pageNo, int pageSize);

	Response addNewAccessGroup(AccessGroupDTO dto);

	Response updateAccessGroup(AccessGroupDTO dto);

	Response updateUserAccessGroup(@Valid UserAccessGroupDTO userAccessGroupDTO);

	Response getUserAccessGroups(Long userId);

	Response getCompanyDetails();

	Response getAppDetails(Long compId);

}
