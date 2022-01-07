package application.gui

import application.backend.postprocess.PostprocessingNode
import application.wrappers.generic.ProcessingNode

abstract class PostprocessingPane(node: PostprocessingNode) : ProcessingNode(node) {}