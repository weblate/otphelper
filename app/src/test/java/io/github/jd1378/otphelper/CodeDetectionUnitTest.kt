@file:Suppress("SpellCheckingInspection")

package io.github.jd1378.otphelper

import io.github.jd1378.otphelper.utils.CodeExtractor
import io.github.jd1378.otphelper.utils.CodeIgnore
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

/** See [testing documentation](http://d.android.com/tools/testing). */
class CodeDetectionUnitTest {
  @Test
  fun pasargadCode() {
    val msg = """پاسارگاد
خرید
اسنپ فود
مبلغ:1,555,000
رمز:1122334455
00:00:00"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  @Test
  fun randomMovePooyaCode() {
    val msg = """انتقال به کارت
000000*0000
5,555,555
رمز پویا 1122334455"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  @Test
  fun pasargadCodeWithRamzArzKeyword() {
    val msg = """پاسارگاد
خرید
سایت رمز-ارز یه چیزی 12421
مبلغ:1,555,000
رمز:1122334455
00:00:00"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  @Test
  fun samanCode() {
    val msg = """بانک سامان
خريد
اسنپ
مبلغ 450,000 ريال
رمز 1122334455
زمان اعتبار رمز 12:00:00"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  @Test
  fun sinaCode() {
    val msg =
        """*بانک سينا*
خريد
ايرانسل
مبلغ 10,000 ريال
رمز 1122334455
زمان اعتبار  12:00:00
تاريخ 1400/01/01-10:00"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("1122334455", CodeExtractor.getCode(msg))
  }

  // -------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------
  // --------------------------------- IGNORE ONLY TESTS ---------------------------------
  // -------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------

  @Test
  fun digikalaIgnore1() {
    var shouldIgnore =
        CodeIgnore.shouldIgnore(
            """دیجی‌کالا
سلام عزیز
از کالاهایی که خریده‌اید راضی هستید؟ لطفا میزان رضایتتان را از طریق لینک زیر به ما بگویید.
https://www.digikala.com/transaction/rate/?RatingCode=x123456
همچنین میتوانید درباره کالا دیدگاه ثبت کنید و پس از تایید دیدگاه، از دیجی کلاب امتیاز بگیرید!""")

    Assert.assertTrue(shouldIgnore)
  }

  @Test
  fun vscodeIgnore() {
    var shouldIgnore = CodeIgnore.shouldIgnore("""your vscode is: 12312312""".trimIndent())
    Assert.assertTrue(shouldIgnore)
  }

  @Test
  fun pasargadShouldIgnore1() {
    val should =
        CodeIgnore.shouldIgnore(
            """پاسارگاد
رمز اول0000*0000000
در1999/01/01
12:00:00
اشتباه وارد شده است""")

    Assert.assertTrue(should)
  }

  @Test
  fun iranKetabCode() {
    val msg = """کد فعالسازی شما در سایت ایران کتاب 
Code: 123456
www.iranketab.ir
لغو11"""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun tiktokCode() {
    val msg = "[#][TikTok] 123456 is your verification code"
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun instagramCode() {
    val msg = "123 456 is your Instagram code. Don't share it."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun paypal2FACode() {
    val msg = """PayPal : 123456 est votre code de sécurité. Ne partagez pas votre code."""

    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun amazonCode() {
    val msg = "123456 ist dein Amazon-Einmalkennwort. Teile es nicht mit anderen Personen."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun googleCode() {
    val msg = "G-123456 is your Google verification code."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals("123456", CodeExtractor.getCode(msg))
  }

  @Test
  fun spanishCode() {
      val testCases = listOf(
          Pair("Su codigo de verificacion de AAAA es 123456", "123456"),
          Pair("123456 es el código de verificación para tu cuenta de Sony.", "123456"),
          Pair("BBB. Clave de firma: 1234. Introduce esta clave de un solo uso (OTP) en el formulario web para firmar (SMS CERTIFICADO)", "1234"),
          Pair("123 456 es tu código de Instagram. No lo compartas.", "123456"),
          Pair("PayPal: Tu código de seguridad es 123456. No lo compartas con nadie.", "123456"),
          Pair("Se ha generado el siguiente codigo de un solo uso: 12345678", "12345678"),
          Pair("123456 es tu contraseña temporal de Amazon. No la compartas con nadie.", "123456")
      )

      for ((msg, expectedCode) in testCases) {
          assertEquals(false, CodeIgnore.shouldIgnore(msg))
          assertEquals(expectedCode, CodeExtractor.getCode(msg))
      }
  }

  @Test
  fun shouldNotExtractAnythingFromWordsContainingOTP() {
    val msg = "123456 is your foOTPath."
    val msg2 = "your foOTPath is 123456."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals(false, CodeIgnore.shouldIgnore(msg2))
    assertEquals(null, CodeExtractor.getCode(msg))
    assertEquals(null, CodeExtractor.getCode(msg2))
  }

  @Test
  fun shouldBeSensitiveToOTP() {
    val msg = "123456 is your OTP."
    val msg2 = "your otp is 123456."
    assertEquals(false, CodeIgnore.shouldIgnore(msg))
    assertEquals(false, CodeIgnore.shouldIgnore(msg2))
    assertEquals("123456", CodeExtractor.getCode(msg))
    assertEquals("123456", CodeExtractor.getCode(msg2))
  }
}
