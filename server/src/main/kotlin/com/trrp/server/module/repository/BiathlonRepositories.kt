package com.trrp.server.module.repository

import com.trrp.server.model.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
interface ChampionshipRepository : JpaRepository<Championship, Long> {
    fun findByName(name: String): Championship?
}

@NoRepositoryBean
interface TestRepo: JpaRepository<Test, Long>{
}

@Repository
interface CityRepository : TestRepo {
    fun findByName(name: String): City?
}

@Repository
interface CountryRepository : JpaRepository<Country, Long> {
    fun findByName(name: String): Country?
}

@Repository
interface DisciplineRepository : JpaRepository<Discipline, Long> {
    fun findByName(name: String): Discipline?
}

@Repository
interface RaceRepository : JpaRepository<Race, Long> {
    fun findByStageAndTrackAndDisciplineAndSexAndDateAndStartTime(
        stage: Stage,
        track: Track,
        discipline: Discipline,
        sex: String,
        date: LocalDate,
        startTime: LocalDateTime
    ): Race?
}

@Repository
interface StageRepository : JpaRepository<Stage, Long> {
    fun findByName(name: String): Stage?
}

@Repository
interface TrackRepository : JpaRepository<Track, Long> {
    fun findByLengthAndLocation(length: Int, location: String): Track?
}

