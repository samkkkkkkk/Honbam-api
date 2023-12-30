package com.example.HonBam.postapi.api;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.postapi.dto.request.CommentCreateRequestDTO;
import com.example.HonBam.postapi.dto.request.PostCreateRequestDTO;
import com.example.HonBam.postapi.dto.response.PostListResponseDTO;
import com.example.HonBam.postapi.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@CrossOrigin
public class PostController {

    private final PostService postService;

    // 게시글 등록 요청
    @PostMapping("/addboard")
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Validated @RequestPart("content") PostCreateRequestDTO requestDTO,
            @RequestPart(value = "postImg", required = false) MultipartFile postImg,
//            @RequestPart(value = "postImg", required = false) MultipartFile postImg,
            BindingResult result
    ) {

//        log.info("userinfo=={}" ,userInfo);

        if (result.hasErrors()) {
            log.warn("DTO 검증 에러 발생: {}", result.getFieldError());
            return ResponseEntity
                    .badRequest()
                    .body(result.getFieldError());
        }

        try {
            String filePath = null;
            if (postImg != null) {
                filePath = postService.uploadFileImg(postImg);
//                log.info("filePath== {}", filePath);
            }

            PostListResponseDTO responseDTO = postService.create(requestDTO, userInfo, filePath);
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
                    .body(PostListResponseDTO
                            .builder()
                            .build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //     게시글 목록 요청
//    @GetMapping
//    public ResponseEntity<?> retrievePostList(
//            // JwtAuthFilter에서 시큐리티에게 전역적으로 사용할 수 있는 인증 정보를 등록해 놓았기 때문에
//            // @AuthenticationPrincipal을 통해 토큰에 인증된 사용자 정보를 불러올 수 있다.
//            @AuthenticationPrincipal TokenUserInfo userInfo
//            ) {
//
////        log.info("/api/posts GET request");
//        PostListResponseDTO responseDTO = PostService.retrieve(userInfo.getUserId());
////        log.info("responseDTO=={}", responseDTO);
//        return ResponseEntity.ok().body(responseDTO);
//    }
    @GetMapping
    public ResponseEntity<?> retrievePostList(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
        PostListResponseDTO responseDTO = postService.retrieve(userInfo.getUserId());
        return ResponseEntity.ok().body(responseDTO);
    }

    // 프로필 사진 이미지 데이터를 클라이언트에게 응답 처리
    @GetMapping("/load-postImg")
    public ResponseEntity<?> loadFile(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
//        log.info("/api/auth/load-profile - GET!, user: {}", userInfo.getEmail());

        try {
            log.info("\n\n\n 가자");
            // 클라이언트가 요청한 게시판 사진을 응답해야 함.

            List<String> filePaths
                    = postService.findPostImgPath(userInfo.getUserId());
            List<byte[]> fileDataList = new ArrayList<>();
            for (String filePath : filePaths) {

                // 2. 얻어낸 파일 경로를 통해 실제 파일 데이터를 로드하기.
                File postFile = new File(filePath);

                byte[] fileData = FileCopyUtils.copyToByteArray(postFile);
                fileDataList.add(fileData);
                System.out.println("가자");
            }
            return ResponseEntity.ok().body(fileDataList);

//            // 모든 사용자가 프로필 사진을 가지는 것은 아니다. -> 프사가 없는 사람들은 경로가 존재하지 않을 것이다.
//            // 만약 존재하지 않는 경로라면 클라이언트로 404 status를 리턴.
//            if(!profileFile.exists()) {
//                if(filePath.startsWith("http://")) {
//                    return ResponseEntity.ok().body(filePath);
//                }
//                return ResponseEntity.notFound().build();
//            }
//
//            // 해당 경로에 저장된 파일을 바이트 배열로 직렬화 해서 리턴.
//            byte[] fileData = FileCopyUtils.copyToByteArray(profileFile);
//
//            // 3. 응답 헤더에 컨텐츠 타입을 설정.
//            HttpHeaders headers = new HttpHeaders();
//            MediaType contentType = findExtensionAndGetMediaType(filePath);
//            if(contentType == null) {
//                return ResponseEntity.internalServerError()
//                        .body("발견된 파일은 이미지 파일이 아닙니다.");
//            }
//            headers.setContentType(contentType);
//
//            return ResponseEntity.ok()
//                    .headers(headers)
//                    .body(fileData);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError()
//                    .body("파일을 찾을 수 없습니다.");
//        }
        } catch (IOException e) {
            log.info("\n\n\n\n 가자 !!");

            throw new RuntimeException(e);

        }
    }

    //    @GetMapping
//    public ResponseEntity<?> snsBoardList() {
//        List<PostDetailResponseDTO> snsList = PostService.getAllList();
//
//        return ResponseEntity.ok().body(snsList);
//    }
    // 할 일 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable("id") String postId
    ) {
//        log.info("/api/posts/{} DELETE request!", postId);

        if (postId == null || postId.trim().equals("")) {
            return ResponseEntity
                    .badRequest()
                    .body(PostListResponseDTO
                            .builder()
//                            .error("ID를 전달해 주세요.")
                            .build());
        }

        try {
            PostListResponseDTO responseDTO = postService.delete(postId, userInfo.getUserId());
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError().body(PostListResponseDTO.builder().build());
        }
    }


    // 할 일 수정하기
//    @RequestMapping(method = {RequestMethod.PATCH, RequestMethod.PUT})
//    public ResponseEntity<?> updatePost(
//            @AuthenticationPrincipal TokenUserInfo userInfo,
//            @Validated @RequestBody PostModifyRequestDTO requestDTO,
//            BindingResult result,
//            HttpServletRequest request
//    ) {
//        if(result.hasErrors()) {
//            return ResponseEntity.badRequest().body(result.getFieldError());
//        }
//
//        log.info("/api/posts {} request!", request.getMethod());
//        log.info("modifying dto: {}", requestDTO);
//
//        try {
//            PostListResponseDTO responseDTO = PostService.update(requestDTO, userInfo.getUserId());
//            return ResponseEntity.ok().body(responseDTO);
//        } catch (RuntimeException e) {
//            return ResponseEntity
//                    .internalServerError()
//                    .body(PostListResponseDTO
//                            .builder()
//                            .error(e.getMessage())
//                            .build());
//        }
//    }

//    @GetMapping("/comment")
//    public String postIdContain() {
//        return
//    }
    @PostMapping("/comment/{postId}")
    public void createComment(
//            @PathVariable("postId") String postId,
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody CommentCreateRequestDTO dto
    ){
        postService.commentRegist(dto, userInfo);
    }
}
