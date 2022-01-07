package application.gui

import application.backend.preprocess.PreprocessingNode
import application.wrappers.generic.ProcessingNode

abstract class PreprocessingPane(node: PreprocessingNode) : ProcessingNode(node) {}