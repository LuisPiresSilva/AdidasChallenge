package com.adidas.luissilva.data.enum

enum class ExerciseType {
    WALKING,
    RUNNING,
    UNKNOWN;

    companion object {
        fun convert(type: String) =
                when {
                    type.contains(WALKING.name, ignoreCase = true) -> {
                        WALKING
                    }
                    type.contains(RUNNING.name, ignoreCase = true) -> {
                        RUNNING
                    }
                    else -> {
                        UNKNOWN
                    }
                }

    }
}