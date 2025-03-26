package com.supplyboost.chero.user;

import com.supplyboost.chero.exception.DomainException;
import com.supplyboost.chero.game.character.model.GameCharacter;
import com.supplyboost.chero.game.character.service.CharacterService;
import com.supplyboost.chero.notification.service.NotificationService;
import com.supplyboost.chero.subscription.service.SubscriptionService;
import com.supplyboost.chero.user.model.User;
import com.supplyboost.chero.user.model.UserRole;
import com.supplyboost.chero.user.repository.UserRepository;
import com.supplyboost.chero.user.service.UserService;
import com.supplyboost.chero.web.dto.AdminUserEditRequest;
import com.supplyboost.chero.web.dto.RegisterRequest;
import com.supplyboost.chero.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private CharacterService characterService;

    @Mock
    private NotificationService notificationService;

    @Spy
    @InjectMocks
    private UserService userService;

    //EditUserDetails
    @Test
    void givenMissingUserFromDataBase_whenEditUserDetails_thenExceptionIsThrown(){
        UUID userId = UUID.randomUUID();
        UserEditRequest dto = UserEditRequest.builder().build();
        MultipartFile profileImage = new MockMultipartFile("Name", (byte[]) null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.editUserDetails(userId, dto, profileImage));
    }

    //EditUserDetails
    @Test
    void giveExistingUserWhenEditTheirProfileWithActualEmail_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase(){
        UUID userId = UUID.randomUUID();
        User user = getUserMock(userId);
        UserEditRequest dto = getEditRequest();

        byte[] imageBytes = "fake-image-content".getBytes(StandardCharsets.UTF_8);
        MultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "avatar.png",
                "image/png",
                imageBytes
        );

        doReturn(user).when(userService).getById(userId);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.editUserDetails(userId, dto, profileImage);

        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getCharacterName(), user.getGameCharacter().getNickName());
        assertTrue(user.getProfile_picture().startsWith("/uploads/"));
        assertTrue(user.getProfile_picture().endsWith(".png"));
    }

    //EditUserDetails
    @Test
    void giveExistingUserWhenEditTheirProfileWithNullProfilePicture_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase(){
        UUID userId = UUID.randomUUID();
        User user = getUserMock(userId);
        UserEditRequest dto = getEditRequest();

        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        doReturn(user).when(userService).getById(userId);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.editUserDetails(userId, dto, emptyFile);

        assertNull(user.getProfile_picture());
    }
    //EditUserDetails
    @Test
    void giveExistingUserWhenEditTheirProfileWithEmptyProfilePicture_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase(){
        UUID userId = UUID.randomUUID();
        User user = getUserMock(userId);
        UserEditRequest dto = getEditRequest();

        doReturn(user).when(userService).getById(userId);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.editUserDetails(userId, dto, null);

        assertNull(user.getProfile_picture());
    }

    //EditUserDetails
    @Test
    void giveExistingUserWhenEditTheirProfileWithBadProfilePicture_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase(){
        UUID userId = UUID.randomUUID();
        User user = getUserMock(userId);
        UserEditRequest dto = getEditRequest();

        MockMultipartFile badFile = new MockMultipartFile("file", "bad.exe", "application/exe", "xxx".getBytes());

        doReturn(user).when(userService).getById(userId);

        userService.editUserDetails(userId, dto, badFile);

        assertNull(user.getProfile_picture());
    }

    //EditUserDetails
    @Test
    void giveExistingUserWhenEditTheirProfileWithIOException_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase(){
        UUID userId = UUID.randomUUID();
        User user = getUserMock(userId);
        UserEditRequest dto = getEditRequest();

        MockMultipartFile validFile = new MockMultipartFile("file", "avatar.png", "image/png", "image-content".getBytes());

        doReturn(user).when(userService).getById(userId);

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.write(any(Path.class), any(byte[].class)))
                    .thenThrow(new IOException("Simulated IOException"));

            userService.editUserDetails(userId, dto, validFile);

            assertNull(user.getProfile_picture(), "Profile picture should not be set on IOException");
            assertEquals(dto.getEmail(), user.getEmail(), "Email should still be updated");
            assertEquals(dto.getCharacterName(), user.getGameCharacter().getNickName(), "Nickname should still be updated");
        }
    }

    //AdminUpdateUser
    @Test
    void givenUserWithAdminRole_whenSwitchRoles_thenExpectUserHasUserRole(){
        //Given
        UUID userId = UUID.randomUUID();
        AdminUserEditRequest adminUserEditRequest = AdminUserEditRequest.builder()
                .characterName("name")
                .email("test@test.com")
                .userRole(UserRole.ROLE_USER)
                .build();
        User user = User.builder()
                .id(userId)
                .userRole(UserRole.ROLE_ADMIN)
                .gameCharacter(GameCharacter.builder()
                        .build())
                .email("test@test.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.adminUpdateUser(userId, adminUserEditRequest);

        assertThat(user.getUserRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getGameCharacter().getNickName()).isEqualTo("name");

    }

    //AdminUpdateUser
    @Test
    void givenUserWithUserRole_whenSwitchRoles_thenExpectUserHasAdminRole(){
        //Given
        UUID userId = UUID.randomUUID();
        AdminUserEditRequest adminUserEditRequest = AdminUserEditRequest.builder()
                .characterName("name")
                .email("test@test.com")
                .userRole(UserRole.ROLE_ADMIN)
                .build();
        User user = User.builder()
                .id(userId)
                .userRole(UserRole.ROLE_USER)
                .gameCharacter(GameCharacter.builder()
                        .build())
                .email("test@test.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.adminUpdateUser(userId, adminUserEditRequest);

        assertThat(user.getUserRole()).isEqualTo(UserRole.ROLE_ADMIN);
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getGameCharacter().getNickName()).isEqualTo("name");

    }

    // Registration
    @Test
    void givenDtoRegistrationDetails_whenRegisteringAndUserExists_thenThrowDomainException(){

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("testUserName")
                .password("testPassword")
                .email("test@test.com")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        assertThrows(DomainException.class, () -> userService.register(registerRequest));

        verify(userRepository, never()).save(any());
        verify(subscriptionService, never()).createDefaultSubscription(any());
        verify(notificationService, never()).saveNotificationPreference(any(), anyBoolean(), any());
        verify(notificationService, never()).sendGreetings(any(), any());

    }

    // Registration
    @Test
    void givenDtoRegistrationDetails_whenRegistering_thenRegisterNewUser(){
        UUID userId = UUID.randomUUID();

        RegisterRequest registerRequest = getRegisterRequestMock();

        User user = getUserMock(userId);

        GameCharacter mockCharacter = GameCharacter.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .nickName("testCharacter")
                .build();

        user.setGameCharacter(mockCharacter);

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User toSave = invocation.getArgument(0);
            toSave.setId(userId);
            return toSave;
        });
        when(characterService.createGameCharacter(any(User.class))).thenReturn(mockCharacter);

        userService.register(registerRequest);

        assertEquals(registerRequest.getUsername(), user.getUsername());
        assertEquals(registerRequest.getPassword(), user.getPassword());
        assertEquals(registerRequest.getEmail(), user.getEmail());
        assertEquals(mockCharacter, user.getGameCharacter());



        verify(notificationService, times(1)).saveNotificationPreference(user.getId(), true, user.getEmail());
        verify(notificationService, times(1)).sendGreetings(user.getId(), user.getUsername());
    }



    User getUserMock(UUID userId){
        return User.builder()
                .id(userId)
                .username("newUserUserName")
                .email("test@test.com")
                .password("testPassword")
                .gameCharacter(GameCharacter.builder()
                        .nickName("oldNickName")
                        .build())
                .build();
    }

    UserEditRequest getEditRequest(){
       return UserEditRequest.builder()
                .characterName("GameCharacterNickName")
                .email("test@test.com")
                .build();
    }

    RegisterRequest getRegisterRequestMock(){
        return RegisterRequest.builder()
                .username("newUserUserName")
                .password("testPassword")
                .email("test@test.com")
                .build();
    }



}
