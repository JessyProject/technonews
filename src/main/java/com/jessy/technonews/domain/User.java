package com.jessy.technonews.domain;

import javax.persistence.*;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.NotNull;
import lombok.*;

import java.util.List;


@Entity @Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "`T_USERS`")
@NamedQuery(name="User.findDisabled", query="SELECT u FROM User u WHERE u.enable = false")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Size(min = 5, max = 15)
    private String username;
    @NotNull
    @Size(max = 70)
    private String email;
    @NotNull
    @Size(min = 8)
    private String password;
    @Column(columnDefinition = "boolean default true")
    private boolean enable;
    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "`T_USERS_ROLES`")
    private List<Role> roles;
}
