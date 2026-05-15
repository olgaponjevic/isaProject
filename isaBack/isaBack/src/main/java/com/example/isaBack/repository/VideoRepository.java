package com.example.isaBack.repository;

import com.example.isaBack.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findAllByOrderByCreatedAtDesc();

    List<Video> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    @Modifying
    @Query("UPDATE Video v SET v.views = v.views + 1 WHERE v.id = :id")
    int incrementViews(@Param("id") Long id);
}