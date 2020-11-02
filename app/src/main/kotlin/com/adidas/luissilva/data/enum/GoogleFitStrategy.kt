package com.adidas.luissilva.data.enum


enum class GoogleFitStrategy {
    SPECIFIC,
    GENERIC;

    companion object {
        fun convert(type: String) =
                if (ExerciseType.values().any { type.contains(it.name, ignoreCase = true) }) {
                    SPECIFIC
                } else {
                    GENERIC
                }

    }

}