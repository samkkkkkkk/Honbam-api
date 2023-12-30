package com.example.HonBam.HonBamapi.service;

import com.example.HonBam.auth.TokenUserInfo;
import com.example.HonBam.HonBamapi.dto.request.HonBamCreateRequestDTO;
import com.example.HonBam.HonBamapi.dto.request.HonBamModifyRequestDTO;
import com.example.HonBam.HonBamapi.dto.response.HonBamDetailResponseDTO;
import com.example.HonBam.HonBamapi.dto.response.HonBamListResponseDTO;
import com.example.HonBam.HonBamapi.entity.HonBam;
import com.example.HonBam.HonBamapi.repository.HonBamRepository;
import com.example.HonBam.userapi.entity.Role;
import com.example.HonBam.userapi.entity.User;
import com.example.HonBam.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class HonBamService {

    private final HonBamRepository HonBamRepository;
    private final UserRepository userRepository;

    public HonBamListResponseDTO create(
            final HonBamCreateRequestDTO requestDTO,
            final TokenUserInfo userInfo
            )
            throws RuntimeException {

        // 이제는 할 일 등록은 회원만 할 수 있도록 세팅하기 때문에
        // toEntity의 매개값으로 User 엔터티도 함께 전달해야 합니다. -> userId로 회원 엔터티를 조회해야 함.
        User user = getUser(userInfo.getUserId());

        // 권한에 따른 글쓰기 제한 처리
        // 일반 회원이 일정을 5개 초과해서 작성하면 예외를 발생.
        if(userInfo.getRole() == Role.COMMON
                && HonBamRepository.countByUser(user) >= 5) {
            throw new IllegalStateException("일반회원은 더 이상 일정을 작성할 수 없습니다.");
        }

        HonBamRepository.save(requestDTO.toEntity(user));
        log.info("할 일 저장 완료! 제목: {}", requestDTO.getTitle());
        return retrieve(userInfo.getUserId());
    }

    public HonBamListResponseDTO retrieve(String userId) {

        // 로그인 한 유저의 정보를 데이터베이스 조회
        User user = getUser(userId);

        List<HonBam> entityList = HonBamRepository.findAllByUser(user);

        List<HonBamDetailResponseDTO> dtoList
                = entityList.stream()
                /*.map(HonBam -> new HonBamDetailResponseDTO(HonBam))*/
                .map(HonBamDetailResponseDTO::new)
                .collect(Collectors.toList());

        return HonBamListResponseDTO.builder()
                .HonBams(dtoList)
                .build();
    }

    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }

    public HonBamListResponseDTO delete(final String HonBamId, final String userId) {
        try {
            HonBamRepository.deleteById(HonBamId);
        } catch (Exception e) {
            log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}, err: {}"
                    , HonBamId, e.getMessage());
            throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
        }
        return retrieve(userId);
    }

    public HonBamListResponseDTO update(final HonBamModifyRequestDTO requestDTO, final String userId)
        throws RuntimeException {
        Optional<HonBam> targetEntity
                = HonBamRepository.findById(requestDTO.getId());

        targetEntity.ifPresent(HonBam -> {
            HonBam.setDone(requestDTO.isDone());

            HonBamRepository.save(HonBam);
        });

        return retrieve(userId);
    }


}











