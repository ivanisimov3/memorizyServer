package com.memorizy.server.model

import jakarta.persistence.*

// Сущность набор

@Entity
@Table(name = "study_sets")
data class StudySet(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // The persistence provider должен назначить первичные ключи для объекта, используя столбец id.
    val id: Long = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    val name: String,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(nullable = false)
    val iconId: Int,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    @Column
    val targetDate: Long? = null,

    // Много наборов может принадлежать одному пользователю
    // FetchType.LAZY - если берем набор из БД не грузим информацию о пользователе
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    // У одного набора может быть много карточек
    // При удалении набора удалять всех его детей
    @OneToMany(mappedBy = "studySet", cascade = [CascadeType.ALL], orphanRemoval = true)
    val cards: List<Card> = emptyList()
)