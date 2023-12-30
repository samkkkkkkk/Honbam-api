package com.example.HonBam.postapi.service;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.postapi.dto.request.CommentCreateRequestDTO;
import com.example.HonBam.postapi.dto.request.PostCreateRequestDTO;
import com.example.HonBam.postapi.dto.response.PostDetailResponseDTO;
import com.example.HonBam.postapi.dto.response.PostListResponseDTO;
import com.example.HonBam.postapi.entity.Post;
import com.example.HonBam.postapi.repository.CommentRepository;
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
        return retrieve(userInfo.getUserId());
    }


    public PostListResponseDTO retrieve(String userId) {

        // 로그인 한 유저의 정보를 데이터베이스 조회
        User user = getUser(userId);

        List<Post> entityList = PostRepository.findAll();

        List<PostDetailResponseDTO> dtoList
                = entityList.stream()
                .map(PostDetailResponseDTO::new)
                .collect(Collectors.toList());
//        System.out.println("dtoList===" + dtoList);

//        for(Post post : entityList) {
//            post.getPostImg()
//        }
//        File postFile = new File(filePath);
//
//        byte[] fileData = FileCopyUtils.copyToByteArray(postFile);
//        fileDataList.add(fileData);

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

    public PostListResponseDTO delete(final String postId, final String userId) {
        try {
            PostRepository.deleteById(postId);
        } catch (Exception e) {
            log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}, err: {}"
                    , postId, e.getMessage());
            throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
        }
        return retrieve(userId);
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

    public void commentRegist(
//            final String postId,
            final CommentCreateRequestDTO dto,
            final TokenUserInfo userInfo
    ) {

        User user = getUser(userInfo.getUserId());
//        commentRepository.save(dto.toEntity(user,post));
        
    }


//    public List<PostListResponseDTO> getAllList() {
//        List<Post> findList = PostRepository.findAll();
//        return findList.stream().map(PostListResponseDTO::new).collect(Collectors.toList());
//
//    }

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











