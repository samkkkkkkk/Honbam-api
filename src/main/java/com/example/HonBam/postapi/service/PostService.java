package com.example.HonBam.postapi.service;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.postapi.dto.request.CommentCreateRequestDTO;
import com.example.HonBam.postapi.dto.request.ModifyRequestDTO;
import com.example.HonBam.postapi.dto.request.PostCreateRequestDTO;
import com.example.HonBam.postapi.dto.response.PostDetailResponseDTO;
import com.example.HonBam.postapi.dto.response.PostListResponseDTO;
import com.example.HonBam.postapi.dto.response.RegisterLikeDTO;
import com.example.HonBam.postapi.entity.Comment;
import com.example.HonBam.postapi.entity.Post;
import com.example.HonBam.postapi.repository.CommentRepository;
import com.example.HonBam.postapi.repository.LikeRepository;
import com.example.HonBam.postapi.repository.PostRepository;
import com.example.HonBam.userapi.entity.User;
import com.example.HonBam.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostService {

    @Value("${upload.path}")
    private String uploadRootPath;

    private final PostRepository PostRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostListResponseDTO create(
            final PostCreateRequestDTO requestDTO,
            final TokenUserInfo userInfo,
            final String filePath
    )
            throws RuntimeException {

        User user = getUser(userInfo.getUserId());
//        log.info("userL {}", user);

        PostRepository.save(requestDTO.toEntity(user, filePath));
//        log.info("게시글 저장 완료!: {}", requestDTO.getContent());
        return retrieve();
    }

    // 게시글 리스트 불러오기
    public PostListResponseDTO retrieve() {

        List<Post> entityList = PostRepository.findAll();

        List<PostDetailResponseDTO> dtoList
                = entityList.stream()
                .map(PostDetailResponseDTO::new)
                .collect(Collectors.toList());

  return PostListResponseDTO.builder()
                .posts(dtoList)
                .build();
    }

    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }

    public PostListResponseDTO delete(final String postId) {
        try {

            List<Long> commentId = PostRepository.findById(postId).orElseThrow().getCommentList()
                    .stream().map(Comment::getCommentID).collect(Collectors.toList());

            for( Long id : commentId) {
                commentRepository.deleteById(id);
            }

            PostRepository.deleteById(postId);
        } catch (Exception e) {
            log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}, err: {}"
                    , postId, e.getMessage());
            throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
        }
        return retrieve();
    }

    public String uploadFileImg(MultipartFile postImg) throws IOException {

        // 루트 디렉토리가 실존하는 지 확인 후 존재하지 않으면 생성.
        File rootDir = new File(uploadRootPath);
        if(!rootDir.exists()) rootDir.mkdirs();

        // 파일명을 유니크하게 변경 (이름 충돌 가능성을 대비)
        // UUID와 원본파일명을 혼합. -> 규칙은 없어요.
        String uniqueFileName
                = UUID.randomUUID() + "_" + postImg.getOriginalFilename();

        // 파일을 저장
        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
        postImg.transferTo(uploadFile);

        return uploadRootPath + '/' + uniqueFileName;
    }

    public List<String> findPostImgPath(String postId) {
//        Post post = PostRepository.findAll().orElseThrow();
        List<Post> post = PostRepository.findAll();
        List<String> postImgPath = new ArrayList<>();
        for (Post postImg : post) {
            postImgPath.add(uploadRootPath + "/" + postImg.getPostImg());
        }


        // DB에 저장되는 profile_img는 파일명. -> service가 가지고 있는 Root Path와 연결해서 리턴.
        return postImgPath;

    }

    public List<Comment> commentRegist(
//            final String postId,
            final CommentCreateRequestDTO dto,
            final TokenUserInfo userInfo
    ) {

        User user = getUser(userInfo.getUserId());
        Post post = PostRepository.findById(dto.getPostId()).orElseThrow();
        commentRepository.save(dto.toEntity(user, post));
        return post.getCommentList();


        
    }

    // 목록 요청
    public List<Comment> commentList(String postId) {
        return PostRepository.findById(postId).orElseThrow().getCommentList();
    }

    // 삭제요청
    public List<Comment> commentDelete(TokenUserInfo userInfo, Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        commentRepository.deleteById(id);
        return commentList(comment.getPost().getPostId());
    }

    public boolean validateWriter(TokenUserInfo userInfo, Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        return comment.getUserId().equals(userInfo.getUserId());
    }

    public boolean validateWriter(TokenUserInfo userInfo, String id) {
        Post post = PostRepository.findById(id).orElseThrow();
        return post.getUser().getId().equals(userInfo.getUserId());
    }

    public List<Comment> modify(ModifyRequestDTO requestDTO) {

        Comment comment = commentRepository.findById(requestDTO.getCommentId()).orElseThrow();
        comment.setComment(requestDTO.getComment());
        comment.setUpdateTime(LocalDateTime.now());

        Comment save = commentRepository.save(comment);
        return commentList(save.getPost().getPostId());
    }

    public void registerLike(String postId, TokenUserInfo userInfo) {

        Post post = PostRepository.findById(postId).orElseThrow();

//        RegisterLikeDTO

    }




//    public PostListResponseDTO update(final PostModifyRequestDTO requestDTO, final String userId)
//        throws RuntimeException {
//        Optional<Post> targetEntity
//                = PostRepository.findById(requestDTO.getId());
//
//        targetEntity.ifPresent(post -> {
//            post.setDone(requestDTO.isDone());
//
//            PostRepository.save(post);
//        });
//
//        return retrieve(userId);
//    }


}











