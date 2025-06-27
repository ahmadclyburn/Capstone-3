package org.yearup.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
    @PutMapping()
    public Profile updateProfile(@RequestBody Profile profile, Principal principal) {
        try {
            System.out.println("Updating profile for user: ");
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            profile.setUserId(user.getId());
            return profileDao.update(profile);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
