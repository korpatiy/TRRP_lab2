package com.trrp.server.model

import javax.persistence.*

@Entity
@Table(name = "t_track")
data class Track(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "track_id")
    val id: Long = 0L,

    @Column(name = "location")
    val location: String = "",

    @Column(name = "length")
    val length: Int = 0
)