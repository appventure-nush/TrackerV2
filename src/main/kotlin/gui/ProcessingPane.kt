package gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import backend.image_processing.preprocess.PreprocessingNode

@Preview
@Composable
fun ProcessingPane(node: PreprocessingNode, options: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
        Row(Modifier.fillMaxSize(), Arrangement.spacedBy(5.dp)) {
            Text(node.name, fontSize = 30.sp)
            Button({}) {
                Text("Help")
            }
        }
    }
}