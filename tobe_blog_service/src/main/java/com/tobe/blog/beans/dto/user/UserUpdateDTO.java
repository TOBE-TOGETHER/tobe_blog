package com.tobe.blog.beans.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserUpdateDTO {
    @NotNull
    private Long id;
    @Length(max = 32)
    private String lastName;
    @Length(max = 32)
    private String firstName;
    @Length(max = 20)
    private String phoneNum;
    @Email
    @Length(max = 120)
    private String email;
    @Length(max = 120)
    private String address;
    @Length(min = 2, max = 64)
    private String username;
    @Length(max = 2000)
    private String avatarUrl;
    @Length(max = 1000)
    private String introduction;
    @Length(max = 200)
    private String blog;
    @Length(max = 100)
    private String profession;
    @Length(max = 2000)
    private String photoImg;
    @Length(max = 2000)
    private String backgroundImg;
    @NotNull
    private UserFeatureDTO features;
}
