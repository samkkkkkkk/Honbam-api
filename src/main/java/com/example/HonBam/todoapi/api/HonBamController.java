package com.example.HonBam.HonBamapi.api;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.HonBamapi.dto.request.HonBamCreateRequestDTO;
import com.example.HonBam.HonBamapi.dto.request.HonBamModifyRequestDTO;
import com.example.HonBam.HonBamapi.dto.response.HonBamListResponseDTO;
import com.example.HonBam.HonBamapi.service.HonBamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/HonBams")
@CrossOrigin
public class HonBamController {

    private final HonBamService HonBamService;

    // 할 일 등록 요청
    @PostMapping
    public ResponseEntity<?> createHonBam(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Validated @RequestBody HonBamCreateRequestDTO requestDTO,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }

        try {
            HonBamListResponseDTO responseDTO = HonBamService.create(requestDTO, userInfo);
            return ResponseEntity
                    .ok()
                    .body(responseDTO);
        } catch (IllegalStateException e) {
            // 권한 때문에 발생한 예외
            log.warn(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity
                    .internalServerError()
                    .body(HonBamListResponseDTO
                            .builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    // 할 일 목록 요청
    @GetMapping
    public ResponseEntity<?> retrieveHonBamList(
            // JwtAuthFilter에서 시큐리티에게 전역적으로 사용할 수 있는 인증 정보를 등록해 놓았기 때문에
            // @AuthenticationPrincipal을 통해 토큰에 인증된 사용자 정보를 불러올 수 있다.
            @AuthenticationPrincipal TokenUserInfo userInfo
            ) {
        log.info("/api/HonBams GET request");
        HonBamListResponseDTO responseDTO = HonBamService.retrieve(userInfo.getUserId());

        return ResponseEntity.ok().body(responseDTO);
    }

    // 할 일 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHonBam(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable("id") String HonBamId
    ) {
        log.info("/api/HonBams/{} DELETE request!", HonBamId);

        if(HonBamId == null || HonBamId.trim().equals("")) {
            return ResponseEntity
                    .badRequest()
                    .body(HonBamListResponseDTO
                            .builder()
                            .error("ID를 전달해 주세요.")
                            .build());
        }

        try {
            HonBamListResponseDTO responseDTO = HonBamService.delete(HonBamId, userInfo.getUserId());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError().body(HonBamListResponseDTO.builder().error(e.getMessage()).build());
        }
    }

    // 할 일 수정하기
    @RequestMapping(method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<?> updateHonBam(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Validated @RequestBody HonBamModifyRequestDTO requestDTO,
            BindingResult result,
            HttpServletRequest request
    ) {
        if(result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getFieldError());
        }

        log.info("/api/HonBams {} request!", request.getMethod());
        log.info("modifying dto: {}", requestDTO);

        try {
            HonBamListResponseDTO responseDTO = HonBamService.update(requestDTO, userInfo.getUserId());
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .internalServerError()
                    .body(HonBamListResponseDTO
                            .builder()
                            .error(e.getMessage())
                            .build());
        }


    }




}










