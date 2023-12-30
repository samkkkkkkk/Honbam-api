package com.example.HonBam.postapi.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostModifyRequestDTO {

    @NotBlank
    private String id;
    private boolean done;

}
