package com.example.HonBam.freeboardapi.dto.response;


import com.example.HonBam.freeboardapi.entity.Freeboard;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeboardResponseDTO {

    private List<FreeboardDetailResponseDTO> posts;
    private int count;

}
