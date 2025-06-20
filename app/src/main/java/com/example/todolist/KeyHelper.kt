package com.example.todolist

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.crypto.KeyGenerator
import androidx.core.content.edit

object KeyHelper {
    private const val PREFS_FILE = "secure_prefs"
    private const val KEY_ALIAS = "aes_key"

    fun getOrCreateAesKey(context: Context): SecretKey {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val keyBytes = sharedPreferences.getString(KEY_ALIAS, null)?.let {
            android.util.Base64.decode(it, android.util.Base64.DEFAULT)
        }

        return if (keyBytes != null) {
            SecretKeySpec(keyBytes, "AES")
        } else {
            val keygen = KeyGenerator.getInstance("AES")
            keygen.init(256)
            val newKey = keygen.generateKey()

            sharedPreferences.edit() {
                putString(
                    KEY_ALIAS,
                    android.util.Base64.encodeToString(newKey.encoded, android.util.Base64.DEFAULT)
                )
            }

            newKey
        }
    }
}