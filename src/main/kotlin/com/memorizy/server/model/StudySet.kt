package com.memorizy.server.model

import jakarta.persistence.*

@Entity
@Table(name = "study_sets")
data class StudySet(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val name: String,

    @Column
    val description: String? = null,

    @Column(nullable = false)
    val iconId: Int,

    // FetchType.LAZY - грузит только набор, данные пользователя если попросим
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
)