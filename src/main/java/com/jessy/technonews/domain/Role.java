package com.jessy.technonews.domain;

import java.util.Collection;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

@Entity @Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "`T_ROLES`")
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "role_name", nullable = false)
    private String name;
    @JsonBackReference
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
}
