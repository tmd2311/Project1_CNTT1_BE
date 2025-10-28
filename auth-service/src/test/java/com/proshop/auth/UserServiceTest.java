package com.proshop.auth;

import com.proshop.auth.dto.response.GeneralResponse;
import com.proshop.auth.dto.response.PageResponse;
import com.proshop.auth.dto.response.UserInfoResponse;
import com.proshop.auth.entity.UserEntity;
import com.proshop.auth.mapper.UserMapper;
import com.proshop.auth.repository.UserRepository;
import com.proshop.auth.service.user.impl.UserServiceImpl;
import com.proshop.auth.utils.enums.UserStatus;
import com.proshop.exceptionlib.enums.ResErrorCode;
import com.proshop.exceptionlib.exceptions.ResException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;
    private UserInfoResponse userInfoResponse;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setCode("USER_001");
        testUser.setAccount("testuser");
        testUser.setEmail("test@example.com");
        testUser.setStatus(UserStatus.ACTIVE.name());
        testUser.setDeleted(false);

        userInfoResponse = new UserInfoResponse();
        //userInfoResponse.setId(1L);
        userInfoResponse.setCode("USER_001");
        userInfoResponse.setAccount("testuser");
    }

    @Test
    void getUserById_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(userInfoResponse);

        // Act
        GeneralResponse<UserInfoResponse> result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("USER_001", result.getData().getCode());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> userService.getUserById(1L));
        assertEquals(ResErrorCode.USER_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void getAllUsers_Success() {
        // Arrange
        List<UserEntity> users = Arrays.asList(testUser);
        Page<UserEntity> userPage = new PageImpl<>(users);

        when(userRepository.findAllByDeletedFalse(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toDTO(any(UserEntity.class))).thenReturn(userInfoResponse);

        // Act
        GeneralResponse<PageResponse<UserInfoResponse>> result =
                userService.getAllUsers(PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getData().size());
    }

    @Test
    void deactivateUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // Act
        userService.deactivateUser(1L);

        // Assert
        verify(userRepository).save(argThat(user ->
                UserStatus.INACTIVE.name().equals(user.getStatus())
        ));
    }

    @Test
    void deactivateUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> userService.deactivateUser(1L));
        assertEquals(ResErrorCode.USER_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void activateUser_Success() {
        // Arrange
        testUser.setStatus(UserStatus.INACTIVE.name());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // Act
        userService.activateUser(1L);

        // Assert
        verify(userRepository).save(argThat(user ->
                UserStatus.ACTIVE.toString().equals(user.getStatus())
        ));
    }

    @Test
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ResException exception = assertThrows(ResException.class,
                () -> userService.deleteUser(1L));
        assertEquals(ResErrorCode.USER_NOT_FOUND.code(), exception.getCode());
    }

    @Test
    void softDeleteUser_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // Act
        userService.softDeleteUser(1L);

        // Assert
        verify(userRepository).save(argThat(user ->
                user.getDeleted() && UserStatus.INACTIVE.name().equals(user.getStatus())
        ));
    }
}
