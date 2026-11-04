package com.travelblog.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travelblog.app.dto.blog.BlogCreateRequest;
import com.travelblog.app.dto.blog.BlogResponse;
import com.travelblog.app.security.JwtUtils;
import com.travelblog.app.service.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BlogController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class BlogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlogService blogService;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private UserDetailsService userDetailsService;

    private BlogResponse testResponse;

    @BeforeEach
    void setUp() {
        testResponse = new BlogResponse();
        testResponse.setId(1L);
        testResponse.setTitle("Тестово заглавие");
        testResponse.setContent("Тестово съдържание");
    }

    @Test
    void getBlogById_ShouldReturn200() throws Exception {
        when(blogService.getBlogById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/blogs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Тестово заглавие"));
    }

    @Test
    void createBlog_ShouldReturn201() throws Exception {
        BlogCreateRequest request = new BlogCreateRequest();
        request.setTitle("Тестово заглавие");
        request.setContent("Тестово съдържание");

        when(blogService.createBlog(any(BlogCreateRequest.class), eq(1L))).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/blogs")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void favoriteBlog_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/v1/blogs/1/favorite")
                        .param("userId", "1"))
                .andExpect(status().isOk());
    }
}