package com.sas.sso.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppMasterDto {

    private Long companyId;
    private String applicationName;
    private String baseUrl;
    private boolean active;
    private String logoUrl;
    private String logOutUrl;
    private Long appId;
}
