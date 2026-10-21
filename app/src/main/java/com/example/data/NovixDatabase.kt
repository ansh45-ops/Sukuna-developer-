package com.example.data

object NovixDatabase {

    val RDX_MATRIX: Map<Int, Map<Int, Char>> = mapOf(
        0 to mapOf(0 to 'S', 1 to 'B', 2 to 'B', 3 to 'B', 4 to 'S', 5 to 'S', 6 to 'B', 7 to 'S', 8 to 'S', 9 to 'B'),
        1 to mapOf(0 to 'B', 1 to 'B', 2 to 'B', 3 to 'S', 4 to 'S', 5 to 'S', 6 to 'B', 7 to 'S', 8 to 'B', 9 to 'B'),
        2 to mapOf(0 to 'S', 1 to 'B', 2 to 'B', 3 to 'B', 4 to 'S', 5 to 'B', 6 to 'S', 7 to 'B', 8 to 'S', 9 to 'S'),
        3 to mapOf(0 to 'S', 1 to 'B', 2 to 'B', 3 to 'S', 4 to 'B', 5 to 'S', 6 to 'B', 7 to 'B', 8 to 'S', 9 to 'S'),
        4 to mapOf(0 to 'B', 1 to 'S', 2 to 'S', 3 to 'B', 4 to 'S', 5 to 'B', 6 to 'S', 7 to 'B', 8 to 'B', 9 to 'S'),
        5 to mapOf(0 to 'S', 1 to 'B', 2 to 'B', 3 to 'S', 4 to 'S', 5 to 'B', 6 to 'B', 7 to 'S', 8 to 'B', 9 to 'B'),
        6 to mapOf(0 to 'S', 1 to 'S', 2 to 'S', 3 to 'B', 4 to 'S', 5 to 'B', 6 to 'B', 7 to 'S', 8 to 'B', 9 to 'B'),
        7 to mapOf(0 to 'B', 1 to 'B', 2 to 'B', 3 to 'S', 4 to 'B', 5 to 'S', 6 to 'B', 7 to 'S', 8 to 'B', 9 to 'S'),
        8 to mapOf(0 to 'S', 1 to 'B', 2 to 'B', 3 to 'B', 4 to 'S', 5 to 'S', 6 to 'B', 7 to 'S', 8 to 'B', 9 to 'B'),
        9 to mapOf(0 to 'S', 1 to 'B', 2 to 'B', 3 to 'S', 4 to 'B', 5 to 'S', 6 to 'S', 7 to 'S', 8 to 'B', 9 to 'B')
    )

    val RDX_HIST: IntArray = intArrayOf(
        2,1,2,0,5,5,5,6,0,7,1,2,6,1,9,7,6,5,0,0,0,8,4,5,2,4,3,9,4,9,4,1,2,3,4,4,2,6,1,2,9,6,1,9,2,8,9,1,8,7,
        2,9,5,9,5,6,3,0,6,8,2,9,3,8,6,8,5,0,6,8,6,9,8,5,3,1,4,4,9,8,9,1,4,8,3,4,3,5,2,1,3,3,2,3,6,8,2,9,4,5,
        6,8,1,4,2,9,0,6,5,5,9,3,5,7,7,3,0,8,1,5,8,5,9,9,4,7,7,1,3,3,6,3,1,6,0,2,5,4,8,2,3,0,7,3,1,0,6,0,9,7,
        1,3,8,0,6,9,3,2,0,4,5,3,2,4,0,8,6,3,5,3,8,5,3,6,3,2,8,8,0,5,4,2,4,6,3,4,4,6,5,5,0,5,1,3,6,9,7,0,2,9,
        5,5,0,9,4,1,0,5,1,9,3,5,4,7,1,5,5,5,3,9,9,7,8,9,7,3,7,1,5,2,0,1,6,7,6,1,1,1,0,3,1,1,0,1,0,6,1,8,1,6,
        2,1,0,3,3,8,7,4,7,3,2,5,5,0,6,9,5,1,3,3,0,0,3,4,6,6,8,6,8,4,2,5,4,4,9,1,7,7,8,6,0,3,0,6,4,1,3,2,4,3,
        3,7,4,9,5,5,9,5,8,9,2,1,0,1,9,2,3,0,2,2,5,1,5,8,7,5,0,8,3,5,4,1,5,0,2,9,2,4,0,8,2,1,2,3,3,6,4,1,9,8,
        4,7,5,5,0,5,6,8,3,7,9,2,1,0,7,2,5,0,8,0,5,1,6,2,2,6,4,9,1,5,3,4,3,7,1,7,3,3,2,6,7,6,1,1,1,4,8,7,6,1,
        9,6,9,5,8,2,2,0,8,6,8,5,4,8,2,7,4,5,1,2,6,5,3,6,8,8,8,1,2,6,4,4,7,9,8,9,6,8,6,7,4,4,9,8,9,5,3,1,0,4,
        6,5,6,4,0,9,9,1,1,9,4,4,9,3,2,6,2,3,7,5,8,0,8,2,5,8,8,8,9,7,0,5,3,8,5,7,8,6,9,3,6,3,6,4,3,6,3,4,3,0,
        3,6,5,6,6,1,6,7,1,4,1,5,9,8,9,7,5,9,6,9,6,6,5,3,8,6,4,8,3,7,7,4,1,4,2,8,4,5,1,4,6,1,3,2,9,8,7,6,1,8,
        1,2,7,7,6,1,7,9,0,5,9,2,5,5,9,2,6,1,7,0,2,7,4,2,8,5,1,6,7,9,7,2,3,7,4,9,7,9,5,3,5,2,6,9,1,8,6,6,0,7,
        3,2,8,3,6,4,2,6,3,1,4,1,8,1,8,2,3,3,6,8,2,1,2,5,6,0,7,7,1,2,2,9,1,7,3,5,7,0,3,7,4,1,8,8,7,3,2,8,5,7,
        4,7,7,8,7,1,7,1,8,8,8,0,4,4,1,1,2,4,2,2,4,3,8,9,7,7,4,5,8,8,9,9,0,3,7,2,2,4,1,0,1,2,8,2,0,4,9,5,4,2
    )

    val VANTA_PATTERNS: Map<String, String> = mapOf(
        "BBBB" to "SMALL", "SSSS" to "BIG", "BBBBB" to "SMALL", "SSSSS" to "BIG",
        "BBBBBB" to "SMALL", "SSSSSS" to "BIG", "BBBBBBB" to "SMALL", "SSSSSSS" to "BIG",
        "BBBBBBBB" to "SMALL", "SSSSSSSS" to "BIG", "BBB" to "SMALL", "SSS" to "BIG",
        "BSBSB" to "SMALL", "SBSBS" to "BIG", "BSBSBS" to "SMALL", "SBSBSB" to "BIG",
        "BSBSBSB" to "SMALL", "SBSBSBS" to "BIG", "BSBSBSBS" to "SMALL", "SBSBSBSB" to "BIG",
        "BBSS" to "BIG", "SSBB" to "SMALL", "BBSSBB" to "SMALL", "SSBBSS" to "BIG",
        "BBBBSS" to "SMALL", "SSSSBB" to "BIG", "BBBBBSS" to "SMALL", "SSSSSBB" to "BIG",
        "BBBBSSS" to "SMALL", "SSSSBBB" to "BIG", "BBBSSS" to "SMALL", "SSSBBB" to "BIG",
        "SSSBBSSS" to "BIG", "BBBSSBBB" to "SMALL"
    )

    // Common n-gram patterns: key -> Pair(predictedChar, totalOccurrences)
    val NOCTIS_BS: Map<String, Pair<Char, Int>> = mapOf(
        "BBBBSBSSSS" to ('B' to 130), "SSBBBSBBBB" to ('S' to 130), "BSSSSBSSSS" to ('B' to 255),
        "SBSSSSBBBB" to ('S' to 249), "SSBBBBSSSS" to ('B' to 246), "SBBBBSSSS" to ('B' to 517),
        "BSBBBBSSSS" to ('B' to 271), "BSSSSBBBB" to ('S' to 511), "BBSSSSBBBB" to ('S' to 262),
        "SBBBBSBBBB" to ('S' to 256), "SBSSBBSSSS" to ('B' to 131), "BSBBSSBBBB" to ('S' to 148),
        "SBBSSBBBB" to ('S' to 274), "SSBBSSBBBB" to ('S' to 126), "SBSSSBSSSS" to ('B' to 144),
        "BSBSBBSSSS" to ('B' to 144), "BBBSBBSSSS" to ('B' to 156), "SBBBSBBBB" to ('S' to 260),
        "BSSSBSSSS" to ('B' to 304), "BBBSSSBBBB" to ('S' to 140), "SSSBSSSS" to ('B' to 581),
        "BSBSSSBBBB" to ('S' to 166), "BBSSSBSSSS" to ('B' to 160), "BBSSBBBB" to ('S' to 551),
        "BBSSSBBBB" to ('S' to 282), "BBBSBSSSS" to ('B' to 266), "SBSBBSSSS" to ('B' to 264),
        "SSSSBSSSS" to ('B' to 277), "BSSSBBBB" to ('S' to 579), "SSSBBBB" to ('S' to 1126),
        "SSSSBBBB" to ('S' to 547), "BBSBSSSS" to ('B' to 558), "BSSBBSSSS" to ('B' to 271),
        "SSBSSSS" to ('B' to 1109), "SBSSSBBBB" to ('S' to 297), "SBSSBSSSS" to ('B' to 249),
        "SBBSBSSSS" to ('B' to 292), "BSBBSBBBB" to ('S' to 288), "BBBSBBBB" to ('S' to 542),
        "SBSSSS" to ('B' to 2218), "SSBBBB" to ('S' to 2213), "SSSBSBBBB" to ('S' to 265),
        "BSBSSSS" to ('B' to 1109), "BSSBSSSS" to ('B' to 528), "BSBBSSSS" to ('B' to 565),
        "SBBSSSS" to ('B' to 1100), "SSBBSSSS" to ('B' to 535), "BSBSBSSSS" to ('B' to 280),
        "BSSSS" to ('B' to 4478), "SBBBB" to ('S' to 4458), "BBSBBBB" to ('S' to 1147),
        "BSSBBBB" to ('S' to 1087), "BBSSBSSSS" to ('B' to 279), "BBBSSBBBB" to ('S' to 277),
        "SSBSBBBB" to ('S' to 538), "BBSSSS" to ('B' to 2260), "BSBBBB" to ('S' to 2245),
        "SBSBSSSS" to ('B' to 551), "SBBSBBBB" to ('S' to 605), "SBSBSBBBB" to ('S' to 301),
        "BBSBBSSSS" to ('B' to 301), "SBSBBBB" to ('S' to 1098), "BBBSSSS" to ('B' to 1160),
        "SBBBSSSS" to ('B' to 592), "BSBSBBBB" to ('S' to 560), "SBSSBBBB" to ('S' to 536),
        "SSBBSBBBB" to ('S' to 317), "BSBSSBBBB" to ('S' to 273), "BBSBSBBBB" to ('S' to 259),
        "SSBBBSSSS" to ('B' to 307), "SSSS" to ('B' to 5161), "BBBB" to ('S' to 5167),
        "BBB" to ('S' to 14228), "SSS" to ('B' to 14113), "BB" to ('S' to 32382), "SS" to ('B' to 32087)
    )
}
