package application.gui.postprocessing

import application.backend.postprocess.fitting.EllipseFittingNode
import application.gui.PostprocessingPane

class EllipsePane: PostprocessingPane(EllipseFittingNode()) {}