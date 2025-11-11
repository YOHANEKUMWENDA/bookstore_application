//package com.example.myapplication.ui.theme.Data
package com.example.myapplication.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class CartItem(
    val book: Book,
    var quantity: Int = 1
)

object CartManager {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: SnapshotStateList<CartItem> = _cartItems

    // Total number of items in cart
    val cartItemCount = mutableStateOf(0)

    // Total price of all items
    val cartTotal = mutableStateOf(0.0)

    fun addToCart(book: Book, quantity: Int = 1) {
        val existingItem = _cartItems.find { it.book.id == book.id }

        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            _cartItems.add(CartItem(book, quantity))
        }

        updateCartTotals()
    }

    fun removeFromCart(bookId: Int) {
        _cartItems.removeIf { it.book.id == bookId }
        updateCartTotals()
    }

    fun updateQuantity(bookId: Int, newQuantity: Int) {
        val item = _cartItems.find { it.book.id == bookId }
        if (item != null) {
            if (newQuantity <= 0) {
                removeFromCart(bookId)
            } else {
                item.quantity = newQuantity
                updateCartTotals()
            }
        }
    }

    fun clearCart() {
        _cartItems.clear()
        updateCartTotals()
    }

    fun isInCart(bookId: Int): Boolean {
        return _cartItems.any { it.book.id == bookId }
    }

    fun getItemQuantity(bookId: Int): Int {
        return _cartItems.find { it.book.id == bookId }?.quantity ?: 0
    }

    private fun updateCartTotals() {
        cartItemCount.value = _cartItems.sumOf { it.quantity }
        cartTotal.value = _cartItems.sumOf { it.book.price * it.quantity }
    }
}
