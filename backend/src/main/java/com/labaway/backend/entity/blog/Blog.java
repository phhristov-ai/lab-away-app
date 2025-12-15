package com.labaway.backend.entity.blog;

import com.labaway.backend.entity.category.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"translations", "categories"})
@Entity
@Table(name = "blogs")
public class Blog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String slug;

    private String author;

    private String imageUrlSmall;
    private String imageUrlMedium;
    private String imageUrlLarge;

    @ManyToMany
    @JoinTable(
            name = "blog_categories",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<BlogTranslation> translations = new HashSet<>();

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Blog)) return false;
        Blog blog = (Blog) o;
        return id != null && id.equals(blog.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
