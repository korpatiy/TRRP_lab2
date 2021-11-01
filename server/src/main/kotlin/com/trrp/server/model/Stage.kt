package com.trrp.server.model

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "t_stage")
data class Stage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stage_id")
    val id: Long = 0L,

    @Column(name = "stage_name")
    val name: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "champ_id", referencedColumnName = "champ_id")
    val championship: Championship? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", referencedColumnName = "city_id")
    val city: City? = null,

    @Column(name = "start_date")
    val startDate: LocalDate = LocalDate.MIN,

    @Column(name = "end_date")
    val endDate: LocalDate = LocalDate.MIN,
)