package com.orech.patcher

import android.content.Context
import com.android.apksig.ApkSigner
import com.android.apksig.ApkVerifier
import java.io.File
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec

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

    /** מאמת שהחתימה שבוצעה בפועל תקפה - לא מניחים ש-sign() תקין רק מפני שהוא לא זרק. */
    fun verify(file: File): Pair<Boolean, String> {
        val result = ApkVerifier.Builder(file)
            .setMinCheckedPlatformVersion(MIN_SDK_VERSION)
            .build()
            .verify()
        val log = buildString {
            append("אימות חתימה: ${if (result.isVerified) "תקף" else "לא תקף"}\n")
            append("  v1=${result.isVerifiedUsingV1Scheme} v2=${result.isVerifiedUsingV2Scheme}\n")
            for (err in result.errors) append("  שגיאה: $err\n")
        }
        return result.isVerified to log
    }
}
