package fr.miage.geotrouvetou.utils

import android.util.Patterns

object UserFieldValidator {

    fun validateNom(nom: String): String? =
        if (nom.isBlank()) "Le nom est requis" else null

    fun validatePrenom(prenom: String): String? =
        if (prenom.isBlank()) "Le prénom est requis" else null

    fun validateEmail(email: String): String? = when {
        email.isBlank() -> "L'adresse email est requise"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format d'email invalide"
        else -> null
    }

    fun isNomValid(nom: String) = validateNom(nom) == null
    fun isPrenomValid(prenom: String) = validatePrenom(prenom) == null
    fun isEmailValid(email: String) = validateEmail(email) == null

    fun capitalizeFirst(value: String) = value.replaceFirstChar { it.uppercaseChar() }
}
