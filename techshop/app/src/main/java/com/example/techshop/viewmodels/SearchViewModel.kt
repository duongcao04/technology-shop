package com.example.techshop.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techshop.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    // State for recent searches
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    // State for search results
    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults: StateFlow<List<Product>> = _searchResults.asStateFlow()

    // State for search query
    private val _currentQuery = MutableStateFlow("")
    val currentQuery: StateFlow<String> = _currentQuery.asStateFlow()

    // Function to set the current search query
    fun setSearchQuery(query: String) {
        _currentQuery.value = query
    }

    // Function to search products
    fun searchProducts(products: List<Product>, query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _searchResults.value = emptyList()
                return@launch
            }

            // Filter products based on name and description
            val filteredProducts = products.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }

            _searchResults.value = filteredProducts

            // Add to recent searches if not already there
            addToRecentSearches(query)
        }
    }

    // Function to add a search query to recent searches
    private fun addToRecentSearches(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            val currentList = _recentSearches.value.toMutableList()

            // Remove the query if it already exists
            currentList.remove(query)

            // Add it to the top of the list
            currentList.add(0, query)

            // Limit to 10 recent searches
            if (currentList.size > 10) {
                currentList.removeAt(currentList.size - 1)
            }

            _recentSearches.value = currentList
        }
    }

    // Function to clear recent searches
    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    // Function to clear search results
    fun clearSearchResults() {
        _searchResults.value = emptyList()
        _currentQuery.value = ""
    }
}