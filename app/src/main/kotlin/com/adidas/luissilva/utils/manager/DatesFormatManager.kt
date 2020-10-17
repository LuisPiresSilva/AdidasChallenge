package com.adidas.luissilva.utils.manager

import org.threeten.bp.format.DateTimeFormatter
import java.util.*

object DatesFormatManager {

    val format_DD_MM_YYYY: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())

}