package com.memorizy.server.model

import jakarta.persistence.*

// Сущность карточка

@Entity
@Table(name = "cards")
data class Card(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // The persistence provider должен назначить первичные ключи для объекта, используя столбец id.
    val id: Long = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    val term: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val definition: String,

    @Column(nullable = false, columnDefinition = "TEXT")    // Указываем columnDefinition чтобы не поставило VARCHAR255
    val definitionVariantsJson: String = "[]",

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    @Column(nullable = false)
    val level: Int = 0,

    @Column(nullable = false)
    val nextReviewDate: Long = System.currentTimeMillis(),

    @Column(nullable = false)
    val reviewCount: Int = 0,

    @Column(nullable = false)
    val mistakeCount: Int = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    val recentAnswerHistory: String = "",

    // Много карточек может принадлежать одному набору
    // FetchType.LAZY - если берем карточку из БД не грузим информацию о наборе
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_set_id", nullable = false)
    val studySet: StudySet
)