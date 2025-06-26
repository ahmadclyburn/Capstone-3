package org.yearup.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("profile")
public class ProfileController {
    private UserDao userDao;
    private ProfileDao profileDao;


    public ProfileController(UserDao userDao, ProfileDao profileDao) {
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @GetMapping
    private Profile getProfile(Principal principal){
        try{
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            Profile profile = profileDao.getByUserId(user.getId());
            return profile;

        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }    }
//    private Profile updateProfile(Principal principal){
//
//    }
}
