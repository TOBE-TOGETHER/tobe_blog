package com.tobe.blog.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tobe.blog.beans.consts.Const;
import com.tobe.blog.beans.dto.user.*;
import com.tobe.blog.beans.entity.user.UserEntity;
import com.tobe.blog.beans.entity.user.UserFeatureEntity;
import com.tobe.blog.beans.entity.user.UserRoleEntity;
import com.tobe.blog.core.exception.TobeRuntimeException;
import com.tobe.blog.core.mapper.UserMapper;
import com.tobe.blog.core.utils.BasicConverter;
import com.tobe.blog.core.utils.CacheUtil;
import com.tobe.blog.core.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

    private final CacheUtil cacheUtil;
    private final UserRoleService userRoleService;
    private final UserFeatureService userFeatureService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserGeneralDTO getUser(long id) {
        return Optional.ofNullable(this.getById(id))
                .map(u -> BasicConverter.convert(u, UserGeneralDTO.class))
                .orElse(null);
    }

    public UserBriefProfileDTO getUserBriefProfile(long id) {
        return Optional.ofNullable(this.getById(id))
                .map(u -> BasicConverter.convert(u, UserBriefProfileDTO.class))
                .orElse(null);
    }

    public UserFullProfileDTO getUserFullProfile(long id) {
        UserFullProfileDTO result = Optional.ofNullable(this.getById(id))
                .map(u -> BasicConverter.convert(u, UserFullProfileDTO.class))
                .orElse(null);
        if (Objects.isNull(result)) {
            return null;
        }
        result.setFeatures(BasicConverter.convert(userFeatureService.getById(id), UserFeatureDTO.class));
        return result;
    }

    public Page<UserGeneralDTO> getUsers(int current, int size) {
        return (Page<UserGeneralDTO>) this.page(new Page<>(current, size))
                .convert((x) -> BasicConverter.convert(x, UserGeneralDTO.class));
    }

    @Transactional
    public UserGeneralDTO createUser(UserCreationDTO dto) {
        // verify if the same request has been submitted in last 3s
        validateRequestSubmitted(dto);

        // verify if the user already exists
        validateEmailExist(dto.getEmail());

        // insert user
        UserEntity userEntity = BasicConverter.convert(dto, UserEntity.class);
        userEntity.setUsername(getDefaultUsernameFromEmail(userEntity.getEmail()));
        userEntity.setPassword(encoder.encode(dto.getPassword()));
        userEntity.setDeleted(Boolean.FALSE);
        this.save(userEntity);

        // insert default user role
        List<UserRoleEntity> defaultRoles = createDefaultRole(userEntity.getId());
        defaultRoles.forEach(x -> this.userRoleService.save(x));

        // insert default user feature setting
        userFeatureService.save(createDefaultFeature(userEntity.getId()));
        return BasicConverter.convert(userEntity, UserGeneralDTO.class);
    }

    @Transactional
    public UserGeneralDTO updateUser(UserUpdateDTO dto) {
        final UserEntity oriUserEntity = this.getById(dto.getId());
        if (Objects.isNull(oriUserEntity)) {
            log.warn("user doesn't exist, id: " + dto.getId());
            return new UserGeneralDTO();
        }
        BeanUtils.copyProperties(dto, oriUserEntity);
        this.updateById(oriUserEntity);
        UserGeneralDTO result = BasicConverter.convert(oriUserEntity, UserGeneralDTO.class);
        UserFeatureDTO featureDTO = updateFeature(dto.getFeatures(), dto.getId());
        result.setFeatures(featureDTO);
        return result;
    }

    @Transactional
    public void deleteUser(long id) {
        // delete role of the specific user
        this.userRoleService.remove(new LambdaQueryWrapper<UserRoleEntity>().eq(UserRoleEntity::getUserId, id));
        // delete features of the specific user
        this.userFeatureService
                .remove(new LambdaQueryWrapper<UserFeatureEntity>().eq(UserFeatureEntity::getUserId, id));
        // delete the specific user
        this.removeById(id);
    }

    private List<UserRoleEntity> createDefaultRole(Long userId) {
        return List.of(
                new UserRoleEntity(Const.Role.BASIC, userId));
    }

    private UserFeatureEntity createDefaultFeature(Long userId) {
        final UserFeatureEntity feature = new UserFeatureEntity();
        feature.setUserId(userId);
        feature.setArticleModule(Boolean.TRUE);
        feature.setPlanModule(Boolean.TRUE);
        feature.setVocabularyModule(Boolean.TRUE);
        return feature;
    }

    private String getDefaultUsernameFromEmail(String email) {
        return email.substring(0, email.indexOf('@'));
    }

    private void validateRequestSubmitted(UserCreationDTO dto) {
        String payload = GsonUtil.toJson(dto);
        if (cacheUtil.getExpireTime(payload) > 0) {
            throw new TobeRuntimeException("The request has been submitted, please don't repeatly submit.");
        }
        cacheUtil.set(payload, null, 3);
    }

    private void validateEmailExist(String email) {
        boolean emailExist = this.getOneOpt(
                new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getEmail, email))
                .isPresent();
        if (emailExist) {
            throw new TobeRuntimeException("The email has been registered, please login directly.");
        }
    }

    private UserFeatureDTO updateFeature(UserFeatureDTO dto, Long userId) {
        UserFeatureEntity featureEntity = new UserFeatureEntity();
        BeanUtils.copyProperties(dto, featureEntity);
        featureEntity.setUserId(userId);
        userFeatureService.saveOrUpdate(featureEntity);
        return userFeatureService
                .getOneOpt(new LambdaQueryWrapper<UserFeatureEntity>().eq(UserFeatureEntity::getUserId, userId))
                .map(e -> BasicConverter.convert(e, UserFeatureDTO.class)).orElse(null);
    }
}
