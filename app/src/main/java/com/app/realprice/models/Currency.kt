package com.app.realprice.models

class Currency(
    val code: String,
    val symbol: String,
    val name: String,
    val rate: Double
) {
    override fun equals(other: Any?): Boolean {
        if (other is Currency) {
            return this.code == other.code
        }

        return false
    }

    override fun toString(): String {
        return "Currency(code='$code', symbol='$symbol', name='$name', rate=$rate)"
    }
}
