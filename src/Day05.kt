import kotlin.math.abs
import kotlin.math.max

/**
 * --- Day 5: Hydrothermal Venture ---
 * You come across a field of hydrothermal vents on the ocean floor! These vents constantly produce large, opaque clouds, so it would be best to avoid them if possible.
 *
 * They tend to form in lines; the submarine helpfully produces a list of nearby lines of vents (your puzzle input) for you to review. For example:
 *
 *
 * 0,9 -> 5,9
 *
 * 8,0 -> 0,8
 *
 * 9,4 -> 3,4
 *
 * 2,2 -> 2,1
 *
 * 7,0 -> 7,4
 *
 * 6,4 -> 2,0
 *
 * 0,9 -> 2,9
 *
 * 3,4 -> 1,4
 *
 * 0,0 -> 8,8
 *
 * 5,5 -> 8,2
 *
 *
 * Each line of vents is given as a line segment in the format x1,y1 -> x2,y2 where x1,y1 are the coordinates of one end the line segment and x2,y2 are the coordinates of the other end. These line segments include the points at both ends. In other words:
 *
 * An entry like 1,1 -> 1,3 covers points 1,1, 1,2, and 1,3.
 * An entry like 9,7 -> 7,7 covers points 9,7, 8,7, and 7,7.
 * For now, only consider horizontal and vertical lines: lines where either x1 = x2 or y1 = y2.
 *
 * So, the horizontal and vertical lines from the above list would produce the following diagram:
 *
 *
 * .......1..
 *
 * ..1....1..
 *
 * ..1....1..
 *
 * .......1..
 *
 * .112111211
 *
 * ..........
 *
 * ..........
 *
 * ..........
 *
 * ..........
 *
 * 222111....
 *
 *
 * In this diagram, the top left corner is 0,0 and the bottom right corner is 9,9. Each position is shown as the number of lines which cover that point or . if no line covers that point. The top-left pair of 1s, for example, comes from 2,2 -> 2,1; the very bottom row is formed by the overlapping lines 0,9 -> 5,9 and 0,9 -> 2,9.
 *
 * To avoid the most dangerous areas, you need to determine the number of points where at least two lines overlap. In the above example, this is anywhere in the diagram with a 2 or larger - a total of 5 points.
 *
 * Consider only horizontal and vertical lines. At how many points do at least two lines overlap?
 *
 * Your puzzle answer was 7142.
 *
 * The first half of this puzzle is complete! It provides one gold star: *
 *
 * --- Part Two ---
 *
 *
 * Unfortunately, considering only horizontal and vertical lines doesn't give you the full picture; you need to also consider diagonal lines.
 *
 * Because of the limits of the hydrothermal vent mapping system, the lines in your list will only ever be horizontal, vertical, or a diagonal line at exactly 45 degrees. In other words:
 *
 * An entry like 1,1 -> 3,3 covers points 1,1, 2,2, and 3,3.
 * An entry like 9,7 -> 7,9 covers points 9,7, 8,8, and 7,9.
 * Considering all lines from the above example would now produce the following diagram:
 *
 *
 * 1.1....11.
 *
 * .111...2..
 *
 * ..2.1.111.
 *
 * ...1.2.2..
 *
 * .112313211
 *
 * ...1.2....
 *
 * ..1...1...
 *
 * .1.....1..
 *
 * 1.......1.
 *
 * 222111....
 *
 *
 * You still need to determine the number of points where at least two lines overlap. In the above example, this is still anywhere in the diagram with a 2 or larger - now a total of 12 points.
 *
 * Consider all of the lines. At how many points do at least two lines overlap?
 *
 * */

const val DAY_5_INPUT = "day05"
const val DAY_5_INPUT_SMALL = "day05-small"

fun main() {

    data class Point(val x: Int, val y: Int)
    data class Line(val start: Point, val end: Point) {

        fun pointsCovered(): List<Point> {
            val pointsCovered = mutableListOf<Point>()

            val xDir = directionOfCover(start.x, end.x)
            val yDir = directionOfCover(start.y, end.y)

            //calculate the magnitude of cover for x and y
            //add 1 to offset o indexing
            val xCover = abs(end.x - start.x) + 1
            val yCover = abs(end.y - start.y) + 1

            //take the max of the above two
            val cover =
                if (xCover > yCover) xCover else yCover //max(xCover, yCover)  // => 1,y1 -> 1,y2  || x1,1 -> x2,1

            var x1 = start.x
            var y1 = start.y

            //Add the start and end points including the points covered
            //too to the points covered by the line
            repeat(cover) {
                pointsCovered.add(Point(x1, y1))
                x1 += xDir
                y1 += yDir
            }
            return pointsCovered
        }

        /**
         * Determine the direction of cover
         *
         * @param start : Start of the line
         * @param end : End of the line
         *
         * If [end] > [start], then the line covers in the positive direction from start to end
         * if [start] > [end], then the line covers in the negative direction from end to start
         * */
        private fun directionOfCover(start: Int, end: Int): Int {
            return when {
                start < end -> 1
                start > end -> -1
                else -> 0
            }

        }
    }

    fun Line.isVertical(): Boolean = start.x == end.x

    fun Line.isHorizontal(): Boolean = start.y == end.y

    fun List<Line>.findNumberOfOverlappingPoints(): Int {
        return flatMap { it.pointsCovered() }
            .fold(mutableMapOf<Point, Int>()) { counts, point ->
                counts[point] = counts.getOrDefault(point, 0) + 1
                counts
            }.values.filter { it > 1 }.size

    }

    fun List<String>.toLines(): List<Line> {
        val regX = """(\d+),(\d+)\s+->\s+(\d+),(\d+)""".toRegex()
        return map {
            val matchResult = regX.find(it) ?: throw IllegalArgumentException("Invalid input")
            val (x1, y1, x2, y2) = matchResult.destructured

            val start = Point(x1.toInt(), y1.toInt())
            val end = Point(x2.toInt(), y2.toInt())

            Line(start, end)
        }
    }

    fun checkInput(input: List<String>): Int {
        return input.size
    }

    fun part1(input: List<String>): Int {
        val lines = input.toLines()
        val verticalOrHorizontalLines = lines.filter { it.isVertical() || it.isHorizontal() }
        return verticalOrHorizontalLines.findNumberOfOverlappingPoints()
    }

    fun part2(input: List<String>): Int {
        val lines = input.toLines()
        return lines.findNumberOfOverlappingPoints()
    }


    // test if implementation meets criteria from the description, like:
    val input = readInput(DAY_5_INPUT)
    check(checkInput(input) == 500)

    println(part1(input))
    println(part2(input))
}

