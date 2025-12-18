package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("/profile")
public class ProfileController {
    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping
    public ResponseEntity<Profile> getProfilee(Principal principal) {
        try {
            if (principal == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            String username = principal.getName();
            int userId = userDao.getIdByUsername(username);

            Profile profile = profileDao.getByUserId(userId);

            if(profile == null)
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok(profile);
        }
        catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }


    @PutMapping
    public ResponseEntity<Void> updateProfile(
            Principal principal,
            @RequestBody Profile profile)
    {
        try
        {
            if (principal == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            String username = principal.getName();
            int userId = userDao.getIdByUsername(username);

            Profile existing = profileDao.getByUserId(userId);
            if (existing == null)
                return ResponseEntity.notFound().build();

            // Prevent spoofing
            profile.setUserId(userId);

            profileDao.update(userId, profile);
            return ResponseEntity.noContent().build();
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
