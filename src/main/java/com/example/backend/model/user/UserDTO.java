package com.example.backend.model.User;

import com.example.backend.entity.User;
import com.example.backend.entity.User.Role;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Integer userId;
    private String fullName;
    private String email;
    private Boolean isSeller;
    private Integer ratingScore;
    private Integer ratingCount;
    private User.Role role;

    public UserDTO(User user) {
        this.userId = user.getUserId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.isSeller = user.getIsSeller();
        this.ratingScore = user.getRatingScore();
        this.ratingCount = user.getRatingCount();
        this.role = user.getRole() != null ? user.getRole() : Role.BIDDER;
    }
}
