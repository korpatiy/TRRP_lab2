package com.trrp.server.model.entity

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "t_championship")
data class Championship(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "champ_id")
    val id: Long = 0L,

    @Column(name = "champ_name")
    val name: String = "",

    @Column(name = "start_date")
    val startDate: LocalDate = LocalDate.MIN,

    @Column(name = "end_date")
    val endDate: LocalDate = LocalDate.MIN
)