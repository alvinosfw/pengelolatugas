package com.example.taskmate

import androidx.compose.material3.RadioButton
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.taskmate.data.Task
import com.example.taskmate.data.TaskDatabase
import com.example.taskmate.ui.theme.TaskMateTheme
import com.example.taskmate.ui.theme.TaskMateBackground
import com.example.taskmate.ui.theme.TaskMateBlue
import com.example.taskmate.ui.theme.TaskMateGray
import com.example.taskmate.ui.theme.TaskMateGreen
import com.example.taskmate.ui.theme.TaskMateOrange
import com.example.taskmate.ui.theme.TaskMateRed
import com.example.taskmate.ui.theme.TaskMateText
import com.example.taskmate.ui.theme.TaskMateWhite

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TaskDatabase.getDatabase(applicationContext)

        setContent {
            TaskMateTheme {
                TaskMateHome(database = database)
            }
        }
    }
}

@Composable
fun TaskMateHome(database: TaskDatabase) {
    val tasks by database.taskDao()
        .getAllTasks()
        .collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf("Semua") }

    val filteredTasks = remember(tasks, searchQuery, selectedPriority) {
        tasks.filter { task ->
            val query = searchQuery.trim()
            val matchesSearch =
                task.title.contains(query, ignoreCase = true) ||
                        task.courseName.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)

            val matchesPriority =
                selectedPriority == "Semua" || task.priority == selectedPriority

            matchesSearch && matchesPriority
        }
    }

    val scope = rememberCoroutineScope()
    var showForm by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var courseName by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Sedang") }
    var status by remember {
        mutableStateOf("Belum Dikerjakan")
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = TaskMateBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(TaskMateBackground)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = TaskMateBlue,
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Text(
                    text = "TaskMate",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Atur tugas kuliahmu, raih targetmu.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.88f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        label = "Semua Tugas",
                        count = tasks.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        label = "Prioritas Tinggi",
                        count = tasks.count { it.priority == "Tinggi" }.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (showForm) {
                    Text(
                        text = "Tambah Tugas Baru",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaskMateText
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = TaskMateWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = courseName,
                                onValueChange = { courseName = it },
                                label = { Text("Nama Mata Kuliah") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("Judul Tugas") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Deskripsi Tugas") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                minLines = 2
                            )
                            OutlinedTextField(
                                value = deadline,
                                onValueChange = { deadline = it },
                                label = { Text("Deadline (contoh: 30-10-2026)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true
                            )

                            Text(
                                text = "Prioritas",
                                fontWeight = FontWeight.SemiBold,
                                color = TaskMateText
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Rendah", "Sedang", "Tinggi").forEach { item ->
                                    FilterChip(
                                        selected = priority == item,
                                        onClick = { priority = item },
                                        label = { Text(item) }
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    if (courseName.isNotBlank() &&
                                        title.isNotBlank() &&
                                        deadline.isNotBlank()
                                    ) {
                                        val newTask = Task(
                                            courseName = courseName.trim(),
                                            title = title.trim(),
                                            description = description.trim(),
                                            deadline = deadline.trim(),
                                            priority = priority
                                        )
                                        scope.launch {
                                            database.taskDao().insertTask(newTask)
                                            courseName = ""
                                            title = ""
                                            description = ""
                                            deadline = ""
                                            priority = "Sedang"
                                            showForm = false
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Simpan Tugas", fontWeight = FontWeight.Bold)
                            }

                            TextButton(
                                onClick = { showForm = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Batal")
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Daftar Tugas",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TaskMateText
                    )
                    Text(
                        text = "${filteredTasks.size} tugas ditemukan",
                        fontSize = 13.sp,
                        color = TaskMateGray
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Cari tugas atau mata kuliah") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Filter prioritas",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TaskMateText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Semua", "Rendah", "Sedang", "Tinggi").forEach { item ->
                            FilterChip(
                                selected = selectedPriority == item,
                                onClick = { selectedPriority = item },
                                label = { Text(item, fontSize = 12.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (filteredTasks.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = TaskMateWhite)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📝", fontSize = 34.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Belum ada tugas yang cocok",
                                    fontWeight = FontWeight.SemiBold,
                                    color = TaskMateText
                                )
                                Text(
                                    text = "Coba ubah pencarian atau tambahkan tugas baru.",
                                    color = TaskMateGray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    } else {
                        filteredTasks.forEach { task ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = TaskMateWhite),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = task.title,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TaskMateText,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Spacer(modifier = Modifier.size(8.dp))
                                        PriorityBadge(priority = task.priority)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = task.courseName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TaskMateBlue
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Deskripsi: ${task.description.ifBlank { "Tidak ada deskripsi" }}",
                                        fontSize = 13.sp,
                                        color = TaskMateGray
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Deadline: ${task.deadline}",
                                        fontSize = 13.sp,
                                        color = TaskMateGray
                                    )
                                    Text(
                                        text = "Status: ${task.status}",
                                        fontSize = 13.sp,
                                        color = TaskMateGray
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                editingTask = task
                                                courseName = task.courseName
                                                title = task.title
                                                description = task.description
                                                deadline = task.deadline
                                                priority = task.priority
                                                showForm = true },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = TaskMateBlue
                                            )
                                        ) {
                                            Text("Edit")
                                        }
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    database.taskDao().deleteTask(task)
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = TaskMateRed
                                            )
                                        ) {
                                            Text("Hapus")
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showForm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TaskMateBlue
                        )
                    ) {
                        Text("+  Tambah Tugas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    label: String,
    count: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.16f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = count,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun PriorityBadge(priority: String) {
    val badgeColor = when (priority) {
        "Tinggi" -> TaskMateRed
        "Sedang" -> TaskMateOrange
        "Rendah" -> TaskMateGreen
        else -> TaskMateGray
    }

    Surface(
        color = badgeColor.copy(alpha = 0.12f),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = priority,
            color = badgeColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
