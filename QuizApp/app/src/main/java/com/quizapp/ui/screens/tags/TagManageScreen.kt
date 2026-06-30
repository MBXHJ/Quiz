package com.quizapp.ui.screens.tags

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quizapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManageScreen(
    bankId: Long,
    onBack: () -> Unit,
    viewModel: TagManageViewModel = hiltViewModel()
) {
    LaunchedEffect(bankId) { viewModel.loadTags(bankId) }
    val s by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("标签管理", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showCreateDialog() },
                containerColor = Primary, contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("新建标签", fontWeight = FontWeight.SemiBold) }
            )
        }
    ) { padding ->
        if (s.tags.isEmpty()) {
            EmptyPlaceholder(Icons.Default.Label, "还没有标签", "点击下方按钮创建标签", Modifier.fillMaxSize().padding(padding))
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text("共 ${s.tags.size} 个标签", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                }
                items(s.tags, key = { it.id }) { tag ->
                    val count = s.tagQuestionCounts[tag.id] ?: 0
                    var showDelete by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(tag.color))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(tag.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text("$count 道题目", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            IconButton(onClick = { showDelete = true }) {
                                Icon(Icons.Default.Delete, "删除", tint = WrongRed.copy(alpha = 0.6f))
                            }
                        }
                    }
                    if (showDelete) {
                        AlertDialog(
                            onDismissRequest = { showDelete = false },
                            shape = RoundedCornerShape(20.dp),
                            title = { Text("删除标签", fontWeight = FontWeight.Bold) },
                            text = { Text("确定删除「${tag.name}」标签吗？\n题目不会丢失，仅移除标签关联。") },
                            confirmButton = {
                                Button(onClick = { viewModel.deleteTag(tag.id, bankId); showDelete = false }, colors = ButtonDefaults.buttonColors(containerColor = WrongRed)) {
                                    Text("删除")
                                }
                            },
                            dismissButton = { TextButton(onClick = { showDelete = false }) { Text("取消") } }
                        )
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    // Create tag dialog
    if (s.showCreateDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideCreateDialog() },
            shape = RoundedCornerShape(20.dp),
            title = { Text("新建标签", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = s.newTagName,
                        onValueChange = { viewModel.updateNewTagName(it) },
                        label = { Text("标签名称") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("选择颜色", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TAG_COLORS.forEach { (color, _) ->
                            Box(
                                Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(color))
                                    .clickable { viewModel.selectColor(color) }
                            ) {
                                if (color == s.selectedColor) {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .border(3.dp, Color.White, CircleShape)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.createTag(bankId) },
                    enabled = s.newTagName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("创建")
                }
            },
            dismissButton = { TextButton(onClick = { viewModel.hideCreateDialog() }) { Text("取消") } }
        )
    }
}
