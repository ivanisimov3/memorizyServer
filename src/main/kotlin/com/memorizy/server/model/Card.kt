package com.memorizy.server.model

import jakarta.persistence.*

// The entity for which Hibernate will create the tables itself

@Entity
@Table(name = "cards")
data class Card(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assign database identity column
    val id: Long = 0,

    @Column(nullable = false)
    val term: String,

    @Column(nullable = false)
    val definition: String,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    // Много карточек может принадлежать одному набору
    // FetchType.LAZY - если берем карточку из БД не грузим сразу информацию о наборе
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_set_id", nullable = false)
    val studySet: StudySet
)