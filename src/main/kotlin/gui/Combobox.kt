package gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize

@Preview
@Composable
fun <T> Combobox(label: String, selectedItem: MutableState<T>, items: List<T>, modifier: Modifier = Modifier,
                 fontSize: TextUnit = 12.sp, onValueChanged: () -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    val textfieldSize = remember { mutableStateOf(Size.Zero)}

    val icon = Icons.Filled.ArrowDropDown

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedItem.value.toString(),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to the Dropdown the same width
                    textfieldSize.value = coordinates.size.toSize()
                },
            label = { Text(label, fontSize = fontSize, color = AppTheme.colorScheme.primary) },
            trailingIcon = {
                Icon(icon,"", Modifier.clickable { expanded.value = !expanded.value })
            },
            textStyle = TextStyle(fontSize = fontSize),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppTheme.colorScheme.primary,
                unfocusedBorderColor = AppTheme.colorScheme.primary,
            )
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.width(
                with(LocalDensity.current) {
                    textfieldSize.value.width.toDp()
                }
            )
        ) {
            items.forEach {
                DropdownMenuItem(
                    text = {
                        Text(text = it.toString(), fontSize = fontSize)
                    },
                    onClick = {
                        selectedItem.value = it
                        expanded.value = false
                        onValueChanged()
                    }
                )
            }
        }
    }
}