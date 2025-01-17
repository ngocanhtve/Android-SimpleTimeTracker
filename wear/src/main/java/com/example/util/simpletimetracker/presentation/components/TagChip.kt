/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.example.util.simpletimetracker.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Checkbox
import androidx.wear.compose.material.CheckboxDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.SplitToggleChip
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChipDefaults
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.util.simpletimetracker.wearrpc.Tag

enum class TagSelectionMode {
    SINGLE, MULTI,
}

@Composable
fun TagChip(
    tag: Tag,
    onClick: () -> Unit = {},
    onToggleOn: () -> Unit = {},
    onToggleOff: () -> Unit = {},
    mode: TagSelectionMode = TagSelectionMode.SINGLE,
) {
    when (mode) {
        TagSelectionMode.SINGLE -> {
            SingleSelectTagChip(tag = tag, onClick = onClick)
        }

        TagSelectionMode.MULTI -> {
            MultiSelectTagChip(
                tag = tag,
                onClick = onClick,
                onToggleOn = onToggleOn,
                onToggleOff = onToggleOff,
            )
        }
    }

}

@Composable
private fun SingleSelectTagChip(tag: Tag, onClick: () -> Unit) {
    Chip(
        onClick = onClick,
        label = { Text(tag.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        colors = ChipDefaults.chipColors(backgroundColor = Color(tag.color)),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 10.dp),
    )
}

@Composable
private fun MultiSelectTagChip(
    tag: Tag,
    onClick: () -> Unit = {},
    onToggleOn: () -> Unit = {},
    onToggleOff: () -> Unit = {},
    checked: Boolean = false,
) {
    var _checked by remember { mutableStateOf(checked) }
    SplitToggleChip(
        checked = _checked,
        onCheckedChange = {
            _checked = !_checked
            if (_checked) {
                onToggleOn()
            } else {
                onToggleOff()
            }
        },
        onClick = onClick,
        label = { Text(tag.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        toggleControl = {
            Checkbox(
                checked = _checked,
                colors = CheckboxDefaults.colors(
                    checkedBoxColor = Color.White,
                    checkedCheckmarkColor = Color.White,
                    uncheckedBoxColor = Color.White,
                    uncheckedCheckmarkColor = Color.White,
                ),
            )
        },
        colors = ToggleChipDefaults.splitToggleChipColors(
            backgroundColor = Color(tag.color),
            splitBackgroundOverlayColor = if (_checked) {
                Color.White.copy(alpha = .1F)
            } else {
                Color.Black.copy(alpha = .3F)
            },
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(top = 10.dp),
    )
}

@Preview(device = WearDevices.LARGE_ROUND)
@Composable
private fun Default() {
    TagChip(
        tag = Tag(id = 123, name = "Sleep", isGeneral = false, color = 0xFF123456),
        onClick = {},
    )
}


@Preview(device = WearDevices.LARGE_ROUND)
@Composable
private fun MultiSelectMode() {
    TagChip(
        tag = Tag(id = 123, name = "Sleep", isGeneral = false, color = 0xFF654321),
        onClick = {},
        mode = TagSelectionMode.MULTI,
    )
}

@Preview(device = WearDevices.LARGE_ROUND)
@Composable
private fun MultiSelectChecked() {
    MultiSelectTagChip(
        tag = Tag(id = 123, name = "Sleep", isGeneral = false, color = 0xFF654321),
        onClick = {},
        checked = true,
    )
}