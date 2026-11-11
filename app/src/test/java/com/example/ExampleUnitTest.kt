package com.example

import com.example.util.AppLanguage
import com.example.util.isUrdu
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun appLanguage_toggle_worksCorrectly() {
    var language = AppLanguage.URDU
    assertTrue(language.isUrdu)
    assertEquals("اردو", language.nativeName)

    // Toggle to English
    language = if (language.isUrdu) AppLanguage.ENGLISH else AppLanguage.URDU
    assertFalse(language.isUrdu)
    assertEquals("English", language.displayName)

    // Toggle back to Urdu
    language = if (language.isUrdu) AppLanguage.ENGLISH else AppLanguage.URDU
    assertTrue(language.isUrdu)
  }
}
