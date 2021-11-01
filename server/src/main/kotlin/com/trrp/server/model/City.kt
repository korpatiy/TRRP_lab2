package com.trrp.server.model

import javax.persistence.*

@Entity
@Table(name = "t_city")
data class City(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    val id: Long = 0L,

    @Column(name = "city_name")
    var name: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", referencedColumnName = "country_id")
    var country: Country? = null
)