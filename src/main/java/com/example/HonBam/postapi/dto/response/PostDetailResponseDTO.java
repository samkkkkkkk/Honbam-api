package com.example.HonBam.postapi.dto.response;

import com.example.HonBam.postapi.entity.Post;
import com.example.HonBam.userapi.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailResponseDTO {

    private String postId;
    private String content;
    private LocalDateTime updateDate;
    private String postImg;
    private String userId;

    // 엔터티를 DTO로 만들어주는 생성자
    public PostDetailResponseDTO(Post post) {
        this.postId = post.getPostId();
        this.content = post.getContent();
        this.updateDate = post.getUpdateDate();
        this.postImg = post.getPostImg();
        this.userId = post.getUser().getId();
    }
}











