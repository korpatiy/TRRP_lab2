package com.trrp.server.module.service


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    fun migrate(decodeMessage: String) {
        val typeToken = object : TypeToken<List<HashMap<String, String>>>() {}.type
        val data: List<HashMap<String, String>> = Gson().fromJson(decodeMessage, typeToken)

        data.forEach {
            val countryName = it["country_name"] ?: ""
            var country = countryRepository.findByName(countryName)
            if (country == null) {
                country = Country(name = countryName)
                countryRepository.save(country)
            }

            val cityName = it["city_name"] ?: ""
            var city = cityRepository.findByName(cityName)
            if (city == null) {
                city = City(name = cityName, country = country)
                cityRepository.save(city)
            }

            val discName = it["disc_name"] ?: ""
            var discipline = disciplineRepository.findByName(discName)
            if (discipline == null) {
                discipline = Discipline(
                    name = discName,
                    nFireLines = it["disc_lines"]?.toInt() ?: 0,
                    fine = it["disc_fine"]?.toInt() ?: 0
                )
                disciplineRepository.save(discipline)
            }

            val champName = it["champ_name"] ?: ""
            var championship = championshipRepository.findByName(champName)
            if (championship == null) {
                championship = Championship(
                    name = champName,
                    startDate = LocalDate.parse(it["champ_start"]),
                    endDate = LocalDate.parse(it["champ_end"])
                )
                championshipRepository.save(championship)
            }

            val stageName = it["stage_name"] ?: ""
            var stage = stageRepository.findByName(stageName)
            if (stage == null) {
                stage = Stage(
                    name = stageName,
                    championship = championship,
                    city = city,
                    startDate = LocalDate.parse(it["stage_start"]),
                    endDate = LocalDate.parse(it["stage_end"])
                )
                stageRepository.save(stage)
            }

            val trackLen = it["track_length"]?.toInt() ?: 0
            val trackLoc = it["track_location"] ?: ""
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

            val sex = it["sex"] ?: ""
            val date = LocalDate.parse(it["race_date"])
            val startTime = LocalDateTime.parse(it["start_time"])
            val race =
                raceRepository.findByStageAndTrackAndDisciplineAndSexAndDateAndStartTime(
                    stage, track, discipline, sex, date, startTime
                )
            if (race == null) {
                raceRepository.save(
                    Race(
                        sex = it["sex"] ?: "",
                        date = LocalDate.parse(it["race_date"]),
                        startTime = LocalDateTime.parse(it["start_time"]),
                        stage = stage, track = track, discipline = discipline
                    )
                )
            }
        }
    }
}