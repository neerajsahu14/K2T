package com.app.k2t.firebase.utils

enum class OrderStatus(val code: Int, val displayName: String) {
    PENDING(0, "Pending"),
    ACCEPTED(1, "Accepted"),
    PREPARING(2, "Preparing"),
    IN_PROGRESS(3, "In Progress"),
    COMPLETED(4, "Completed"),
    CANCELLED(5, "Cancelled");


    companion object {
        fun fromCode(code: Int) = entries.firstOrNull { it.code == code } ?: PENDING // Default to PENDING if code not found
        fun fromDisplayName(displayName: String) = entries.firstOrNull { it.displayName == displayName } ?: PENDING
    }
}
