package com.trrp.server.model.entity

import javax.persistence.*

@Entity
@Table(name = "t_country")
data class Country(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id")
    val id: Long = 0L,

    @Column(name = "country_name")
    var name: String = ""
)