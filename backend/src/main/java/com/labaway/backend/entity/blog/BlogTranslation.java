package com.labaway.backend.entity.blog;

import com.labaway.backend.enums.Language;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "blog_translations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"blog_id", "language"}))
public class BlogTranslation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Column(name = "reading_time")
    private Integer readingTime;

    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlogTranslation)) return false;
        BlogTranslation that = (BlogTranslation) o;
        return Objects.equals(getBlogId(blog), getBlogId(that.blog)) && language == that.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBlogId(blog), language);
    }

    private UUID getBlogId(Blog blog) {
        return blog != null ? blog.getId() : null;
    }
}
