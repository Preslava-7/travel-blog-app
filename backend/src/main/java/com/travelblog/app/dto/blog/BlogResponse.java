package com.travelblog.app.dto.blog;

import com.travelblog.app.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlogResponse {

    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorUsername;
    private int favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
