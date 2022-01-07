package application.wrappers.generic

import javafx.scene.Node
import javafx.scene.layout.Pane

class RoundedPane(vararg children: Node, radius: Int = 30, padding: Int = 30): Pane(*children) {
    init {
        this.style = "-fx-hgap: 20px; -fx-padding: ${padding}px; -fx-background-radius: ${radius}px; " +
                "-fx-border-radius: ${radius}px; -fx-border-width: 5px; -fx-border-color: black; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0);"
    }
}