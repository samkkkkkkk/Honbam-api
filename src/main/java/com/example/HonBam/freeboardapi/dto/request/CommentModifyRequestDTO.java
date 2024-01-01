package com.example.HonBam.freeboardapi.dto.request;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentModifyRequestDTO {

    private Long id;
    private String comment;

}
