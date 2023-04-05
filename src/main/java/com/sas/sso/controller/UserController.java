package com.sas.sso.controller;

import com.sas.sso.dto.Response;
import com.sas.sso.dto.UserDto;
import com.sas.sso.serviceimpl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @PostMapping("/createUser")
    public Response createUser(@RequestBody UserDto userDto){
        return userServiceImpl.createUser(userDto);
    }

    @PutMapping("/updateUser")
    public Response updateUser(@RequestParam UserDto userDto){
        return userServiceImpl.updateUser(userDto);
    }

    @GetMapping("/getUser")
    public Response findByIdOrNameOrEmail(@RequestParam(required = false) Long id, @RequestParam(required = false) String name,@RequestParam(required = false) String email){
        return userServiceImpl.findByIdOrNameOrEmail(id,name,email);
    }
}
