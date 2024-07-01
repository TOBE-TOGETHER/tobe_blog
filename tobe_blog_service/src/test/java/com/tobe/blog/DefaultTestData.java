package com.tobe.blog;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.tobe.blog.beans.consts.Const;
import com.tobe.blog.beans.dto.user.EnhancedUserDetail;
import com.tobe.blog.beans.dto.user.UserGeneralDTO;

public class DefaultTestData {
    public class DefaultUser {
        public static final long USER_ID = 1L;
        public static final String FIRST_NAME = "Admin";
        public static final String LAST_NAME = "TOBE";
        public static final String USERNAME = "tobe_admin";
        public static final String EMAIL = "tobe_admin@tobe.com";
        public static final String ADDRESS = "Shenzhen China";
        public static final String AVATAR = "https://avatar.com";
        public static final String PHONE_NUM = "13145801234";
        public static final String INTRODUCTION = "Hello world";
        public static final String BLOG = "https://blog.com";
        public static final String BACKGROUND_IMG = "https://bg.com";
        public static final String PHOTO_IMG = "https://photo.com";
        public static final String PROFESSION = "Developer";
        public static final Boolean ARTICLE_MODULE = true;
        public static final Boolean PLAN_MODULE = false;
        public static final Boolean VOCABULARY_MODULE = true;

    }

    public static Authentication getDefaultUserAuthentication() {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(Const.Role.ADMIN, Const.Role.BASIC);
        UserGeneralDTO userProfile = new UserGeneralDTO();
        userProfile.setId(DefaultUser.USER_ID);
        userProfile.setUsername(DefaultUser.USERNAME);
        userProfile.setFirstName(DefaultUser.FIRST_NAME);
        userProfile.setLastName(DefaultUser.LAST_NAME);
        EnhancedUserDetail userDetail =  new EnhancedUserDetail(authorities, DefaultUser.USERNAME, null, userProfile);
        return new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
    }
}
