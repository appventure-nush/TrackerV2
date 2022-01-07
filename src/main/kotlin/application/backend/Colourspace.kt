package application.backend

val ALL_SPACES = listOf(Colourspace.RGB, Colourspace.HSV, Colourspace.GRAYSCALE)
enum class Colourspace {
    RGB,
    HSV,
    GRAYSCALE
}