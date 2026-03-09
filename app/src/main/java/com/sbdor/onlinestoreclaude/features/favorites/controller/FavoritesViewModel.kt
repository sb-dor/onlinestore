package com.sbdor.onlinestoreclaude.features.favorites.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sbdor.onlinestoreclaude.features.favorites.data.IFavoritesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesViewModel(
    val favoritesRepositoryImpl: IFavoritesRepository
) : ViewModel() {

    companion object {
        fun factory(favoritesRepository: IFavoritesRepository) = viewModelFactory {
            initializer { FavoritesViewModel(favoritesRepository) }
        }
    }

    private val _state = MutableStateFlow<FavoritesState>(FavoritesState.Initial)
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    // WARNING: load() is declared as 'suspend fun' here.
    // This means the screen (FavoritesScreen) MUST call it from inside a coroutine context.
    // In this case LaunchedEffect(Unit) in the screen provides that context — so it works.
    //
    // However this is NOT the recommended pattern for ViewModel functions called from UI.
    // The problem: if you ever call viewModel.load() from a Button onClick or any normal
    // lambda, it will be a compile error because onClick is not a coroutine.
    //
    // RECOMMENDED ALTERNATIVE: make load() a normal fun and use viewModelScope.launch inside.
    // This way the screen and any other caller can call it without needing a coroutine context:
    //
    // fun load() {
    //     viewModelScope.launch {
    //         _state.value = FavoritesState.InProgress
    //         val favorites = favoritesRepositoryImpl.favorites()
    //         _state.value = FavoritesState.Completed(favorites)
    //     }
    // }
    //
    // But it's just for learning purposes
    suspend fun load() {
        _state.value = FavoritesState.InProgress
        val favorites = favoritesRepositoryImpl.favorites()
        _state.value = FavoritesState.Completed(favorites)
    }
}
