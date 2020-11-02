package com.adidas.luissilva.data.enum

enum class CountType {
    STEP,
    DISTANCE,
    UNKNOWN;

    companion object {
        fun convert(type: String) =
                when {
                    type.contains(STEP.name, ignoreCase = true) -> {
                        STEP
                    }
                    type.contains(DISTANCE.name, ignoreCase = true) -> {
                        DISTANCE
                    }
                    else -> {
                        UNKNOWN
                    }
                }

    }
}