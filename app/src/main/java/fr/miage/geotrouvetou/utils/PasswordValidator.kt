package fr.miage.geotrouvetou.utils

data class PasswordValidation(
    val hasMinLength: Boolean,
    val hasDigit: Boolean,
    val hasSpecial: Boolean,
    val hasUppercase: Boolean,
) {
    val isValid: Boolean get() = hasMinLength && hasDigit && hasSpecial && hasUppercase

    fun firstError(): String? = when {
        !hasMinLength -> "Le mot de passe doit contenir au moins 8 caractères"
        !hasDigit -> "Le mot de passe doit contenir au moins 1 chiffre"
        !hasUppercase -> "Le mot de passe doit contenir au moins 1 majuscule"
        !hasSpecial -> "Le mot de passe doit contenir au moins 1 caractère spécial"
        else -> null
    }

    companion object {
        val EMPTY = PasswordValidation(
            hasMinLength = false,
            hasDigit = false,
            hasSpecial = false,
            hasUppercase = false,
        )

        fun of(password: String) = PasswordValidation(
            hasMinLength = password.length >= 8,
            hasDigit = password.any { it.isDigit() },
            hasSpecial = password.any { !it.isLetterOrDigit() },
            hasUppercase = password.any { it.isUpperCase() },
        )

        fun confirmError(password: String, confirm: String): String? =
            if (confirm.isNotEmpty() && confirm != password) "Les mots de passe ne correspondent pas" else null
    }
}
