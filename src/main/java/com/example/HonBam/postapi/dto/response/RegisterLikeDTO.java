package com.example.HonBam.postapi.dto.response;

import com.example.HonBam.postapi.entity.SnsLike;
import lombok.*;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;

@Getter @Setter
@ToString @NoArgsConstructor
@AllArgsConstructor @EqualsAndHashCode
@Builder
public class RegisterLikeDTO {

    private SnsLike like;

}
