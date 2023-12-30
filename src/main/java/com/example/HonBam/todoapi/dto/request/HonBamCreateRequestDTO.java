package com.example.HonBam.HonBamapi.dto.request;

import com.example.HonBam.HonBamapi.entity.HonBam;
import com.example.HonBam.userapi.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HonBamCreateRequestDTO {

    @NotBlank
    @Size(min = 2, max = 30)
    private String title;

    // dto를 엔터티로 변환
    public HonBam toEntity(User user) {
        return HonBam.builder()
                .title(this.title)
                .user(user)
                .build();
    }

}









