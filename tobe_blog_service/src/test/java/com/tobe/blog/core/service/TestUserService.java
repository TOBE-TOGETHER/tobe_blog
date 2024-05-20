package com.tobe.blog.core.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tobe.blog.beans.consts.Const.Role;
import com.tobe.blog.beans.dto.user.UserBriefProfileDTO;
import com.tobe.blog.beans.dto.user.UserCreationDTO;
import com.tobe.blog.beans.dto.user.UserFeatureDTO;
import com.tobe.blog.beans.dto.user.UserFullProfileDTO;
import com.tobe.blog.beans.dto.user.UserGeneralDTO;
import com.tobe.blog.beans.dto.user.UserUpdateDTO;
import com.tobe.blog.beans.entity.user.UserFeatureEntity;
import com.tobe.blog.beans.entity.user.UserRoleEntity;
import com.tobe.blog.core.mapper.UserFeatureMapper;
import com.tobe.blog.core.mapper.UserRoleMapper;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class TestUserService {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRoleMapper roleMapper;
    @Autowired
    private UserFeatureMapper featureMapper;

    @Test
    @DisplayName("User Service: user can be created successfully with correct input")
    public void testCreateUser_saveWithCorrectInput() {
        UserCreationDTO dto = new UserCreationDTO();
        dto.setEmail("bob.pop@tobe.com");
        dto.setLastName("bob");
        dto.setFirstName("pop");
        dto.setPassword("123456");

        // The user should be saved successfully
        UserGeneralDTO result = userService.createUser(dto);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(dto.getEmail(), result.getEmail());
        Assertions.assertEquals(dto.getLastName(), result.getLastName());
        Assertions.assertEquals(dto.getFirstName(), result.getFirstName());

        // The user role should be correctly saved
        UserRoleEntity roleEntity = roleMapper.selectOne(
                new LambdaQueryWrapper<UserRoleEntity>()
                        .eq(UserRoleEntity::getUserId, result.getId())
                        .eq(UserRoleEntity::getRole, Role.BASIC)
                        .eq(UserRoleEntity::getDeleted, Boolean.FALSE));
        Assertions.assertNotNull(roleEntity);

        // The user feature should be correctly saved
        UserFeatureEntity featureEntity = featureMapper.selectOne(
                new LambdaQueryWrapper<UserFeatureEntity>()
                        .eq(UserFeatureEntity::getUserId, result.getId())
                        .eq(UserFeatureEntity::getDeleted, Boolean.FALSE));
        Assertions.assertNotNull(featureEntity);
        Assertions.assertEquals(Boolean.TRUE, featureEntity.getArticleModule());
        Assertions.assertEquals(Boolean.TRUE, featureEntity.getPlanModule());
        Assertions.assertEquals(Boolean.TRUE, featureEntity.getVocabularyModule());
    }

    @Test
    @DisplayName("User Service: user can not be saved when the email already exists")
    public void testCreateUser_duplicatedEmailIsNotAllow() {
        UserCreationDTO dto = new UserCreationDTO();
        dto.setEmail("duplicat_testing@tobe.com");
        dto.setPassword("123456");
        // The user should be saved successfully
        userService.createUser(dto);

        // DuplicateKeyException should be throw when executing the same request again
        Assertions.assertThrows(DuplicateKeyException.class, () -> userService.createUser(dto));
    }

    @Test
    @DisplayName("User Service: fail to save with bad input")
    public void testCreateUser_badInputIsNotAllow() {
        UserCreationDTO dtoWithNullEmail = new UserCreationDTO();
        dtoWithNullEmail.setLastName("bob");
        dtoWithNullEmail.setFirstName("pop");
        dtoWithNullEmail.setPassword("123456");
        Assertions.assertThrows(RuntimeException.class, () -> userService.createUser(dtoWithNullEmail));

        UserCreationDTO dtoWithNullPassword = new UserCreationDTO();
        dtoWithNullPassword.setEmail("no_password@tobe.com");
        dtoWithNullPassword.setLastName("bob");
        dtoWithNullPassword.setFirstName("pop");
        Assertions.assertThrows(RuntimeException.class, () -> userService.createUser(dtoWithNullPassword));

        UserCreationDTO dtoWithLongFirstName = new UserCreationDTO();
        dtoWithLongFirstName.setEmail("longFirstName@tobe.com");
        dtoWithLongFirstName.setFirstName(RandomStringUtils.randomAlphabetic(33));
        dtoWithLongFirstName.setPassword("123456");
        Assertions.assertThrows(RuntimeException.class, () -> userService.createUser(dtoWithLongFirstName));
        dtoWithLongFirstName.setFirstName(RandomStringUtils.randomAlphabetic(32));
        Assertions.assertDoesNotThrow(() -> userService.createUser(dtoWithLongFirstName));

        UserCreationDTO dtoWithLongLastName = new UserCreationDTO();
        dtoWithLongLastName.setEmail("longLastName@tobe.com");
        dtoWithLongLastName.setLastName(RandomStringUtils.randomAlphabetic(33));
        dtoWithLongLastName.setPassword("123456");
        Assertions.assertThrows(RuntimeException.class, () -> userService.createUser(dtoWithLongLastName));
        dtoWithLongLastName.setLastName(RandomStringUtils.randomAlphabetic(32));
        Assertions.assertDoesNotThrow(() -> userService.createUser(dtoWithLongLastName));

        // username comes from email and length can not exceed 64
        UserCreationDTO dtoWithLongEmail = new UserCreationDTO();
        dtoWithLongEmail.setEmail(RandomStringUtils.randomAlphabetic(65) + "@tobe.com");
        dtoWithLongEmail.setPassword("123456");
        Assertions.assertThrows(RuntimeException.class, () -> userService.createUser(dtoWithLongEmail));
        dtoWithLongEmail.setEmail(RandomStringUtils.randomAlphabetic(64) + "@tobe.com");
        Assertions.assertDoesNotThrow(() -> userService.createUser(dtoWithLongEmail));

        // Since the pw will be encrypted with Bcrypt algo, no need to worry about
        // length
        UserCreationDTO dtoWithLongPw = new UserCreationDTO();
        dtoWithLongPw.setEmail("longPw@tobe.com");
        dtoWithLongPw.setPassword(RandomStringUtils.randomAlphabetic(100));
        Assertions.assertDoesNotThrow(() -> userService.createUser(dtoWithLongPw));
    }

    @Test
    @DisplayName("User Service: get existing user info by userId")
    public void testGetUser_getExistingUser() {
        UserGeneralDTO dto = userService.getUser(1);
        Assertions.assertEquals(DefaultTestData.USER_ID, dto.getId());
        Assertions.assertEquals(DefaultTestData.USERNAME, dto.getUsername());
        Assertions.assertEquals(DefaultTestData.PHONE_NUM, dto.getPhoneNum());
        Assertions.assertEquals(DefaultTestData.EMAIL, dto.getEmail());
        Assertions.assertEquals(DefaultTestData.LAST_NAME, dto.getLastName());
        Assertions.assertEquals(DefaultTestData.FIRST_NAME, dto.getFirstName());
        Assertions.assertEquals(DefaultTestData.ADDRESS, dto.getAddress());
        Assertions.assertEquals(DefaultTestData.AVATAR, dto.getAvatarUrl());
        Assertions.assertEquals(DefaultTestData.INTRODUCTION, dto.getIntroduction());
        Assertions.assertEquals(DefaultTestData.BLOG, dto.getBlog());
        Assertions.assertEquals(DefaultTestData.BACKGROUND_IMG, dto.getBackgroundImg());
        Assertions.assertEquals(DefaultTestData.PHOTO_IMG, dto.getPhotoImg());
        Assertions.assertEquals(DefaultTestData.PROFESSION, dto.getProfession());
    }

    @Test
    @DisplayName("User Service: get non-existing user info by userId")
    public void testGetUser_getNoneExistingUser() {
        Assertions.assertNull(userService.getUser(2));
    }

    @Test
    @DisplayName("User Service: get existing user brief profile info by userId")
    public void testgetUserBriefProfile_getExistingUser() {
        UserBriefProfileDTO dto = userService.getUserBriefProfile(1);
        Assertions.assertEquals(DefaultTestData.USER_ID, dto.getId());
        Assertions.assertEquals(DefaultTestData.LAST_NAME, dto.getLastName());
        Assertions.assertEquals(DefaultTestData.FIRST_NAME, dto.getFirstName());
        Assertions.assertEquals(DefaultTestData.AVATAR, dto.getAvatarUrl());
        Assertions.assertEquals(DefaultTestData.INTRODUCTION, dto.getIntroduction());
        Assertions.assertEquals(DefaultTestData.BLOG, dto.getBlog());
    }

    @Test
    @DisplayName("User Service: get non-existing user brief profile info by userId")
    public void testgetUserBriefProfile_getNoneExistingUser() {
        Assertions.assertNull(userService.getUserBriefProfile(2));
    }

    @Test
    @DisplayName("User Service: get existing user full profile info by userId")
    public void testgetUserFullProfile_getExistingUser() {
        UserFullProfileDTO dto = userService.getUserFullProfile(1);
        Assertions.assertEquals(dto.getId(), 1);
        Assertions.assertEquals(dto.getLastName(), "Admin");
        Assertions.assertEquals(dto.getFirstName(), "TOBE");
        Assertions.assertEquals(dto.getAddress(), "Shenzhen China");
        Assertions.assertEquals(dto.getAvatarUrl(), "https://avatar.com");
        Assertions.assertEquals(dto.getIntroduction(), "Hello world");
        Assertions.assertEquals(dto.getBlog(), "https://blog.com");
        Assertions.assertEquals(dto.getBackgroundImg(), "https://bg.com");
        Assertions.assertEquals(dto.getPhotoImg(), "https://photo.com");
        Assertions.assertEquals(dto.getProfession(), "Developer");
        UserFeatureDTO featureDTO = dto.getFeatures();
        Assertions.assertEquals(featureDTO.getArticleModule(), Boolean.TRUE);
        Assertions.assertEquals(featureDTO.getPlanModule(), Boolean.TRUE);
        Assertions.assertEquals(featureDTO.getVocabularyModule(), Boolean.FALSE);
    }

    @Test
    @DisplayName("User Service: get non-existing user full profile info by userId")
    public void testgetUserFullProfile_getNoneExistingUser() {
        Assertions.assertNull(userService.getUserFullProfile(2));
    }

    @Test
    @DisplayName("User Service: get all user profiles in pages")
    public void testGetUsers_getUsersNotEmpty() {
        Page<UserGeneralDTO> page = userService.getUsers(1, 5);
        Assertions.assertEquals(1, page.getCurrent());
        Assertions.assertEquals(5, page.getSize());
        Assertions.assertTrue(page.getRecords().size() > 0);
    }

    @Test
    @DisplayName("User Service: get all user profiles in pages")
    public void testUpdateUser_existingUserCanBeUpdated() {
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setAddress(null);
        // TODO
        UserGeneralDTO dto = userService.getUser(1);

    }

    class DefaultTestData {
        public static final long USER_ID = 1L;
        public static final String FIRST_NAME = "Admin";
        public static final String LAST_NAME = "TOBE";
        public static final String USERNAME = "TOBE";
        public static final String EMAIL = "tobe_admin@tobe.com";
        public static final String ADDRESS = "Shenzhen China";
        public static final String AVATAR = "https://avatar.com";
        public static final String PHONE_NUM = "13145801234";
        public static final String INTRODUCTION = "Hello world";
        public static final String BLOG = "https://blog.com";
        public static final String BACKGROUND_IMG = "https://bg.com";
        public static final String PHOTO_IMG = "https://photo.com";
        public static final String PROFESSION = "Developer";
    }

}
