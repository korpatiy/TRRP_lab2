package com.trrp.server.module.service


import com.trrp.server.grpc.Request
import com.trrp.server.model.entity.*
import com.trrp.server.module.repository.*
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class MigrationService(
    private val cityRepository: CityRepository,
    private val countryRepository: CountryRepository,
    private val disciplineRepository: DisciplineRepository,
    private val trackRepository: TrackRepository,
    private val championshipRepository: ChampionshipRepository,
    private val raceRepository: RaceRepository,
    private val stageRepository: StageRepository
) {

    fun migrate(request: Request) {
        /*val typeToken = object : TypeToken<List<HashMap<String, String>>>() {}.type
        val data: List<HashMap<String, String>> = Gson().fromJson(decodeMessage, typeToken)*/

        val countryName = request.countryName ?: ""
        var country = countryRepository.findByName(countryName)
        if (country == null) {
            country = Country(name = countryName)
            countryRepository.save(country)
        }

        val cityName = request.cityName ?: ""
        var city = cityRepository.findByName(cityName)
        if (city == null) {
            city = City(country = country)
            city.name = cityName
            cityRepository.save(city)
        }

        val discName = request.discName ?: ""
        var discipline = disciplineRepository.findByName(discName)
        if (discipline == null) {
            discipline = Discipline(
                name = discName,
                nFireLines = request.discLines,
                fine = request.discFines
            )
            disciplineRepository.save(discipline)
        }

        val champName = request.champName ?: ""
        var championship = championshipRepository.findByName(champName)
        if (championship == null) {
            championship = Championship(
                name = champName,
                startDate = LocalDate.parse(request.champStart),
                endDate = LocalDate.parse(request.champEnd)
            )
            championshipRepository.save(championship)
        }

        val stageName = request.stageName ?: ""
        var stage = stageRepository.findByName(stageName)
        if (stage == null) {
            stage = Stage(
                name = stageName,
                championship = championship,
                city = city,
                startDate = LocalDate.parse(request.stageStart),
                endDate = LocalDate.parse(request.stageEnd)
            )
            stageRepository.save(stage)
        }

        val trackLen = request.trackLength
        val trackLoc = request.trackLocation
        var track = trackRepository.findByLengthAndLocation(
            trackLen,
            trackLoc
        )
        if (track == null) {
            track = Track(
                length = trackLen,
                location = trackLoc
            )
            trackRepository.save(track)
        }

        val sex = request.sex
        val date = LocalDate.parse(request.raceDate)
        val startTime = LocalDateTime.parse(request.startTime)
        val race =
            raceRepository.findByStageAndTrackAndDisciplineAndSexAndDateAndStartTime(
                stage, track, discipline, sex, date, startTime
            )
        if (race == null) {
            raceRepository.save(
                Race(
                    sex = sex,
                    date = date,
                    startTime = startTime,
                    stage = stage, track = track, discipline = discipline
                )
            )
        }
    }
}