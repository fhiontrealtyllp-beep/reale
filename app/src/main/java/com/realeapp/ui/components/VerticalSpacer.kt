package com.realeapp.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalSpacer(height: Dp, modifier: Modifier = Modifier) {
    Spacer(modifier = modifier.height(height))
}

@Composable
fun VerticalSpacer4(modifier: Modifier = Modifier) = VerticalSpacer(4.dp, modifier)

@Composable
fun VerticalSpacer8(modifier: Modifier = Modifier) = VerticalSpacer(8.dp, modifier)

@Composable
fun VerticalSpacer12(modifier: Modifier = Modifier) = VerticalSpacer(12.dp, modifier)

@Composable
fun VerticalSpacer16(modifier: Modifier = Modifier) = VerticalSpacer(16.dp, modifier)

@Composable
fun VerticalSpacer24(modifier: Modifier = Modifier) = VerticalSpacer(24.dp, modifier)

@Composable
fun VerticalSpacer32(modifier: Modifier = Modifier) = VerticalSpacer(32.dp, modifier)
