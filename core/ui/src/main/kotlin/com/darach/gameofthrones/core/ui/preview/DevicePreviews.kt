package com.darach.gameofthrones.core.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Phone",
    device = "spec:width=411dp,height=891dp",
    showBackground = true
)
annotation class PreviewPhone

@Preview(
    name = "Phone Landscape",
    device = "spec:width=891dp,height=411dp",
    showBackground = true
)
annotation class PreviewPhoneLandscape

@Preview(
    name = "Foldable",
    device = "spec:width=673dp,height=841dp",
    showBackground = true
)
annotation class PreviewFoldable

@Preview(
    name = "Foldable Landscape",
    device = "spec:width=841dp,height=673dp",
    showBackground = true
)
annotation class PreviewFoldableLandscape

@Preview(
    name = "Small Tablet",
    device = "spec:width=800dp,height=1280dp,dpi=240",
    showBackground = true
)
annotation class PreviewSmallTablet

@Preview(
    name = "Tablet",
    device = "spec:width=1024dp,height=1366dp,dpi=240",
    showBackground = true
)
annotation class PreviewTablet

@Preview(
    name = "Tablet Landscape",
    device = "spec:width=1366dp,height=1024dp,dpi=240",
    showBackground = true
)
annotation class PreviewTabletLandscape

@Preview(
    name = "Large Tablet",
    device = "spec:width=1920dp,height=1200dp,dpi=240",
    showBackground = true
)
annotation class PreviewLargeTablet

@Preview(
    name = "Desktop",
    device = "spec:width=1920dp,height=1080dp,dpi=160",
    showBackground = true
)
annotation class PreviewDesktop

@Preview(
    name = "Phone - Dark",
    device = "spec:width=411dp,height=891dp",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class PreviewPhoneDark

@Preview(
    name = "Tablet - Dark",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class PreviewTabletDark

@PreviewPhone
@PreviewFoldable
@PreviewTablet
@PreviewDesktop
annotation class PreviewAllDevices

@PreviewPhone
@PreviewPhoneDark
annotation class PreviewPhoneThemes

@PreviewTablet
@PreviewTabletDark
annotation class PreviewTabletThemes

@PreviewPhone
@PreviewPhoneDark
@PreviewFoldable
@PreviewTablet
@PreviewTabletDark
@PreviewDesktop
annotation class PreviewComprehensive

@PreviewPhone
@PreviewPhoneLandscape
annotation class PreviewOrientation

@PreviewPhone
@PreviewFoldable
@PreviewTabletLandscape
annotation class PreviewAdaptiveLayout
