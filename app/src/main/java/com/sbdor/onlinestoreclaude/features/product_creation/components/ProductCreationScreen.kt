package com.sbdor.onlinestoreclaude.features.product_creation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sbdor.onlinestoreclaude.di.LocalDependenciesScope
import com.sbdor.onlinestoreclaude.features.product_creation.components.controllers.ProductCreationDataViewModel
import com.sbdor.onlinestoreclaude.features.product_creation.controller.ProductCreationState
import com.sbdor.onlinestoreclaude.features.product_creation.controller.ProductCreationViewModel
import com.sbdor.onlinestoreclaude.features.products.models.Category


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCreationScreen(
    productId: Int?,
    onBackClick: () -> Unit,
    onSuccessfullyCreation: () -> Unit
) {

    val localDependenciesScope = LocalDependenciesScope.current

    val productCreationViewModel: ProductCreationViewModel =
        viewModel(factory = ProductCreationViewModel.factory(localDependenciesScope.productRepository))
    val productCreationState = productCreationViewModel.state.collectAsState()


    val productCreationDataViewModel: ProductCreationDataViewModel = viewModel(
        factory = ProductCreationDataViewModel.factory(localDependenciesScope.productRepository)
    )
    val productCreationDataState = productCreationDataViewModel.state.collectAsState()

    // local form fields — populated once the product loads
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.ELECTRONICS) }
    var rating by remember { mutableFloatStateOf(0f) }
    var reviewCount by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        productCreationDataViewModel.load(productId)
    }

    // fill form fields when the product data arrives
    LaunchedEffect(productCreationDataState.value) {
        productCreationDataState.value?.let { product ->
            name = product.name ?: ""
            description = product.description ?: ""
            price = product.price?.toString() ?: ""
            selectedCategory = product.category ?: Category.ELECTRONICS
            rating = product.rating
            reviewCount = product.reviewCount.toString()
        }
    }

    // navigate back when save completes
    LaunchedEffect(productCreationState.value) {
        if (productCreationState.value is ProductCreationState.Completed) {
            onBackClick()
            onSuccessfullyCreation()
        }
    }

    val isLoading = productCreationState.value is ProductCreationState.InProgress

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product creation") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (productCreationDataState.value != null) {
                    productCreationViewModel.create(
                        productCreationDataState.value!!.copy(
                            name = name.ifBlank { null },
                            description = description.ifBlank { null },
                            price = price.toDoubleOrNull(),
                            category = selectedCategory,
                            rating = rating,
                            reviewCount = reviewCount.toIntOrNull() ?: 0,
                        )
                    )
                }
            }) {
                if (isLoading) CircularProgressIndicator() else Text("Save")
            }
        },
    ) { paddingValues ->

        if (productCreationDataState.value == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center,
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix = { Text("$") },
            )

            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = selectedCategory.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                ) {
                    Category.entries.filter { it != Category.ALL }.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.displayName) },
                            onClick = {
                                selectedCategory = category
                                dropdownExpanded = false
                            },
                        )
                    }
                }
            }

            Text(
                text = "Rating: ${"%.1f".format(rating)}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Slider(
                value = rating,
                onValueChange = { rating = it },
                valueRange = 0f..5f,
                steps = 9,
            )

            OutlinedTextField(
                value = reviewCount,
                onValueChange = { reviewCount = it },
                label = { Text("Review Count") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )

            Spacer(modifier = Modifier.height(80.dp)) // space for FAB
        }
    }
}