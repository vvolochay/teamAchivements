package com.vvolochay

import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectories

data class TeamInfo(
    val id: String,
    val school: String,
    val name: String,
    val team: String
)

class NercSvgGenerator {

    private val outputDir = Path.of("build/generated").createDirectories().toString()
    fun run() {
        File("src/main/resources/nerc/vkoshp.txt").forEachLine {
            val teamInfo = parseStr(it)

            var replaced =
                if (teamInfo.school.length > 45) File("src/main/resources/nerc/vkoshp2.svg").readText(Charsets.UTF_8)
                else File("src/main/resources/nerc/vkoshp1.svg").readText(Charsets.UTF_8)

            if (teamInfo.school.length > 45) {
                val first = teamInfo.school.substringBeforeLast(",")
                val second = teamInfo.school.substringAfterLast(", ")

                replaced = replaced.replace("{FullSchoolName1}", first)
                    .replace("{FullSchoolName2}", second)

            } else {
                replaced = replaced.replace("{FullSchoolName1}", teamInfo.school)
            }

            replaced = replaced.replace("{TeamId}", teamInfo.id)
                .replace("{TeamMembers}", teamInfo.team)
                .replace("{TeamName}", teamInfo.name)

            File(outputDir + "/" + teamInfo.id + ".svg").writeText(replaced, Charsets.UTF_8)
        }
    }

    private fun parseStr(str: String): TeamInfo {
        val id = str.substringBefore("/")
        val teamInfo = str.substringAfter("/")
        val school: String
        var name = ""
        if (teamInfo.contains(":")) {
            school = teamInfo.substringBefore(":")
            name = teamInfo.substringAfter(": ").substringBefore("(")
        } else {
            school = teamInfo.substringBefore("(")
        }

        val team = "(" + teamInfo.substringAfterLast("(")

        return TeamInfo(id, school, name, team)
    }
}

fun main() {
    NercSvgGenerator().run()
}