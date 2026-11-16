package com.orech.patcher

import android.content.Context
import com.android.apksig.ApkSigner
import java.io.File
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.spec.PKCS8EncodedKeySpec

/** חותם APK עם אחד מהמפתחות שמגיעים עם האפליקציה (assets/keys/<name>.pk8 + .x509.pem). */
object Signer {

    fun sign(context: Context, input: File, output: File, keyName: String): Boolean {
        return try {
            val pk8Bytes = context.assets.open("keys/$keyName.pk8").readBytes()
            val certBytes = context.assets.open("keys/$keyName.x509.pem").readBytes()

            val keyFactory = KeyFactory.getInstance("RSA")
            val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(pk8Bytes))

            val cert = CertificateFactory.getInstance("X.509")
                .generateCertificate(certBytes.inputStream())

            val signerConfig = ApkSigner.SignerConfig.Builder(
                keyName, privateKey, listOf(cert)
            ).build()

            ApkSigner.Builder(listOf(signerConfig))
                .setInputApk(input)
                .setOutputApk(output)
                .setV1SigningEnabled(true)
                .setV2SigningEnabled(true)
                .build()
                .sign()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
