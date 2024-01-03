package com.example.HonBam.freeboardapi.Service;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.freeboardapi.dto.request.CommentModifyRequestDTO;
import com.example.HonBam.freeboardapi.dto.request.FreeboardCommentRequestDTO;
import com.example.HonBam.freeboardapi.dto.request.FreeboardRequestDTO;
import com.example.HonBam.freeboardapi.dto.response.FreeboardDetailResponseDTO;
import com.example.HonBam.freeboardapi.dto.response.FreeboardResponseDTO;
import com.example.HonBam.freeboardapi.entity.Freeboard;
import com.example.HonBam.freeboardapi.entity.FreeboardComment;
import com.example.HonBam.freeboardapi.repository.FreeboardCommentRepository;
import com.example.HonBam.freeboardapi.repository.FreeboardRepository;
import com.example.HonBam.postapi.entity.Post;
import com.example.HonBam.userapi.entity.User;
import com.example.HonBam.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FreeboardService {

    private final FreeboardRepository freeboardRepository;
    private final UserRepository userRepository;
    private final FreeboardCommentRepository freeboardCommentRepository;


    public FreeboardResponseDTO createContent(
            final FreeboardRequestDTO requestDto,
            final TokenUserInfo userInfo) {

        User user = getUser(userInfo.getUserId());
        freeboardRepository.save(requestDto.toEntity(user));
        return retrieve();
    }

    private User getUser(String userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );

    }

    public FreeboardDetailResponseDTO getContent(Long id) {


        Freeboard freeboard = freeboardRepository.findById(id).orElseThrow(
                () -> new RuntimeException(id + "번 게시물이 존재하지 않습니다.")
        );

        return new FreeboardDetailResponseDTO((freeboard));
    }

    public FreeboardResponseDTO retrieve() {

//        // 로그인 한 유저의 정보를 데이터베이스 조회
//        User user = getUser(userId);

        List<Freeboard> entityList = freeboardRepository.findAll();

        List<FreeboardDetailResponseDTO> dtoList
                = entityList.stream()
                .map(FreeboardDetailResponseDTO::new)
                .collect(Collectors.toList());

        return FreeboardResponseDTO
                .builder()
                .count(dtoList.size())
                .posts(dtoList)
                .build();
    }


    public FreeboardResponseDTO delete(final String userId, final Long id) {

        freeboardRepository.deleteById(id);
        return retrieve();

    }


    // 게시글 상세보기
    public FreeboardDetailResponseDTO modify(TokenUserInfo userInfo, Long id, FreeboardRequestDTO requestDTO) {
        User user = getUser(userInfo.getUserId());
//        freeboardRepository.save(requestDTO.toEntity(user));
        Freeboard foundContents = freeboardRepository.findById(id).orElseThrow();

        Freeboard entity = requestDTO.toEntity(foundContents, user);
        entity.setUpdateDate(LocalDateTime.now());
        Freeboard save = freeboardRepository.save(entity);
        return new FreeboardDetailResponseDTO(save);
    }
    
    // 댓글 서비스 시작

    // 댓글 등록
    public List<FreeboardComment> commentRegist(
//         final String postId,
            final FreeboardCommentRequestDTO dto,
            final TokenUserInfo userInfo
    ) {

        User user = getUser(userInfo.getUserId());
        Freeboard freeboard = freeboardRepository.findById(dto.getId()).orElseThrow();
        freeboardCommentRepository.save(dto.toEntity(user, freeboard));
        return freeboard.getCommentList();

    }

    // 목록 요청
    public List<FreeboardComment> commentList(Long id) {
        return freeboardRepository.findById(id).orElseThrow().getCommentList();
    }

    // 삭제요청
    public List<FreeboardComment> commentDelete(TokenUserInfo userInfo, Long id) {
        FreeboardComment comment = freeboardCommentRepository.findById(id).orElseThrow();
        freeboardRepository.deleteById(id);
        return commentList(comment.getFreeboard().getId());
    }

    public boolean validateWriter(TokenUserInfo userInfo, Long id) {
        if(freeboardCommentRepository.findById(id).isPresent()) {
            FreeboardComment comment = freeboardCommentRepository.findById(id).orElseThrow();
            return comment.getUserId().equals(userInfo.getUserId());
        }
        if(freeboardRepository.findById(id).isPresent()){
            Freeboard freeboard = freeboardRepository.findById(id).orElseThrow();
            return freeboard.getUser().getId().equals(userInfo.getUserId());
        }
        return false;
    }

//    public boolean validateWriter2(TokenUserInfo userInfo, Long id) {
//        Freeboard freeboard = freeboardRepository.findById(id).orElseThrow();
//        return freeboard.getUser().getId().equals(userInfo.getUserId());
//    }

    // 댓글 수정
    public List<FreeboardComment> modify(CommentModifyRequestDTO requestDTO) {

        FreeboardComment comment = freeboardCommentRepository.findById(requestDTO.getId()).orElseThrow();
        comment.setComment(requestDTO.getComment());
        comment.setUpdateTime(LocalDateTime.now());

        FreeboardComment save = freeboardCommentRepository.save(comment);
        return commentList(save.getFreeboard().getId());
    }
}
