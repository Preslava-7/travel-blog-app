package com.travelblog.app.mapper;

import com.travelblog.app.dto.blog.BlogCreateRequest;
import com.travelblog.app.dto.blog.BlogResponse;
import com.travelblog.app.dto.blog.BlogUpdateRequest;
import com.travelblog.app.entity.Blog;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BlogMapper {

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorUsername", source = "author.username")
    @Mapping(target = "favoriteCount", expression = "java(blog.getFavoritedBy() != null ? blog.getFavoritedBy().size() : 0)")
    BlogResponse toResponse(Blog blog);

    Blog toEntity(BlogCreateRequest request);

    void updateEntityFromRequest(BlogUpdateRequest request, @MappingTarget Blog blog);
}