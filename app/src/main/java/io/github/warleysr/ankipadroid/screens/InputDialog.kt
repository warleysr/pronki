import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.warleysr.ankipadroid.R

@Composable
fun TextEditDialog(
    @StringRes name: Int,
    storedValue: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {

    var currentInput by remember {
        mutableStateOf(TextFieldValue(storedValue))
    }

    Surface {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(stringResource(id = name))
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                currentInput,
                label = { Text(stringResource(id = R.string.api_key)) },
                onValueChange = {
                    currentInput = it
                },
                singleLine = true
            )
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    onSave(currentInput.text)
                    onDismiss()
                }) {
                    Text(stringResource(id = R.string.save))
                }
            }
        }
    }
}