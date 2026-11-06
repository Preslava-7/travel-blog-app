package com.travelblog.app.controller;

import com.travelblog.app.dto.blog.BlogCreateRequest;
import com.travelblog.app.dto.blog.BlogResponse;
import com.travelblog.app.dto.blog.BlogUpdateRequest;
import com.travelblog.app.security.CustomUserDetailsService;
import com.travelblog.app.security.UserPrincipal;
import com.travelblog.app.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<Page<BlogResponse>> getAllBlogs(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(blogService.getAllBlogs(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable Long id){
        return ResponseEntity.ok(blogService.getBlogById(id));
    }

    @PostMapping
    public ResponseEntity<BlogResponse> createBlog(@Valid @RequestBody BlogCreateRequest request,
                                                   @AuthenticationPrincipal UserPrincipal principal){
        Long userId = principal.getId();

        BlogResponse createBlog = blogService.createBlog(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createBlog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogResponse> updateBlog(
            @PathVariable Long id,
            @Valid @RequestBody BlogUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long userId = principal.getId();
        return ResponseEntity.ok(blogService.updateBlog(id,request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id,
                                           @AuthenticationPrincipal UserPrincipal principal){
        Long userId = principal.getId();
        blogService.deleteBlog(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/favorite")
    public ResponseEntity<Void> favoriteBlog(@PathVariable Long id,
                                             @AuthenticationPrincipal UserPrincipal principal){
        Long tempUserId = principal.getId();
        blogService.favoriteBlog(id, tempUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/favorite")
    public ResponseEntity<Void> unfavoriteBlog(@PathVariable Long id,
                                               @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        blogService.unfavoriteBlog(id, userId);
        return ResponseEntity.ok().build();
    }

}
