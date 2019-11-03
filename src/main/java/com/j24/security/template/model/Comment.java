package com.j24.security.template.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.bind.annotation.PathVariable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(1000)")
    private String content;

    @CreationTimestamp
    private LocalDateTime additionDate;

    private boolean locked;

    @ManyToOne
    private Participation participation;

    public String additionDateToString() {
        return additionDate.toLocalDate().toString() + " " + additionDate.toLocalTime().toString();
    }


}
