package com.sbdor.onlinestoreclaude.features.product_creation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sbdor.onlinestoreclaude.di.LocalDependenciesScope
import com.sbdor.onlinestoreclaude.features.product_creation.components.controllers.ProductCreationDataViewModel
import com.sbdor.onlinestoreclaude.features.product_creation.controller.ProductCreationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreationScreen(onBackClick: () -> Unit, onSavedClick: () -> Unit) {

    val localDependenciesScope = LocalDependenciesScope.current

    val productCreationViewModel: ProductCreationViewModel =
        viewModel(factory = ProductCreationViewModel.factory(localDependenciesScope.productRepository))
    val productCreationState = productCreationViewModel.state.collectAsState()


    val productCreationDataViewModel: ProductCreationDataViewModel = viewModel()
    val productCreationDataState = productCreationDataViewModel.state.collectAsState()


    Scaffold(topBar = {
        TopAppBar(title = { Text("Online Store") })
    }, floatingActionButton = {
        FloatingActionButton(onClick = {
            productCreationViewModel.create(productCreationDataState.value)
        }) { }
    }) { }
}