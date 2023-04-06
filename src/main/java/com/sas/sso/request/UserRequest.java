package com.sas.sso.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {

    private String firstName;
    private String email;
    private Long compId;
    private Long appId;
    private int startIndex;
    private int endIndex;
}
