package com.example.digifin.data.model

enum class PaymentType(val title: String) {
    CASH("Cash"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    UPI("UPI/Bank Transfer"),
    OTHERS("Others")
}
