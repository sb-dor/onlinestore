package com.sbdor.onlinestoreclaude.features.favorites.controller

import androidx.lifecycle.ViewModel
import com.sbdor.onlinestoreclaude.features.favorites.data.IFavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    val favoritesRepositoryImpl: IFavoritesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<FavoritesState>(FavoritesState.Initial)
    val state: StateFlow<FavoritesState> = _state.asStateFlow()


    suspend fun load() {
        _state.value = FavoritesState.InProgress
        val favorites = favoritesRepositoryImpl.favorites();
        _state.value = FavoritesState.Completed(favorites)
    }
}