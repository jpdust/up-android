package com.unstampedpages.app.data.model

data class ChecklistProgress(
    val checkedCount: Int = 0,
    val totalCount: Int = 0
) {
    val percentage: Float
        get() = if (totalCount > 0) checkedCount.toFloat() / totalCount else 0f

    val displayText: String
        get() = "$checkedCount/$totalCount packed"

    val isComplete: Boolean
        get() = totalCount > 0 && checkedCount == totalCount
}
