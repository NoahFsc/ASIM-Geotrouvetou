package fr.miage.geotrouvetou.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.miage.geotrouvetou.R

private val SearchBarShape = RoundedCornerShape(50)

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = SearchBarShape,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.12f),
            )
            .background(Color.White, SearchBarShape)
            .border(1.dp, colorResource(R.color.text_disabled), SearchBarShape)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = colorResource(R.color.text_darker),
        ),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = colorResource(R.color.text_placeholder),
                        fontSize = 16.sp,
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview(name = "SearchBar – Empty")
@Composable
private fun SearchBarEmptyPreview() {
    Box(
        modifier = Modifier
            .background(colorResource(R.color.background))
            .padding(16.dp)
    ) {
        SearchBar(
            value = "",
            onValueChange = {},
            placeholder = "Choisir votre lieu de randonnée",
        )
    }
}

@Preview(name = "SearchBar – With Text")
@Composable
private fun SearchBarWithTextPreview() {
    Box(
        modifier = Modifier
            .background(colorResource(R.color.background))
            .padding(16.dp)
    ) {
        SearchBar(
            value = "Paris",
            onValueChange = {},
            placeholder = "Choisir votre lieu de randonnée",
        )
    }
}
