package com.travelblog.app.service.impl;

import com.travelblog.app.dto.blog.BlogCreateRequest;
import com.travelblog.app.dto.blog.BlogResponse;
import com.travelblog.app.entity.Blog;
import com.travelblog.app.entity.User;
import com.travelblog.app.mapper.BlogMapper;
import com.travelblog.app.repository.BlogRepository;
import com.travelblog.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogServiceImplTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BlogMapper blogMapper;

    @InjectMocks
    private BlogServiceImpl blogService;

    private User testUser;
    private Blog testBlog;
    private BlogResponse testBlogResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("preslava");

        testBlog = new Blog();
        testBlog.setId(100L);
        testBlog.setTitle("Тестово пътуване");
        testBlog.setContent("Съдържание за теста");
        testBlog.setAuthor(testUser);
        testBlog.setFavoritedBy(new HashSet<>());

        testBlogResponse = new BlogResponse();
        testBlogResponse.setId(100L);
        testBlogResponse.setTitle("Тестово пътуване");
    }

    @Test
    void createBlog_Success() {
        BlogCreateRequest request = new BlogCreateRequest();
        request.setTitle("Тестово пътуване");
        request.setContent("Съдържание за теста");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(blogMapper.toEntity(request)).thenReturn(testBlog);
        when(blogRepository.save(any(Blog.class))).thenReturn(testBlog);
        when(blogMapper.toResponse(testBlog)).thenReturn(testBlogResponse);

        BlogResponse result = blogService.createBlog(request, 1L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Тестово пътуване", result.getTitle());

        verify(userRepository, times(1)).findById(1L);
        verify(blogRepository, times(1)).save(testBlog);
    }

    @Test
    void getBlogById_Success() {
        when(blogRepository.findById(100L)).thenReturn(Optional.of(testBlog));
        when(blogMapper.toResponse(testBlog)).thenReturn(testBlogResponse);

        BlogResponse result = blogService.getBlogById(100L);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        verify(blogRepository, times(1)).findById(100L);
    }

    @Test
    void getBlogById_NotFound_ThrowsException() {
        when(blogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> blogService.getBlogById(999L));
        verify(blogRepository, times(1)).findById(999L);
    }

    @Test
    void favoriteBlog_Success() {
        when(blogRepository.findById(100L)).thenReturn(Optional.of(testBlog));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        blogService.favoriteBlog(100L, 1L);

        assertTrue(testBlog.getFavoritedBy().contains(testUser));
        verify(blogRepository, times(1)).save(testBlog);
    }

    @Test
    void deleteBlog_ForbiddenForNonAuthor() {
        User anotherUser = new User();
        anotherUser.setId(2L); // различен от автора (1L)

        when(blogRepository.findById(100L)).thenReturn(Optional.of(testBlog));

        assertThrows(RuntimeException.class, () -> blogService.deleteBlog(100L, 2L));
        verify(blogRepository, never()).delete(any());
    }
}