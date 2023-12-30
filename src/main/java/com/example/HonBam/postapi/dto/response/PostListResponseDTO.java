package com.example.HonBam.postapi.dto.response;

import com.example.HonBam.postapi.entity.Post;
import lombok.*;

import java.util.List;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResponseDTO {

//    private String error; // 에러 발생 시 에러 메세지를 담을 필드
    private List<PostDetailResponseDTO> posts;


}









