package com.memorizy.server.model

import jakarta.persistence.*

// The entity for which Hibernate will create the tables itself

@Entity
@Table(name = "study_sets")
data class StudySet(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assign database identity column
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column
    val description: String? = null,

    @Column(nullable = false)
    val iconId: Int,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    // Много наборов может принадлежать одному пользователю
    // FetchType.LAZY - если берем набор из БД не грузим сразу информацию о пользователе
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    // У одного набора может быть много карточек
    // При удалении набора удалять всех его детей
    @OneToMany(mappedBy = "studySet", cascade = [CascadeType.ALL], orphanRemoval = true)
    val cards: List<Card> = emptyList()
)