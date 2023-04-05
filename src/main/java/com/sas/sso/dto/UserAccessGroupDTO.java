package com.sas.sso.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UserAccessGroupDTO {

    @Valid
    @NotEmpty
    List<Long> accessGroupIds = new ArrayList<>();

    @NotNull
    Long userId;
}
