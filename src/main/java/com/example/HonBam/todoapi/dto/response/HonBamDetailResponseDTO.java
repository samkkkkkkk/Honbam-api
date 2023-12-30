package com.example.HonBam.HonBamapi.dto.response;

import com.example.HonBam.HonBamapi.entity.HonBam;
import lombok.*;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HonBamDetailResponseDTO {

    private String id;
    private String title;
    private boolean done;

    // 엔터티를 DTO로 만들어주는 생성자
    public HonBamDetailResponseDTO(HonBam HonBam) {
        this.id = HonBam.getHonBamId();
        this.title = HonBam.getTitle();
        this.done = HonBam.isDone();
    }
}











