package com.sas.sso.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserAccessAssignmentDTO {

    Long userId;

    List<AccessGroupDTO> assignedDTOs = new ArrayList<>();

    List<AccessGroupDTO> unAssignedDTOs = new ArrayList<>();
}
