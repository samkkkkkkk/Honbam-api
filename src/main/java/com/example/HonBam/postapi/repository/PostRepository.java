package com.example.HonBam.postapi.repository;

import com.example.HonBam.postapi.entity.Post;
import com.example.HonBam.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository
    extends JpaRepository<Post, String> {

    // 특정 회원의 할 일 목록 리턴
    // SELECT * FROM tbl_HonBam WHERE user_id = ?
    @Query("SELECT t FROM HonBam t WHERE t.user = :user")
    List<Post> findAllByUser(@Param("user") User user);

    // 회원이 작성한 일정의 개수를 리턴
    @Query("SELECT COUNT(*) FROM HonBam t WHERE t.user= :user")
    int countByUser(@Param("user") User user);


}










