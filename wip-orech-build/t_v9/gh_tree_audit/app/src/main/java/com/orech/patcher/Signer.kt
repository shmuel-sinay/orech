package com.orech.patcher

import android.content.Context
import com.android.apksig.ApkSigner
import com.android.apksig.ApkVerifier
import java.io.File
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import java.util.jar.JarFile
import java.util.zip.ZipFile

/**
 * חותם ומאמת APK/JAR עם אחד מהמפתחות שמגיעים עם האפליקציה
 * (assets/keys/<name>.pk8 + .x509.pem).
 *
 * minSdkVersion נקבע באופן מפורש (לא מוסק מ-AndroidManifest.xml) כי ל-services.jar
 * וקבצי jar אחרים אין manifest בכלל - בלעדיו גם ApkSigner וגם ApkVerifier מנסים
 * לקרוא אותו, ונכשלים על קובץ כזה.
 */
object Signer {
    private const val MIN_SDK_VERSION = 26

    /** זורק חריגה בכשל - נתפס ונרשם מלא ע"י PatchEngine.apply, לא נבלע כאן. */
    fun sign(context: Context, input: File, output: File, keyName: String) {
        val pk8Bytes = context.assets.open("keys/$keyName.pk8").readBytes()
        val certBytes = context.assets.open("keys/$keyName.x509.pem").readBytes()

        val keyFactory = KeyFactory.getInstance("RSA")
        val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(pk8Bytes))

        val cert = CertificateFactory.getInstance("X.509")
            .generateCertificate(certBytes.inputStream()) as X509Certificate

        val signerConfig = ApkSigner.SignerConfig.Builder(
            keyName, privateKey, listOf(cert)
        ).build()

        ApkSigner.Builder(listOf(signerConfig))
            .setInputApk(input)
            .setOutputApk(output)
            .setMinSdkVersion(MIN_SDK_VERSION)
            .setV1SigningEnabled(true)
            .setV2SigningEnabled(true)
            .build()
            .sign()
    }

    /**
     * מאמת שהחתימה שבוצעה בפועל תקפה - לא מניחים ש-sign() תקין רק מפני שהוא לא זרק.
     * ApkVerifier (ספריית apksig) דורש - בלי אפשרות לעקוף, גם עם minSdkVersion
     * מפורש - לקרוא AndroidManifest.xml עבור בדיקת "target sandbox version"
     * שרלוונטית רק לקבצי APK אמיתיים. לקובץ jar רגיל (כמו services.jar) אין
     * manifest כזה בכלל, אז עבורו בודקים את חתימת ה-JAR (META-INF) ישירות
     * באמצעות java.util.jar.JarFile - מנגנון אימות חתימות מובנה של ה-JVM עצמו.
     */
    fun verify(file: File): Pair<Boolean, String> {
        val isRealApk = ZipFile(file).use { zf -> zf.getEntry("AndroidManifest.xml") != null }
        return if (isRealApk) verifyApk(file) else verifyPlainJar(file)
    }

    private fun verifyApk(file: File): Pair<Boolean, String> {
        val result = ApkVerifier.Builder(file)
            .setMinCheckedPlatformVersion(MIN_SDK_VERSION)
            .build()
            .verify()
        val log = buildString {
            append("אימות חתימה (ApkVerifier): ${if (result.isVerified) "תקף" else "לא תקף"}\n")
            append("  v1=${result.isVerifiedUsingV1Scheme} v2=${result.isVerifiedUsingV2Scheme}\n")
            for (err in result.errors) append("  שגיאה: $err\n")
        }
        return result.isVerified to log
    }

    private fun verifyPlainJar(file: File): Pair<Boolean, String> {
        return try {
            var checked = 0
            JarFile(file, true).use { jar ->
                if (jar.manifest == null) {
                    return false to "אימות חתימת JAR: אין META-INF/MANIFEST.MF - לא נחתם כראוי\n"
                }
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val e = entries.nextElement()
                    if (!e.isDirectory) {
                        jar.getInputStream(e).use { it.readBytes() } // מפעיל את בדיקת החתימה בפועל
                        checked++
                    }
                }
            }
            true to "אימות חתימת JAR (java.util.jar.JarFile, לא ApkVerifier - אין AndroidManifest.xml): תקף, $checked קבצים נבדקו\n"
        } catch (e: SecurityException) {
            false to "אימות חתימת JAR נכשל - חתימה לא תקפה: ${e.message}\n"
        } catch (e: java.io.IOException) {
            false to "אימות חתימת JAR נכשל - שגיאת קריאה: ${e.message}\n"
        }
    }
}
