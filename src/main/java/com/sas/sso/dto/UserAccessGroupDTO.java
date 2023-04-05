package com.sas.sso.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserAccessGroupDTO {

    @Valid
    @NotEmpty
    Set<Long> accessGroupIds = new HashSet<>();

    @NotNull
    Long userId;
}
