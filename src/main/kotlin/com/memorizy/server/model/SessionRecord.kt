package com.memorizy.server.model

import jakarta.persistence.*

// Сущность записи учебной сессии

@Entity
@Table(name = "session_records")
data class SessionRecord(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val correctCount: Int,

    @Column(nullable = false)
    val totalCount: Int,

    @Column(nullable = false)
    val percentage: Float,

    @Column(nullable = false, updatable = false)
    val timestamp: Long = System.currentTimeMillis(),

    // Много записей сессий может принадлежать одному набору
    // FetchType.LAZY - если берем запись из БД не грузим информацию о наборе
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_set_id", nullable = false)
    val studySet: StudySet
)