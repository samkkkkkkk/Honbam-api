package com.example.HonBam.HonBamapi.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HonBamModifyRequestDTO {

    @NotBlank
    private String id;
    private boolean done;

}
