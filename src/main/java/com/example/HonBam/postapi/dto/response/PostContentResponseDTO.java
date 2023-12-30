package com.example.HonBam.postapi.dto.response;

import com.example.HonBam.postapi.entity.Post;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostContentResponseDTO {

    private String postId;
    private String content;
    private byte[] postImg;
    private LocalDateTime updateDate;

    public PostContentResponseDTO(Post post, byte[] postImg) {

        this.postId = post.getPostId();
        this.updateDate = post.getUpdateDate();
        this.postImg = postImg;
        this.updateDate = post.getUpdateDate();
    }
}
