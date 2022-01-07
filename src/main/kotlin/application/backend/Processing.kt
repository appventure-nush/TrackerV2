package application.backend

abstract class Processing {
    abstract val name: String
    abstract val help: String

    open var inputColourspace: Colourspace = Colourspace.RGB
    abstract val inputColourspaces: List<Colourspace>
}