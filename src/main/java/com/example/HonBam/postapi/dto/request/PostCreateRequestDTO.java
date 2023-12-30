package com.example.HonBam.postapi.dto.request;

import com.example.HonBam.postapi.entity.Post;
import com.example.HonBam.userapi.entity.User;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCreateRequestDTO {

    @NotNull
    private String content;
    // dto를 엔터티로 변환
    public Post toEntity(User user, String filePath) {
        return Post.builder()
                .content(this.content)
                .postImg(filePath)
                .user(user)
                .build();
    }

}









