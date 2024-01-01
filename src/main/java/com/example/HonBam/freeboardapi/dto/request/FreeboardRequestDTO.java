package com.example.HonBam.freeboardapi.dto.request;

import com.example.HonBam.freeboardapi.entity.Freeboard;
import com.example.HonBam.userapi.entity.User;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeboardRequestDTO {

    @NotNull
    private String content;
    private String title;
    private String userName;

    public Freeboard toEntity(User user) {
        return Freeboard.builder()
                .content(this.content)
                .title((this.title))
                .userName(this.userName)
                .user(user)
                .build();
    }

    public Freeboard toEntity(Freeboard foundContents, User user) {
        return Freeboard.builder()
                .id(foundContents.getId())
                .content(this.content)
                .title((this.title))
                .userName(foundContents.getUserName())
                .user(user)
                .build();
    }

}
