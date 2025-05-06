
package com.example.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.ui.theme.ToDoListTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    val taskList = remember { mutableStateListOf<Pair<String, String>>() }

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
            onValueChange = { dateInput = it },
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
                .background(color = Color.Magenta)
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
                .background(color = Color.Magenta)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Button(onClick = {
            if (dateInput.isNotBlank() && taskInput.isNotBlank()) {
                taskList.add(Pair(dateInput, taskInput))
                dateInput = ""
                taskInput = ""
            }
        }) {
            Text(stringResource(R.string.add_button))
        }

        Spacer(modifier = Modifier.height(16.dp))

        taskList.forEach { (date, task) ->
            Text(
                text = "Date: $date  |  Task: $task",
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                    .background(color = Color.Magenta, shape = MaterialTheme.shapes.medium)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun EditDateField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier){

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.add_date_label)) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    )
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