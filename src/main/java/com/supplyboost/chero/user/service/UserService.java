package com.supplyboost.chero.user.service;


import com.supplyboost.chero.event.UserRegisteredEventProducer;
import com.supplyboost.chero.event.payload.UserRegisteredEvent;
import com.supplyboost.chero.notification.service.NotificationService;
import com.supplyboost.chero.web.dto.AdminUserEditRequest;
import com.supplyboost.chero.web.dto.LoginRequest;
import com.supplyboost.chero.web.dto.RegisterRequest;
import com.supplyboost.chero.web.dto.UserEditRequest;
import com.supplyboost.chero.exception.DomainException;
import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.subscription.service.SubscriptionService;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.model.UserRole;
import com.supplyboost.chero.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SubscriptionService subscriptionService;
    private final CharacterService characterService;
    private final NotificationService notificationService;
    private final UserRegisteredEventProducer userRegisteredEventProducer;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SubscriptionService subscriptionService, CharacterService characterService, NotificationService notificationService, UserRegisteredEventProducer userRegisteredEventProducer){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
        this.characterService = characterService;
        this.notificationService = notificationService;
        this.userRegisteredEventProducer = userRegisteredEventProducer;
    }

    public void save(User user) {
        userRepository.save(user);
    }


    public User login(LoginRequest loginRequest) throws DomainException{
        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());

        if(optionalUser.isEmpty()){
            throw new DomainException("Username or password are incorrect");
        }

        User user = optionalUser.get();

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new DomainException("Username or password are incorrect");
        }

        return user;
    }

    @Transactional
    public User register(RegisterRequest registerRequest) throws DomainException{

        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());

        if(userOptional.isPresent()){
            throw new DomainException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user = userRepository.save(initializeUser(registerRequest));

        //To be added in the User model if needed
//        subscriptionService.createDefaultSubscription(user);
        GameCharacter gameCharacter = characterService.createGameCharacter(user);

        notificationService.saveNotificationPreference(user.getId(), true, user.getEmail());


//        UserRegisteredEvent event = UserRegisteredEvent.builder()
//                .userId(user.getId())
//                .createdOn(user.getCreatedOn())
//                .build();
//        userRegisteredEventProducer.sendEvent(event);


        notificationService.sendGreetings(user.getId(), user.getUsername());

        log.info("Successfully create new user account [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        user.setGameCharacter(gameCharacter);
        userRepository.save(user);

        return user;
    }

    public User findByUsername(String username) throws DomainException{
        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isEmpty()){
            throw new DomainException("Username [%s] does not exist please contact administrator.".formatted(username));
        }

        return userOptional.get();
    }

    public User getById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new DomainException("User with id [%s] does not exist please contact administrator.".formatted(id)));
    }


    public void editUserDetails(UUID userId, UserEditRequest userEditRequest, MultipartFile profilePictureFile){
        User user = getById(userId);

        if (profilePictureFile != null && !profilePictureFile.isEmpty()) {
            try {
                File uploadDir = new File("src/main/resources/static/uploads/");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String contentType = profilePictureFile.getContentType();
                assert contentType != null;
                if (!(contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/webp"))) {

                }else{
                    String fileName = UUID.randomUUID() + "." + contentType.split("/")[1];
                    Path filePath = Paths.get(uploadDir + "/" + fileName);
                    Files.write(filePath, profilePictureFile.getBytes());

                    user.setProfile_picture("/uploads/" + fileName);
                }


            } catch (IOException e) {

            }
        }

        user.setEmail(userEditRequest.getEmail());
        user.getGameCharacter().setNickName(userEditRequest.getCharacterName());


        characterService.editCharacterName(user.getGameCharacter().getId(), userEditRequest.getCharacterName());
        save(user);
        characterService.save(user.getGameCharacter());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException("User with this username does not exist."));

        return new AuthenticationMetadata(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getUserRole(),
                user.isActive());
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public void adminUpdateUser(UUID userId, AdminUserEditRequest editRequest) {
        User user = getById(userId);

        user.setUserRole(editRequest.getUserRole());
        user.setEmail(editRequest.getEmail());
        user.getGameCharacter().setNickName(editRequest.getCharacterName());
        if(!user.getEmail().equals("admin@underground.com")){
            save(user);
            characterService.save(user.getGameCharacter());

            log.info("Admin [%s] successfully updated user [%s]".formatted(user.getUsername(), editRequest.getCharacterName()));
        }
        log.info("Admin [%s] failed to updated The Moderator [%s]".formatted(user.getUsername(), editRequest.getCharacterName()));

    }

    private User initializeUser(RegisterRequest registerRequest){

        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .userRole(UserRole.ROLE_USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }



}
