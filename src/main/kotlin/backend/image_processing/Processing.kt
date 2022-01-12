package backend.image_processing

import backend.Colourspace

/**
 * The base class for all types of processing nodes
 */
abstract class Processing {
    /**
     * Name of the node
     */
    abstract val name: String

    /**
     * The help string to display
     */
    abstract val help: String

    /**
     * The input colourspace of the node
     */
    open var inputColourspace: Colourspace = Colourspace.RGB

    /**
     * The list of possible input colourspaces of the node
     */
    abstract val inputColourspaces: List<Colourspace>
}