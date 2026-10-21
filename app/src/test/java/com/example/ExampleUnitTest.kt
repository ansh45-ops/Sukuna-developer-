package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testEngineFusionGeneratesPrediction() {
    val sample = com.example.data.ApiService.generateSampleHistory(50)
    val fusion = com.example.data.Engines.fuse(sample, "2026102110010051")
    assertNotNull(fusion)
    assertTrue(fusion.call == "BIG" || fusion.call == "SMALL")
    assertEquals(2, fusion.nums.size)
    assertTrue(fusion.conf in 0.5..1.0)
  }
}
