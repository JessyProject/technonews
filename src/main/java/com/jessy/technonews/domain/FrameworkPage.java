package com.jessy.technonews.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "T_FRAMEWORK_PAGES")
public class FrameworkPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonManagedReference(value = "frameworkPages")
    @ManyToOne
    private Framework framework;
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
    private String content;
    private Date date_creation;
}
