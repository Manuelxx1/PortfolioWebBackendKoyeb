package com.abml.jpa.hibernate.model;

@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;
  private String password;
  private String email;

  @Column(name = "created_at")
  private LocalDateTime createdAt;
}
