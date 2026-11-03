package com.travelblog.app.service.impl;

import com.travelblog.app.dto.blog.BlogCreateRequest;
import com.travelblog.app.dto.blog.BlogResponse;
import com.travelblog.app.dto.blog.BlogUpdateRequest;
import com.travelblog.app.entity.Blog;
import com.travelblog.app.entity.User;
import com.travelblog.app.mapper.BlogMapper;
import com.travelblog.app.repository.BlogRepository;
import com.travelblog.app.repository.UserRepository;
import com.travelblog.app.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final BlogMapper blogMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<BlogResponse> getAllBlogs(Pageable pageable) {
        return blogRepository.findAll(pageable)
                .map(blogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogResponse getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));
        return blogMapper.toResponse(blog);
    }

    @Override
    @Transactional
    public BlogResponse createBlog(BlogCreateRequest request, Long currentUserId) {
        User author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + currentUserId));

        Blog blog = blogMapper.toEntity(request);
        blog.setAuthor(author);

        Blog savedBlog = blogRepository.save(blog);
        return blogMapper.toResponse(savedBlog);
    }

    @Override
    @Transactional
    public BlogResponse updateBlog(Long id, BlogUpdateRequest request, Long currentUserId) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));

        if (!blog.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to update this blog");
        }

        blogMapper.updateEntityFromRequest(request, blog);
        return blogMapper.toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    public void deleteBlog(Long id, Long currentUserId) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + id));

        if (!blog.getAuthor().getId().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to delete this blog");
        }

        blogRepository.delete(blog);
    }

    @Override
    @Transactional
    public void favoriteBlog(Long blogId, Long currentUserId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + blogId));
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + currentUserId));

        user.getFavorites().add(blog);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unfavoriteBlog(Long blogId, Long currentUserId) {
        Blog blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new RuntimeException("Blog not found with id: " + blogId));
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + currentUserId));

        user.getFavorites().remove(blog);
        userRepository.save(user);
    }
}