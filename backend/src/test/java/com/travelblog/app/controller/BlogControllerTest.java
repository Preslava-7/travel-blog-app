package com.travelblog.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelblog.app.dto.blog.BlogCreateRequest;
import com.travelblog.app.dto.blog.BlogResponse;
import com.travelblog.app.dto.blog.BlogUpdateRequest;
import com.travelblog.app.security.UserPrincipal;
import com.travelblog.app.service.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BlogControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private BlogService blogService;

    @InjectMocks
    private BlogController blogController;

    private BlogResponse testResponse;
    private UserPrincipal testUserPrincipal;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        testResponse = new BlogResponse();
        testResponse.setId(1L);
        testResponse.setTitle("Test Blog Title");
        testResponse.setContent("Test Blog Content");

        testUserPrincipal = new UserPrincipal(1L, "test@example.com", "password", Collections.emptyList());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testUserPrincipal, null, testUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc = MockMvcBuilders.standaloneSetup(blogController)
                .setCustomArgumentResolvers(
                        new AuthenticationPrincipalArgumentResolver(),
                        new PageableHandlerMethodArgumentResolver()
                )
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/blogs - Should return paged blogs")
    void getAllBlogs_ShouldReturnPagedBlogs() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<BlogResponse> pagedResponse = new PageImpl<>(List.of(testResponse), pageable, 1);

        when(blogService.getAllBlogs(any(Pageable.class))).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/blogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test Blog Title"));

        verify(blogService).getAllBlogs(any(Pageable.class));
    }

    @Test
    @DisplayName("GET /api/v1/blogs/{id} - Should return single blog")
    void getBlogById_ShouldReturnBlog() throws Exception {
        when(blogService.getBlogById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/blogs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Blog Title"));

        verify(blogService).getBlogById(1L);
    }

    @Test
    @DisplayName("POST /api/v1/blogs - Should create blog using principal ID")
    void createBlog_ShouldReturn201() throws Exception {
        BlogCreateRequest request = new BlogCreateRequest();
        request.setTitle("Test Blog Title");
        request.setContent("Test Blog Content");

        when(blogService.createBlog(any(BlogCreateRequest.class), anyLong())).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/blogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Blog Title"));

        verify(blogService).createBlog(any(BlogCreateRequest.class), eq(1L));
    }

    @Test
    @DisplayName("PUT /api/v1/blogs/{id} - Should update blog using principal ID")
    void updateBlog_ShouldReturnUpdatedBlog() throws Exception {
        BlogUpdateRequest request = new BlogUpdateRequest();
        request.setTitle("Updated Title");
        request.setContent("Updated Content");

        BlogResponse updatedResponse = new BlogResponse();
        updatedResponse.setId(1L);
        updatedResponse.setTitle("Updated Title");
        updatedResponse.setContent("Updated Content");

        when(blogService.updateBlog(eq(1L), any(BlogUpdateRequest.class), anyLong())).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/blogs/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));

        verify(blogService).updateBlog(eq(1L), any(BlogUpdateRequest.class), eq(1L));
    }

    @Test
    @DisplayName("DELETE /api/v1/blogs/{id} - Should delete blog using principal ID")
    void deleteBlog_ShouldReturn204() throws Exception {
        doNothing().when(blogService).deleteBlog(1L, 1L);

        mockMvc.perform(delete("/api/v1/blogs/1"))
                .andExpect(status().isNoContent());

        verify(blogService).deleteBlog(1L, 1L);
    }

    @Test
    @DisplayName("POST /api/v1/blogs/{id}/favorite - Should favorite blog using principal ID")
    void favoriteBlog_ShouldReturn200() throws Exception {
        doNothing().when(blogService).favoriteBlog(1L, 1L);

        mockMvc.perform(post("/api/v1/blogs/1/favorite"))
                .andExpect(status().isOk());

        verify(blogService).favoriteBlog(1L, 1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/blogs/{id}/favorite - Should unfavorite blog using principal ID")
    void unfavoriteBlog_ShouldReturn200() throws Exception {
        doNothing().when(blogService).unfavoriteBlog(1L, 1L);

        mockMvc.perform(delete("/api/v1/blogs/1/favorite"))
                .andExpect(status().isOk());

        verify(blogService).unfavoriteBlog(1L, 1L);
    }
}