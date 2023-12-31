package com.example.HonBam.postapi.dto.request;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModifyRequestDTO {

    private String comment;
    private Long commentId;

}
