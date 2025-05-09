package com.example.todolist

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.util.Calendar

class MainActivity : ComponentActivity() {
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

@Composable
fun ToDoLayout() {
    var dateInput by remember { mutableStateOf("") }
    var taskInput by remember { mutableStateOf("") }
    var isDateValid by remember { mutableStateOf(true) }

    data class TaskItem(
        val date: String,
        val task: String,
        var isDone: Boolean = false
    )

    val taskList = remember { mutableStateListOf<TaskItem>() }

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
                taskList.add(TaskItem(dateInput, taskInput))
                dateInput = ""
                taskInput = ""
            }
        }) {
            Text(stringResource(R.string.add_button))
        }

        Spacer(modifier = Modifier.height(16.dp))

        taskList.forEachIndexed { index, taskItem ->
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
                            taskList[index] = taskItem.copy(isDone = !taskItem.isDone)
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

                Text(
                    text = "Date: ${formatDateForDisplay(taskItem.date)} | Task: ${taskItem.task}",
                    fontSize = 18.sp,
                    color = if (taskItem.isDone) Color.DarkGray else Color.Black,
                    style = if (taskItem.isDone) MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                    ) else MaterialTheme.typography.bodyMedium
                )
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
                val formattedDate = "%02d/%02d/%04d".format(day, month + 1, year)
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToDoListTheme {
        ToDoLayout()
    }
}