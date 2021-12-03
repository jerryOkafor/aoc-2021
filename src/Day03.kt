import kotlin.collections.ArrayList

/**
 * --- Day 3: Binary Diagnostic ---
 *The submarine has been making some odd creaking noises, so you ask it to produce a diagnostic report just in case.
 *
 * The diagnostic report (your puzzle input) consists of a list of binary numbers which, when decoded properly, can tell you many useful things about the conditions of the submarine. The first parameter to check is the power consumption.
 *
 * You need to use the binary numbers in the diagnostic report to generate two new binary numbers (called the gamma rate and the epsilon rate). The power consumption can then be found by multiplying the gamma rate by the epsilon rate.
 *
 * Each bit in the gamma rate can be determined by finding the most common bit in the corresponding position of all numbers in the diagnostic report. For example, given the following diagnostic report:
 *
 *
 * 00100
 *
 * 11110
 *
 * 10110
 *
 * 10111
 *
 * 10101
 *
 * 01111
 *
 * 00111
 *
 * 11100
 *
 * 10000
 *
 * 11001
 *
 * 00010
 *
 * 01010
 *
 *
 * Considering only the first bit of each number, there are five 0 bits and seven 1 bits. Since the most common bit is 1, the first bit of the gamma rate is 1.
 *
 * The most common second bit of the numbers in the diagnostic report is 0, so the second bit of the gamma rate is 0.
 *
 * The most common value of the third, fourth, and fifth bits are 1, 1, and 0, respectively, and so the final three bits of the gamma rate are 110.
 *
 * So, the gamma rate is the binary number 10110, or 22 in decimal.
 *
 * The epsilon rate is calculated in a similar way; rather than use the most common bit, the least common bit from each position is used. So, the epsilon rate is 01001, or 9 in decimal. Multiplying the gamma rate (22) by the epsilon rate (9) produces the power consumption, 198.
 *
 * Use the binary numbers in your diagnostic report to calculate the gamma rate and epsilon rate, then multiply them together. What is the power consumption of the submarine? (Be sure to represent your answer in decimal, not binary.)
 * */

const val DAY3_INPUT_FILE = "day3"
const val DAY3_INPUT_FILE_SMALL = "day3-small"

//Issues:
//We do not know how long the binary number can be:  (4 Bit, 8 Bit or 16 bit)
//We simply store the count in a list.]

//Uses: groupingBy(), map(), eachCount() and maxBy()
fun main() {

    fun <T> List<List<T>>.transpose(): List<List<T>> {
        val transposed = ArrayList<ArrayList<T>>(maxOf { it.size })

        for (i in 1..maxOf { it.size }) {
            transposed.add(ArrayList(size))
        }

        forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, col ->
                transposed[colIndex].add(rowIndex, col)
            }
        }

        return transposed
    }

    fun List<Int>.fromBitsToInt() = joinToString("") { it.toString() }.let { Integer.parseInt(it, 2) }


    fun checkInput(input: List<String>): Int {
        return input.size
    }

    fun part1(input: List<String>): Int {

        //convert to 2D array, easier to manipulate
        val matrix = input.map { it.map { it.toString().toInt() } }

        //Since we are interested in the first items, lets transpose
        //this will give us all the items in a row then we can easily manipulate
        val transposed = matrix.transpose()
        val commonEntries = transposed.map { it.groupingBy { it }.eachCount().maxByOrNull { it.value }!!.key }
        val commonEntriesMin = transposed.map { it.groupingBy { it }.eachCount().minByOrNull { it.value }!!.key }

        //we can also get the same below
//        val commonEntriesXor = commonEntries.map { it?.xor(1) }
//
        val gammaRate = commonEntries.fromBitsToInt()
        val epsilonRate = commonEntriesMin.fromBitsToInt()

        //println("Gamma Rate: $gammaRateBinary == $gammaRate")
        //println("Epsilon Rate: $epsilonRateBinary == $epsilonRate")

        return gammaRate * epsilonRate
    }

    fun List<Int>.getMostCommonBit(defaultBit: Int, comparator: (Map<Int, Int>) -> Map.Entry<Int, Int>) =
        groupingBy { it }
            .eachCount().let {
                //handle the case whereby 0s == 1s
                if (it[0] == it[1]) {
                    it.entries.first { it.key == defaultBit }
                } else {
                    comparator(it)
                }
            }.key

    fun part2(input: List<String>): Int {

        val o2Lists = input.map { it.map { it.toString().toInt() } }.let { originalList ->
            var filteredList: List<List<Int>> = originalList
            for (i in 0 until originalList.maxOf { it.size }) {
                val currentCol = filteredList.map { it[i] }
                val mostCommonInCol = currentCol.getMostCommonBit(1) { it.maxByOrNull { it.value }!! }
                filteredList = filteredList.filter { it[i] == mostCommonInCol }
            }
            filteredList.first()
        }

        val cO2Lists = input.map { it.map { it.toString().toInt() } }.let { originalList ->
            var filteredList: List<List<Int>> = originalList
            for (i in 0 until originalList.maxOf { it.size }) {
                val currentCol = filteredList.map { it[i] }
                val mostCommonInCol = currentCol.getMostCommonBit(0) { it.minByOrNull { it.value }!! }
                filteredList = filteredList.filter { it[i] == mostCommonInCol }
            }
            filteredList.first()
        }

        val o2GeneratorRating = o2Lists.fromBitsToInt()
        val c02ScrubbingRating = cO2Lists.fromBitsToInt()

        return o2GeneratorRating * c02ScrubbingRating
    }

    // test if implementation meets criteria from the description, like:
    val input = readInput(DAY3_INPUT_FILE)
    check(checkInput(input) == 1000)

    println(part1(input))
    println(part2(input))
}

