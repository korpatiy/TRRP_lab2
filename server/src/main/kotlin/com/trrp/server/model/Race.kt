package com.trrp.server.model


import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "t_race")
data class Race(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "race_id")
    val id: Long = 0L,

    @Column(name = "sex")
    val sex: String = "",

    @Column(name = "date")
    val date: LocalDate = LocalDate.MIN,

    @Column(name = "start_time")
    val startTime: LocalDateTime = LocalDateTime.now(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", referencedColumnName = "stage_id")
    val stage: Stage? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id", referencedColumnName = "track_id")
    val track: Track? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disc_id", referencedColumnName = "disc_id")
    val discipline: Discipline? = null
)