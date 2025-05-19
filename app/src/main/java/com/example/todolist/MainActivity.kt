package com.example.todolist

import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.ui.theme.ToDoListTheme
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilterChip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.Calendar
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.spec.IvParameterSpec
import android.util.Base64
import androidx.compose.material.icons.filled.Lock

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ToDoLayout()
                }
            }
        }
    }
}

enum class FilterType {
    ALL, PENDING, COMPLETED
}

data class TaskItem(
    var date: String,
    var task: String,
    var isDone: Boolean = false,
    var isEncrypted: Boolean = true
)

val ivMap = mutableMapOf<String, ByteArray>()
private const val KEY_ALIAS = "my_aes_key_alias"

@Composable
fun ToDoLayout() {
    var dateInput by remember { mutableStateOf("") }
    var taskInput by remember { mutableStateOf("") }
    var isDateValid by remember { mutableStateOf(true) }
    var selectedFilter by remember { mutableStateOf(FilterType.ALL) }

    val taskList = remember { mutableStateListOf<TaskItem>() }

    val filteredTasks = remember(taskList, selectedFilter) {
        when (selectedFilter) {
            FilterType.ALL -> taskList
            FilterType.PENDING -> taskList.filter { !it.isDone }
            FilterType.COMPLETED -> taskList.filter { it.isDone }
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(R.string.add_date),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )

        EditDateField(
            value = dateInput,
            onValueChange = {
                dateInput = it
                isDateValid = true
            },
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.add_task),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 10.dp)
                .align(alignment = Alignment.Start)
        )

        EditTaskField(
            value = taskInput,
            onValueChange = { taskInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(5.dp))

        Button(onClick = {
            if (dateInput.isNotBlank() && taskInput.isNotBlank()) {
                isDateValid = true
                taskList.add(TaskItem(crypto(dateInput), crypto(taskInput), isEncrypted = true))
                dateInput = ""
                taskInput = ""
            }
        }) {
            Text(stringResource(R.string.add_button))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FilterChip(
                selected = selectedFilter == FilterType.ALL,
                onClick = { selectedFilter = FilterType.ALL },
                label = { Text("All") },
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            FilterChip(
                selected = selectedFilter == FilterType.PENDING,
                onClick = { selectedFilter = FilterType.PENDING },
                label = { Text("Pending") },
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            FilterChip(
                selected = selectedFilter == FilterType.COMPLETED,
                onClick = { selectedFilter = FilterType.COMPLETED },
                label = { Text("Completed") },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredTasks.isEmpty()) {
            Text(
                text = "No tasks found",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            filteredTasks.forEachIndexed { index, taskItem ->
                val actualIndex = taskList.indexOfFirst { it.date == taskItem.date && it.task == taskItem.task }
                if (actualIndex != -1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(color = Color.hsl(300f, 0.4f, 0.75f), shape = MaterialTheme.shapes.medium)
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(24.dp)
                                .clip(CircleShape)
                                .clickable {
                                    taskList[actualIndex] = taskList[actualIndex].copy(isDone = !taskList[actualIndex].isDone)
                                },
                            color = if (taskItem.isDone) Color.hsl(120f, 0.4f, 0.55f) else Color.LightGray,
                            contentColor = Color.White,
                            shape = CircleShape,
                        ) {
                            if (taskItem.isDone) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formatDateForDisplay(taskItem.date),
                                    fontSize = 16.sp,
                                    color = if (taskItem.isDone) Color.DarkGray else Color.Black,
                                    style = if (taskItem.isDone) MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    ) else MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Task",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = taskItem.task,
                                    fontSize = 16.sp,
                                    color = if (taskItem.isDone) Color.DarkGray else Color.Black,
                                    style = if (taskItem.isDone) MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                    ) else MaterialTheme.typography.bodyMedium,
                                    softWrap = true
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete task",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    taskList.removeAt(actualIndex)
                                },
                            tint = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = if (taskItem.isEncrypted) "Decrypt task" else "Encrypt task",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    authenticateBiometric(
                                        context = context,
                                        onSuccess = {
                                            taskList[actualIndex] = taskList[actualIndex].copy(
                                                date = if (taskItem.isEncrypted) decrypt(taskItem.date) else crypto(taskItem.date),
                                                task = if (taskItem.isEncrypted) decrypt(taskItem.task) else crypto(taskItem.task),
                                                isEncrypted = !taskItem.isEncrypted
                                            )
                                        },
                                        onError = {
                                            Toast.makeText(context, "Biometric authentication failed.", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                },
                            tint = if(taskItem.isEncrypted) MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if(filteredTasks.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {

                    authenticateBiometric(
                        context = context,
                        onSuccess = {
                            taskList.replaceAll { task ->
                                task.copy(
                                    date = decrypt(task.date),
                                    task = decrypt(task.task)
                                )
                            }
                        },
                        onError = {
                            Toast.makeText(context, "Biometric authentication failed.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }) {
                    Text(stringResource(R.string.decrypt_button))
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    taskList.replaceAll { task ->
                        task.copy(
                            date = crypto(task.date),
                            task = crypto(task.task)
                        )
                    }
                }) {
                    Text(stringResource(R.string.crypt_button))
                }


            }

        }

    }
}

@Composable
fun EditDateField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showDatePicker) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val formattedDate = "%02d%02d%04d".format(day, month + 1, year)
                onValueChange(formattedDate)
                showDatePicker = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.add_date_label)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = DateTransformation(),
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Select date",
                modifier = Modifier.clickable { showDatePicker = true }
            )
        }
    )
}

fun formatDateForDisplay(date: String): String {
    return if (date.length == 8) {
        "${date.take(2)}/${date.substring(2, 4)}/${date.substring(4)}"
    } else {
        date
    }
}

class DateTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        var output = ""
        text.text.forEachIndexed { index, char ->
            output += char
            when (index) {
                1, 3 -> output += "/"
            }
        }
        return TransformedText(AnnotatedString(output), DateOffsetMapper)
    }
}

object DateOffsetMapper : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        return when {
            offset <= 1 -> offset
            offset <= 3 -> offset + 1
            else -> offset + 2
        }
    }

    override fun transformedToOriginal(offset: Int): Int {
        return when {
            offset <= 2 -> offset
            offset <= 5 -> offset - 1
            else -> offset - 2
        }
    }
}

@Composable
fun EditTaskField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier){

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.add_task_label)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
}

// cryptography
fun getKey(): SecretKey {
    val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    val key = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
    if (key != null) {
        return key.secretKey
    }

    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    keyGenerator.init(
        KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
    )

    return keyGenerator.generateKey()
}

fun crypto(text: String): String {
    val plaintext = text.toByteArray()

    val key = getKey()

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, key)

    val ciphertext = cipher.doFinal(plaintext)
    val iv = cipher.iv

    val encodedCiphertext = Base64.encodeToString(ciphertext, Base64.DEFAULT)
    val encodedIv = Base64.encodeToString(iv, Base64.DEFAULT)

    ivMap[encodedCiphertext] = iv

    return "$encodedIv:$encodedCiphertext"
}

fun decrypt(encoded: String): String {
    val key = getKey()

    val parts = encoded.split(":")
    val iv = Base64.decode(parts[0], Base64.DEFAULT)
    val ciphertext = Base64.decode(parts[1], Base64.DEFAULT)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val ivSpec = IvParameterSpec(iv)
    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

    val decryptedBytes = cipher.doFinal(ciphertext)
    return String(decryptedBytes)
}

// biometric
fun authenticateBiometric(context: Context, onSuccess: () -> Unit, onError: () -> Unit) {
    val activity = context.findFragmentActivity()
    if (activity == null) {
        onError()
        return
    }

    val executor = ContextCompat.getMainExecutor(context)

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError()
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Authentication")
        .setSubtitle("Use your fingerprint or face ID to unlock the tasks")
        .setNegativeButtonText("Cancel")
        .build()

    biometricPrompt.authenticate(promptInfo)
}

fun Context.findFragmentActivity(): FragmentActivity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is FragmentActivity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return null
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoListTheme {
        ToDoLayout()
    }
}