import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPreviewInfo(currentColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(
            modifier = Modifier
                .background(
                    currentColor,
                    shape = CircleShape
                )
                .size(48.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
    }
}
