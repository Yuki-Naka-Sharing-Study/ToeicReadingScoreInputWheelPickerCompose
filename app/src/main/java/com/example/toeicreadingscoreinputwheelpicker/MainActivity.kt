package com.example.toeicreadingscoreinputwheelpicker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.toeicreadingscoreinputwheelpicker.ui.theme.ToeicReadingScoreInputWheelPickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToeicReadingScoreInputWheelPickerTheme {
                ReadingScoreScreen()
            }
        }
    }
}

@Composable
fun ReadingScoreScreen() {
    Column {
        var readingScore by rememberSaveable { mutableIntStateOf(0) }
        var showReadingPicker by remember { mutableStateOf(false) }

        ReadingScorePicker(Modifier, readingScore, onScoreChange = { showReadingPicker = true})
    }
}

@Composable
fun ReadingScorePicker(
    modifier: Modifier = Modifier,
    readingScore: Int,
    onScoreChange: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        // スコア入力ボタン
        Button(
            modifier = Modifier.align(Alignment.TopCenter),
            onClick = { showDialog = true },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(androidx.compose.ui.graphics.Color.Green),
        ) {
            Text(
                text = "TOEIC Readingスコア: $readingScore",
                color = androidx.compose.ui.graphics.Color.White
            )
        }
    }

    // スコア入力ダイアログ
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Companion.LightGray),
                modifier = Modifier
                    .size(width = 240.dp, height = 280.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    // スコア選択のWheel Picker
                    ReadingScorePickerView(
                        score = readingScore,
                        onScoreChange = onScoreChange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = { showDialog = false },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(androidx.compose.ui.graphics.Color.Green),
                    ) {
                        Text(
                            text = "確定",
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReadingScorePickerView(
    score: Int,
    onScoreChange: (Int) -> Unit
) {
    val hundreds = score / 100 // 100の位
    val tens = (score % 100) / 10 // 10の位
    val ones = score % 10 // 1の位

    val hundredOptions = listOf(0, 5) // 1桁目（100の位）は0か5
    val tenOptions = (0..9).toList() // 2桁目（10の位）は0～9
    val oneOptions = (0..4).toList() // 3桁目（1の位）は0～4

    // 状態管理のためにrememberを使う
    val hundredState = remember { mutableStateOf(hundreds) }
    val tenState = remember { mutableStateOf(tens) }
    val oneState = remember { mutableStateOf(ones) }

    // スコア変更をトリガーするLaunchedEffect
    LaunchedEffect(hundredState.value, tenState.value, oneState.value) {
        onScoreChange(hundredState.value * 100 + tenState.value * 10 + oneState.value)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 100の位
        FVerticalWheelPicker(
            modifier = Modifier.weight(1f),
            options = hundredOptions,
            currentValue = hundredState.value,
            onValueChange = { hundredState.value = it }
        ) { value ->
            Text(text = value.toString())
        }

        // 10の位
        FVerticalWheelPicker(
            modifier = Modifier.weight(1f),
            options = tenOptions,
            currentValue = tenState.value,
            onValueChange = { tenState.value = it }
        ) { value ->
            Text(text = value.toString())
        }

        // 1の位
        FVerticalWheelPicker(
            modifier = Modifier.weight(1f),
            options = oneOptions,
            currentValue = oneState.value,
            onValueChange = { oneState.value = it }
        ) { value ->
            Text(text = value.toString())
        }
    }
}

@Composable
fun FVerticalWheelPicker(
    modifier: Modifier = Modifier,
    options: List<Int>,
    currentValue: Int,
    onValueChange: (Int) -> Unit,
    content: @Composable (Int) -> Unit
) {
    val state = remember { mutableStateOf(currentValue) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // WheelPicker表示
        val selectedValue = options[state.value]
        content(selectedValue)

        // 変更時に新しい値を設定
        onValueChange(selectedValue)
    }
}
