package application.model

import javafx.beans.property.SimpleStringProperty

class Address(firstName: String = "", lastName: String = "", email: String = "") {
    private val firstNameProperty = SimpleStringProperty(firstName)
    private val lastNameProperty = SimpleStringProperty(lastName)
    private val emailProperty = SimpleStringProperty(email)

    var firstName: String
        get() = firstNameProperty.get()
        set(value) { firstNameProperty.set(value) }

    var lastName: String
        get() = lastNameProperty.get()
        set(value) { lastNameProperty.set(value) }

    var email: String
        get() = emailProperty.get()
        set(value) { emailProperty.set(value) }
}
