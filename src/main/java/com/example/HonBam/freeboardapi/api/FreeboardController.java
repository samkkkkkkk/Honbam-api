package com.example.HonBam.freeboardapi.api;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.freeboardapi.Service.FreeboardService;
import com.example.HonBam.freeboardapi.dto.request.FreeboardRequestDTO;
import com.example.HonBam.freeboardapi.dto.response.FreeboardResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/freeboard")
@CrossOrigin
public class FreeboardController {

    private final FreeboardService freeboardService;

    @PostMapping
    public ResponseEntity<?> createContent(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody FreeboardRequestDTO requestDTO
    ) {

        FreeboardResponseDTO responseDTO =
                freeboardService.createContent(requestDTO, userInfo);

        return ResponseEntity.ok().body(responseDTO);

    }

    @GetMapping
    public ResponseEntity<?> contentList(
            @AuthenticationPrincipal TokenUserInfo userInfo
        ) {

        FreeboardResponseDTO responseDTO = freeboardService.retrieve(userInfo.getUserId());
        return ResponseEntity.ok().body(responseDTO);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteContent(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable("id") Long id
    ){
        if(id == null || Long.toString(id).isEmpty()){
            ResponseEntity.badRequest()
                    .body("에러");
        }

        try{
            FreeboardResponseDTO responseDTO = freeboardService.delete(userInfo.getUserId(), id);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(FreeboardResponseDTO.builder().build());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyContent(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable("id") Long id,
            @RequestBody FreeboardRequestDTO RequestDTO
    ) {

        freeboardService.modify(userInfo.getUserId(), id, RequestDTO);
        return null;

    }



}
