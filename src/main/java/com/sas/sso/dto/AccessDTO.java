package com.sas.sso.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccessDTO {
	
	List<AccessGroupDTO> accessGroupDTOs;
	
	private Long offSet;

    private int pageSize;

    private int pageCount;

    private int totalPage;

}
