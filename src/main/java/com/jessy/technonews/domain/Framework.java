package com.jessy.technonews.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "T_FRAMEWORKS")
public class Framework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Size(min = 3, max = 25)
    private String name;
    private String image;
    @JsonBackReference(value = "frameworkPages")
    @OneToMany(
            targetEntity = FrameworkPage.class,
            mappedBy = "framework",
            cascade = CascadeType.ALL)
    private List<FrameworkPage> frameworkPages;
}
