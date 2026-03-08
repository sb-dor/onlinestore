package com.sbdor.onlinestoreclaude.features.favorites.models

import com.sbdor.onlinestoreclaude.features.products.models.Product
import java.time.Instant

data class Favorite(
    var product: Product,
    var datetime: Instant = Instant.now()
)
