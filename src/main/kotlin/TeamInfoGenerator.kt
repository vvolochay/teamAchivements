package com.vvolochay

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileReader
import java.util.*

data class Data(
    val id: Int,
    val university: University,
    val team: Team,
    val coach: Person,
    val contestants: List<Person>
)

data class Team(
    val name: String,
    val regionals: List<String>
)

data class University(
    val fullName: String,
    val shortName: String,
    val region: String,
    val hashTag: String?,
    val url: String,
    val appYears: List<Int>? = null,
    val winYears: List<Int>? = null,
    val goldYears: List<Int>? = null,
    val silverYears: List<Int>? = null,
    val bronzeYears: List<Int>? = null,
    val regYears: List<Int>? = null
)

data class Person(
    val name: String,
    val altNames: List<String>,
    val tcHandle: String? = null,
    val tcRating: Int? = null,
    val cfHandle: String? = null,
    val cfRating: Int? = null,
    val twitterHandle: String? = null,
    val achievements: List<Achievement>? = null
)

data class Achievement(val achievement: String, val priority: Int)

fun parseJsons(): List<Data> {
    val teamsData = mutableListOf<Data>()
    File("src/main/resources/teaminfo").listFiles()?.forEach {
        val reader = JsonReader(FileReader(it))
        val data: Data = Gson().fromJson(reader, Data::class.java)
        teamsData.add(data)
    }
    return teamsData
}

fun generateMainSVG(data: Data) {
    val svgText: String = if (data.university.fullName.length <= 45) {
        File("src/main/resources/svgBase/main.svg").readText(Charsets.UTF_8)
            .replace("{FullUniversityName}", data.university.fullName)
    } else {
        println("Long naming: check " + data.id)

        File("src/main/resources/svgBase/main2.svg").readText(Charsets.UTF_8)
            .replace("{FullUniversityName_1}", data.university.fullName.substring(0, 45))
            .replace("{FullUniversityName_2}", data.university.fullName.substring(45, data.university.fullName.length))
    }

    val replaced = svgText.replace("{UniversityLogo}", base64Logo(data.id.toString() + ".jpg"))
        .replace("{ShortTeamName}", data.university.shortName)
        .replace("{Region}", data.university.region)
        .replace("{HashTag}", data.university.hashTag ?: "")
        .replace(
            "{FinalsCounter}",
            if (data.university.appYears == null || data.university.appYears.size == 1) " 1 FINAL"
            else data.university.appYears.size.toString() + " FINALS"
        )
    File(data.id.toString() + "_main.svg").writeText(replaced, Charsets.UTF_8)
}

fun generatePersonSVG(data: Data, person: Person, prefix: String) {
    var rightRect =
        "<rect x=\"1245.3047\" y=\"798.7099\" width=\"78\" height=\"28\" rx=\"4\" fill=\"#dd340f\" id=\"rect19698\"/>\n <text fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"16px\" font-weight=\"bold\" letter-spacing=\"0em\" id=\"text19702\" x=\"13.304717\" y=\"758.2099\"><tspan x=\"1253.3047\" y=\"818.22937\" id=\"tspan19700\">{Rating}</tspan></text>\n"
    var leftRect =
        "<rect x=\"1155.3047\" y=\"798.7099\" width=\"78\" height=\"28\" rx=\"4\" fill=\"#dd340f\" id=\"rect19692\"/><text fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"16px\" font-weight=\"bold\" letter-spacing=\"0em\" id=\"text19696\" x=\"13.304717\" y=\"758.2099\"><tspan x=\"1163.3047\" y=\"818.22937\" id=\"tspan19694\">{Rating}</tspan></text>"

    var svgText = File("src/main/resources/svgBase/person.svg").readText(Charsets.UTF_8)
        .replace("{FullUniversityName}", data.university.fullName)
        .replace("{PersonName}", person.name)
        .replace("{UniversityLogo}", base64Logo(data.id.toString() + ".jpg"))


    if (person.cfRating == null && person.tcRating == null) {
        rightRect = ""
        leftRect = ""
    } else if (person.cfRating != null && person.tcRating == null) {
        leftRect = ""
        rightRect = rightRect.replace("{Rating}", "CF " + person.cfRating.toString())
    } else if (person.cfRating == null && person.tcRating != null) {
        leftRect = ""
        rightRect = rightRect.replace("{Rating}", "TC " + person.tcRating.toString())
    } else {
        leftRect = leftRect.replace("{Rating}", "CF " + person.cfRating.toString())
        rightRect = rightRect.replace("{Rating}", "TC " + person.tcRating.toString())
    }

    svgText = svgText.replace("{rightRect}", rightRect).replace("{leftRect}", leftRect)

    var a1 =
        "<text opacity=\"0.75\" fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"18px\" letter-spacing=\"0em\" id=\"text19706\"><tspan x=\"216\" y=\"103.52\" id=\"tspan19704\">{achievement}</tspan></text>"
    var a2 =
        "<text opacity=\"0.75\" fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"18px\" letter-spacing=\"0em\" id=\"text19710\"><tspan x=\"216\" y=\"133.52\" id=\"tspan19708\">{achievement}</tspan></text>"
    var a3 =
        "<text opacity=\"0.75\" fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"18px\" letter-spacing=\"0em\" id=\"text19714\"><tspan x=\"216\" y=\"163.52\" id=\"tspan19712\">{achievement}</tspan></text>"
    var a4 =
        "<text opacity=\"0.75\" fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"18px\" letter-spacing=\"0em\" id=\"text19718\"><tspan x=\"523\" y=\"103.52\" id=\"tspan19716\">{achievement}</tspan></text>"
    var a5 =
        "<text opacity=\"0.75\" fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"18px\" letter-spacing=\"0em\" id=\"text19722\"><tspan x=\"523\" y=\"133.52\" id=\"tspan19720\">{achievement}</tspan></text>"
    var a6 =
        "<text opacity=\"0.75\" fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"18px\" letter-spacing=\"0em\" id=\"text19726\"><tspan x=\"523\" y=\"163.52\" id=\"tspan19724\">{achievement}</tspan></text>"
    var a7 =
        "<text opacity=\"0.75\" fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"18px\" letter-spacing=\"0em\" id=\"text19730\"><tspan x=\"800\" y=\"103.52\" id=\"tspan19728\">{achievement}</tspan></text>"
    var a8 =
        "<text opacity=\"0.75\" fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"18px\" letter-spacing=\"0em\" id=\"text19734\"><tspan x=\"800\" y=\"133.52\" id=\"tspan19732\">{achievement}</tspan></text>"
    var a9 =
        "<text opacity=\"0.75\" fill=\"#ffffff\" xml:space=\"preserve\" style=\"white-space:pre\" font-family=\"Helvetica\" font-size=\"18px\" letter-spacing=\"0em\" id=\"text19738\"><tspan x=\"800\" y=\"163.52\" id=\"tspan19736\">{achievement}</tspan></text>"

    val achs = arrayListOf(a1, a2, a3, a4, a5, a6, a7, a8, a9)

    if (person.achievements != null) {
        val arrayList = ArrayList<Pair<Int, String>>()
        for (p in person.achievements) {
            arrayList.add(Pair(p.priority, p.achievement))
        }

        if (arrayList.size > 3) {
            println("Please check one more time: " + person.name)
        }

        if (arrayList.size >= 9) {
            for (i in 1..9) {
                svgText = svgText.replace("{a$i}", achs[i - 1])
                    .replace("{achievement}", arrayList[i - 1].second)
            }
        } else {
            for (i in 1..arrayList.size) {
                svgText = svgText.replace("{a$i}", achs[i - 1])
                    .replace("{achievement}", arrayList[i - 1].second)
            }
            for (i in arrayList.size + 1..9) {
                svgText = svgText.replace("{a$i}", achs[i - 1])
                    .replace("{achievement}", "")
            }
        }

    } else {
        for (i in 1..9) svgText = svgText.replace("{a$i}", "")
    }

    File("src/main/resources/result/" + data.id + "_" + prefix + ".svg").writeText(svgText, Charsets.UTF_8)
}


fun base64Logo(filename: String): String {
    val fileContent: ByteArray = FileUtils.readFileToByteArray(File("src/main/resources/logo/$filename"))
//    val fileContent: ByteArray = FileUtils.readFileToByteArray(File("src/main/resources/images/temp.png"))
    return Base64.getEncoder().encodeToString(fileContent)
}

class TeamInfoGenerator {
    fun run() {
        val teamData = parseJsons()
        for (data in teamData) {
            generateMainSVG(data)
            generatePersonSVG(data, data.coach, "coach")
            for (i in 0 until data.contestants.size) {
                generatePersonSVG(data, data.contestants[i], "contestant_$i")
            }
        }
    }
}

fun main() {
    TeamInfoGenerator().run()
}