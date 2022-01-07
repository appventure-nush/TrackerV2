package com.thepyprogrammer.fxtools.autocomplete

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.ComboBox
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.util.*


class AutoCompleteComboBoxListener<T>(private val comboBox: ComboBox<T>) : EventHandler<KeyEvent> {
    private val sb: StringBuilder = StringBuilder()
    private val data: ObservableList<T> = comboBox.items
    private var moveCaretToPos = false
    private var caretPos = 0

    override fun handle(event: KeyEvent) {
        when (event.code) {
            KeyCode.UP -> {
                caretPos = -1
                moveCaret(comboBox.editor.text.length)
                return
            }
            KeyCode.DOWN -> {
                if (!comboBox.isShowing) {
                    comboBox.show()
                }
                caretPos = -1
                moveCaret(comboBox.editor.text.length)
                return
            }
            KeyCode.BACK_SPACE -> {
                moveCaretToPos = true
                caretPos = comboBox.editor.caretPosition
            }
            KeyCode.DELETE -> {
                moveCaretToPos = true
                caretPos = comboBox.editor.caretPosition
            }
            else -> {}
        }
        if (event.code == KeyCode.RIGHT || event.code == KeyCode.LEFT || event.isControlDown || event.code == KeyCode.HOME || event.code == KeyCode.END || event.code == KeyCode.TAB) {
            return
        }
        val list: ObservableList<T> = FXCollections.observableArrayList()
        for (i in data.indices) {
            if (data[i].toString().lowercase(Locale.getDefault()).startsWith(
                    comboBox
                        .editor.text.lowercase(Locale.getDefault())
                )
            ) {
                list.add(data[i])
            }
        }
        val t = comboBox.editor.text
        comboBox.items = list
        comboBox.editor.text = t
        if (!moveCaretToPos) {
            caretPos = -1
        }
        moveCaret(t.length)
        if (!list.isEmpty()) {
            comboBox.show()
        }
    }

    private fun moveCaret(textLength: Int) {
        if (caretPos == -1) {
            comboBox.editor.positionCaret(textLength)
        } else {
            comboBox.editor.positionCaret(caretPos)
        }
        moveCaretToPos = false
    }

    init {
        comboBox.isEditable = true
        comboBox.onKeyPressed = EventHandler { comboBox.hide() }
        comboBox.onKeyReleased = this@AutoCompleteComboBoxListener
        comboBox.isFocusTraversable = false
    }
}