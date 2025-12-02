package com.memorizy.server.model

import jakarta.persistence.*

@Entity
@Table(name = "cards")
data class Card(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val term: String,

    @Column(nullable = false)
    val definition: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_set_id", nullable = false)
    val studySet: StudySet
)