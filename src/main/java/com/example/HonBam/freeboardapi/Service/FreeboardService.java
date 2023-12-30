package com.example.HonBam.freeboardapi.Service;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.freeboardapi.dto.request.FreeboardRequestDTO;
import com.example.HonBam.freeboardapi.dto.response.FreeboardDetailResponseDTO;
import com.example.HonBam.freeboardapi.dto.response.FreeboardResponseDTO;
import com.example.HonBam.freeboardapi.entity.Freeboard;
import com.example.HonBam.freeboardapi.repository.FreeboardRepository;
import com.example.HonBam.postapi.dto.response.PostDetailResponseDTO;
import com.example.HonBam.postapi.dto.response.PostListResponseDTO;
import com.example.HonBam.postapi.entity.Post;
import com.example.HonBam.userapi.entity.User;
import com.example.HonBam.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FreeboardService {

    private final FreeboardRepository freeboardRepository;
    private final UserRepository userRepository;


    public FreeboardResponseDTO createContent(
            final FreeboardRequestDTO requestDto,
            final TokenUserInfo userInfo) {

        User user = getUser(userInfo.getUserId());
        freeboardRepository.save(requestDto.toEntity(user));
        return retrieve(userInfo.getUserId());
    }

    private User getUser(String userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
    }

    public FreeboardResponseDTO retrieve(String userId) {

        // 로그인 한 유저의 정보를 데이터베이스 조회
        User user = getUser(userId);

        List<Freeboard> entityList = freeboardRepository.findAll();

        List<FreeboardDetailResponseDTO> dtoList
                = entityList.stream()
                .map(FreeboardDetailResponseDTO::new)
                .collect(Collectors.toList());

        return FreeboardResponseDTO
                .builder()
                .posts(dtoList)
                .build();
    }


    public FreeboardResponseDTO delete(final String userId, final Long id) {

        freeboardRepository.deleteById(id);
        return retrieve(userId);

    }


    public Freeboard modify(TokenUserInfo userInfo, Long id, FreeboardRequestDTO requestDTO) {
        Freeboard found = freeboardRepository.findById(id).orElseThrow();
        User user = getUser(userInfo.getUserId());
        freeboardRepository.save(requestDTO.toEntity(user));
        return ;
    }
}
