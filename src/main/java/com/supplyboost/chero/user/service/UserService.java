package com.supplyboost.chero.user.service;


import com.supplyboost.chero.dto.LoginRequest;
import com.supplyboost.chero.dto.RegisterRequest;
import com.supplyboost.chero.dto.UserEditRequest;
import com.supplyboost.chero.exception.DomainException;
import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.model.ResourceType;
import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.security.AuthenticationMetadata;
import com.supplyboost.chero.subscription.service.SubscriptionService;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.model.UserRole;
import com.supplyboost.chero.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, SubscriptionService subscriptionService, CharacterService characterService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.subscriptionService = subscriptionService;
        this.characterService = characterService;
    }


    public User login(LoginRequest loginRequest){
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
    public User register(RegisterRequest registerRequest){

        Optional<User> userOptional = userRepository.findByUsername(registerRequest.getUsername());

        if(userOptional.isPresent()){
            throw new DomainException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user = userRepository.save(initializeUser(registerRequest));

        subscriptionService.createDefaultSubscription(user);
        GameCharacter gameCharacter = characterService.createGameCharacter(user);

        log.info("Successfully create new user account [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        user.setGameCharacter(gameCharacter);
        userRepository.save(user);

        return user;
    }

    public User findByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isEmpty()){
            throw new DomainException("Username [%s] does not exist please contact administrator.".formatted(username));
        }

        return userOptional.get();
    }

    public User getById(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isEmpty()){
            throw new DomainException("User with id [%s] does not exist please contact administrator.".formatted(id));
        }

        return userOptional.get();
    }

    public void editUserDetails(UUID userId, UserEditRequest userEditRequest){

        User user = getById(userId);

        user.setEmail(userEditRequest.getEmail());
        user.setCountry(userEditRequest.getCountry());
        user.setProfile_picture(userEditRequest.getProfilePicture());

        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DomainException("User with this username does not exist."));

        return new AuthenticationMetadata(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                user.isActive());
    }




    private User initializeUser(RegisterRequest registerRequest){

        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(UserRole.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }



}
