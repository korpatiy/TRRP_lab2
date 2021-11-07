package com.trrp.server.model.entity


import javax.persistence.*

@Entity
@Table(name = "t_discipline")
data class Discipline(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disc_id")
    val id: Long = 0L,

    @Column(name = "disc_name")
    val name: String = "",

    @Column(name = "number_of_fire_lines")
    val nFireLines: Int = 0,

    @Column(name = "fine_unit")
    val fine: Int = 0
)