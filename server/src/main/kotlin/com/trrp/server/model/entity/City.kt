package com.trrp.server.model.entity

import javax.persistence.*


@MappedSuperclass
abstract class Test(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "city_id")
    val id: Long = 0L,

    @Column(name = "city_name")
    var name: String = "",
)

@Entity
@Table(name = "t_city")
data class City(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", referencedColumnName = "country_id")
    var country: Country? = null
) : Test()


