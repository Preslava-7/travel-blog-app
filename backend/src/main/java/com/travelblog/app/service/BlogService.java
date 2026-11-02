package com.travelblog.app.service;

import com.travelblog.app.dto.blog.BlogCreateRequest;
import com.travelblog.app.dto.blog.BlogResponse;
import com.travelblog.app.dto.blog.BlogUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogService {

    Page<BlogResponse> getAllBlogs(Pageable pageable);

    BlogResponse getBlogById(Long id);

    BlogResponse createBlog(BlogCreateRequest request, Long currentUserId);

    BlogResponse updateBlog(Long id, BlogUpdateRequest request, Long currentUserId);

    void deleteBlog(Long id, Long currentUserId);

    void favoriteBlog(Long blogId, Long currentUserId);

    void unfavoriteBlog(Long blogId, Long currentUserId);
}
