package com.example.HonBam.postapi.dto.response;

import com.example.HonBam.postapi.entity.SnsLike;
import lombok.*;

@Getter @Setter
@ToString @NoArgsConstructor
@AllArgsConstructor @EqualsAndHashCode
@Builder
public class RegisterLikeDTO {

    private SnsLike like;


}
