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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
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
        var selectedScore by rememberSaveable { mutableIntStateOf(readingScore) }

        ReadingScorePicker(
            Modifier,
            readingScore = readingScore,
            selectedScore = selectedScore,
            onScoreChange = { selectedScore = it },
            onConfirm = { readingScore = selectedScore }
        )
    }
}

@Composable
fun ReadingScorePicker(
    modifier: Modifier = Modifier,
    readingScore: Int,
    selectedScore: Int,
    onScoreChange: (Int) -> Unit,
    onConfirm: () -> Unit
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
                text = "スコア: $readingScore",
                color = androidx.compose.ui.graphics.Color.White
            )
        }
    }

    // スコア入力ダイアログ
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.LightGray),
                modifier = Modifier
                    .size(width = 240.dp, height = 320.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    // スコア選択のWheel Picker
                    ReadingScorePickerView(
                        score = selectedScore,
                        onScoreChange = onScoreChange
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 確定ボタン
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        onClick = {
                            onConfirm() // 確定時にスコアを親に渡す
                            showDialog = false
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(androidx.compose.ui.graphics.Color.Green),
                    ) {
                        Text(
                            text = "確定",
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
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

    // 状態管理のためにrememberを使う
    val hundredState = remember { mutableIntStateOf(hundreds) }
    val tenState = remember { mutableIntStateOf(tens) }
    val oneState = remember { mutableIntStateOf(ones) }

    // スコア変更をトリガーする
    LaunchedEffect(hundredState.intValue, tenState.intValue, oneState.intValue) {
        onScoreChange(hundredState.intValue * 100 + tenState.intValue * 10 + oneState.intValue)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement
            .SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 100の位
        ThreeDigits(hundredState)
        // 10の位
        TwoDigits(tenState)
        // 1の位
        OneDigit(oneState)
    }
}

@Composable
private fun ThreeDigits(state: MutableIntState) {
    FVerticalWheelPicker(
        modifier = Modifier.width(64.dp),
        count = 5,
        itemHeight = 48.dp,
        unfocusedCount = 2,
    ) { index ->
        Text(
            index.toString(),
            color = androidx.compose.ui.graphics.Color.Black
        )
        state.intValue = index
    }
}

@Composable
private fun TwoDigits(state: MutableIntState) {
    FVerticalWheelPicker(
        modifier = Modifier.width(64.dp),
        count = 10,
        itemHeight = 48.dp,
        unfocusedCount = 2,
    ) { index ->
        Text(
            index.toString(),
            color = androidx.compose.ui.graphics.Color.Black
        )
        state.intValue = index
    }
}

// 三桁目の数字は0か5のみしか入力不可
@Composable
private fun OneDigit(state: MutableIntState) {
    val items = listOf(0, 5)

    FVerticalWheelPicker(
        modifier = Modifier.width(64.dp),
        count = items.size,
        itemHeight = 48.dp,
        unfocusedCount = 2,
    ) { index ->
        Text(
            items[index].toString(),
            color = androidx.compose.ui.graphics.Color.Black
        )
        state.intValue = items[index]
    }
}
