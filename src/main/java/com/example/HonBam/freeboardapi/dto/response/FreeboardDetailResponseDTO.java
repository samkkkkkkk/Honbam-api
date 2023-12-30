package com.example.HonBam.freeboardapi.dto.response;

import com.example.HonBam.freeboardapi.entity.Freeboard;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FreeboardDetailResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String userName;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime createDate;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime updateDate;


    public FreeboardDetailResponseDTO(Freeboard freeboard) {
        this.id = freeboard.getId();
        this.title = freeboard.getTitle();
        this.content = freeboard.getContent();
        this.userName = freeboard.getUser().getUserName();
        this.createDate = freeboard.getCreateDate();
        this.updateDate = freeboard.getUpdateDate();
    }


}
