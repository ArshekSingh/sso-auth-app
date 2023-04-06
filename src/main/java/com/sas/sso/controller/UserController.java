package com.sas.sso.controller;

import com.sas.sso.dto.Response;
import com.sas.sso.dto.UserDto;
import com.sas.sso.request.UserRequest;
import com.sas.sso.service.UserService;
import com.sas.sso.serviceimpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/createUser")
    public Response createUser(@RequestBody UserDto userDto){
        return userService.createUser(userDto);
    }

    @PutMapping("/updateUser")
    public Response updateUser(@RequestParam UserDto userDto){
        return userService.updateUser(userDto);
    }

    @GetMapping("/getUser")
    public Response findByIdOrNameOrEmail(@RequestBody UserRequest request){
            return userService.findAllFilterData(request);
    }
}
