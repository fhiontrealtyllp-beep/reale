package com.fhiont.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.fhiont.AppStrings
import com.fhiont.ui.theme.FhiontTheme

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmColor: Color = Color.Unspecified
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.extraLarge,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = confirmColor
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissText)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ConfirmationDialogPreview() {
    FhiontTheme {
        ConfirmationDialog(
            title = AppStrings.EXIT_DIALOG_TITLE,
            message = AppStrings.EXIT_DIALOG_MESSAGE,
            confirmText = AppStrings.EXIT_CONFIRM,
            dismissText = AppStrings.DIALOG_CANCEL,
            onConfirm = {},
            onDismiss = {}
        )
    }
}
