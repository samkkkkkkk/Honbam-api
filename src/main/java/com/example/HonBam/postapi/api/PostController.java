package com.example.HonBam.postapi.api;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.postapi.dto.request.CommentCreateRequestDTO;
import com.example.HonBam.postapi.dto.request.ModifyRequestDTO;
import com.example.HonBam.postapi.dto.request.PostCreateRequestDTO;
import com.example.HonBam.postapi.dto.response.PostListResponseDTO;
import com.example.HonBam.postapi.entity.Comment;
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





    // 게시글 목록 요청
    @GetMapping
    public ResponseEntity<?> retrievePostList(
            @AuthenticationPrincipal TokenUserInfo userInfo
    ) {
        PostListResponseDTO responseDTO = postService.retrieve();
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


        } catch (IOException e) {
            log.info("\n\n\n\n 가자 !!");

            throw new RuntimeException(e);

        }
    }


    // 게시글 삭제
    @DeleteMapping
    public ResponseEntity<?> deleteContent(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestParam String postId
    ){
        if(!postService.validateWriter(userInfo, postId)) {
            return ResponseEntity.badRequest().body("잘못된 권한 요청입니다.");
        }
        return ResponseEntity.ok().body(postService.delete(postId));
    }

    
    // 댓글 등록
    @PostMapping("/comment")
    public ResponseEntity<List<Comment>> createComment(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody CommentCreateRequestDTO dto
    ){

        return ResponseEntity.ok().body(postService.commentRegist(dto, userInfo));
    }

    // 댓글 목록 요청
    @GetMapping("/comment")
    public ResponseEntity<?> commentLis (
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestParam String postId
            ){
        List<Comment> comments = postService.commentList(postId);
        return ResponseEntity.ok().body(comments);
    }

    // 댓글 삭제 요청
    @DeleteMapping("/comment")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestParam Long id
    ) {
        if(!postService.validateWriter(userInfo, id)) {
            return ResponseEntity.badRequest().body("잘못된 권한 요청입니다.");
        }

        return ResponseEntity.ok().body(postService.commentDelete(userInfo, id));

    }

    // 댓글 수정
    @PutMapping("/comment")
    public ResponseEntity<?> modifyComment(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody ModifyRequestDTO requestDTO
    ){
        if(!postService.validateWriter(userInfo, requestDTO.getCommentId())){
            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
        }
        return ResponseEntity.ok().body(postService.modify(requestDTO));

    }

    // 좋아요 등록
    @PostMapping("/like/{id}")
    public ResponseEntity<?> registLike(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestParam String postId
            ) {
        postService.registerLike(postId, userInfo);

        return null;
    }
}
