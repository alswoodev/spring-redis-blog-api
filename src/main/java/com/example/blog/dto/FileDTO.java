package com.example.blog.dto;

import java.util.Date;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    //To use DTO for insert, make 'id' field Nullable
    private Integer id;
    private String path;
    private String name;
    private String extension;
    private Date createTime;

    //FK
    private Long postId;
    
    // Use value-based comparison (equals/hashCode) instead of reference equality
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileDTO fileDTO = (FileDTO) o;

        return Objects.equals(path, fileDTO.path) &&
            Objects.equals(name, fileDTO.name) &&
            Objects.equals(extension, fileDTO.extension) &&
            Objects.equals(postId, fileDTO.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name, extension, postId);
    }
}
