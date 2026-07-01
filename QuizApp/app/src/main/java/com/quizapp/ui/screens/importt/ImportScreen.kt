package com.quizapp.ui.screens.importt

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quizapp.ui.theme.*

/**
 * 通过 ContentResolver 查询 URI 的真实文件名，
 * 解决 lastPathSegment 不带扩展名导致 "不支持的文件格式" 的问题。
 */
private fun resolveFileName(context: android.content.Context, uri: Uri): String {
    var name = "unknown.txt"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx >= 0) {
                cursor.getString(idx)?.let { name = it }
            }
        }
    }
    return name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    onBack: () -> Unit,
    onImportComplete: () -> Unit,
    viewModel: ImportViewModel = hiltViewModel()
) {
    val s by viewModel.uiState.collectAsState()
    val fm = LocalFocusManager.current

    val context = LocalContext.current

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            val fileName = resolveFileName(context, it)
            viewModel.importFile(it, fileName)
        }
    }

    LaunchedEffect(s.isComplete) { if (s.isComplete) { kotlinx.coroutines.delay(1800); onImportComplete() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("导入题库", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(16.dp))

            // Icon
            Box(contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(96.dp), shape = CircleShape, color = BorderLight) {}
                Surface(modifier = Modifier.size(68.dp), shape = CircleShape, color = Primary.copy(alpha = 0.08f)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.MenuBook, null, Modifier.size(34.dp), tint = Primary) }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("创建新题库", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("txt", "md", "docx", "xlsx", "json").forEach { fmt ->
                    Surface(shape = RoundedCornerShape(6.dp), color = BorderLight) {
                        Text(".$fmt", Modifier.padding(horizontal = 8.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = s.bankName, onValueChange = { viewModel.updateBankName(it) },
                label = { Text("题库名称") }, placeholder = { Text("如：人工智能训练师、考研政治") },
                leadingIcon = { Icon(Icons.Default.Edit, null, Modifier.size(20.dp)) },
                modifier = Modifier.fillMaxWidth(), singleLine = true, enabled = !s.isImporting,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { fm.clearFocus() })
            )

            Spacer(Modifier.height(22.dp))

            when {
                s.isImporting -> Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.04f))
                ) {
                    Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Primary, strokeWidth = 3.dp)
                        Spacer(Modifier.height(16.dp))
                        Text("正在解析题库...", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(4.dp))
                        Text("请稍候，大文件可能需要几秒", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                s.isComplete -> Card(
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(modifier = Modifier.size(52.dp), shape = CircleShape, color = CorrectGreen.copy(alpha = 0.1f)) {
                            Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.CheckCircle, null, tint = CorrectGreen, modifier = Modifier.size(30.dp)) }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("导入成功！", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CorrectGreen)
                        Spacer(Modifier.height(6.dp))
                        Surface(shape = RoundedCornerShape(10.dp), color = CorrectGreenBg) {
                            Text("成功导入 ${s.successCount} 道题", Modifier.padding(horizontal = 14.dp, vertical = 8.dp), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                        }
                        if (s.failCount > 0) { Spacer(Modifier.height(6.dp)); Text("${s.failCount} 道题解析失败", style = MaterialTheme.typography.bodySmall, color = WrongRed) }
                    }
                }
                else -> Button(
                    onClick = {
                        viewModel.clearError(); picker.launch(arrayOf("text/plain", "text/markdown", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel", "application/json"))
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp), enabled = s.bankName.isNotBlank(),
                    shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.UploadFile, null, Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text("选择文件并导入", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }

            if (s.error != null) {
                Spacer(Modifier.height(14.dp))
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = WrongRedBg)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ErrorOutline, null, Modifier.size(20.dp), tint = WrongRed); Spacer(Modifier.width(8.dp)); Text(s.error!!, color = WrongRed, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
