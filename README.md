<div align="center">
<img src="./draftsman/art/logo.svg" width=150 height=150/>
<br>
<h1>Draftsman</h1>
</div>

Draftsman is an on-device layout inspector for Android apps. It allows you to view various properties of rendered Android Views such as width, height, class name, paddings, margins etc.

Draftsman aims to combine capabilities of different Android tools such as `Show Layout Bounds` and Android Studio's layout inspector.

The primary objective of Draftsman is to provide information around rendered views directly from your app which is easily accessible to devs, designers, PMs or any one who has installed the app. No need to use any external tool or rely on debug variant of your app.

## Features

* Width & Height Info for any view

* Class name for any view

* Margin and Padding visualization

* Dimension values in both Pixel(Px) and dp/sp

* TextView color and size information 

* Overlay a grid

* Overlay a image to compare UI

Check usage guide at bottom for more details.

## Integration
Add following code to your build.gradle

```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation "com.gojek.draftsman:draftsman:0.0.1"
}
```

To enable Draftsman in an activity

```kotlin
Draftsman.install(activity)
```

To exit from Draftsman, there is an exit button in information window. And if you want do it programmatically, you can call

```kotlin
Draftsman.uninstall(activity)
```

## Usage Guide

This section will help you understand what information Draftsman can capture from a view.

This is our base view

![draftsman-base.png](/draftsman/art/draftsman-base.png)

**When Draftsman is enabled**

This is how our view looks like once Draftsman is enabled. Inspectable views are highlighted and a arrow appears on right to open drawer.

![draftsman-enabled.png](/draftsman/art/draftsman-enabled.png)

**Draftsman Setting Drawer**

You can use this drawer to customise a few settings.

![draftsman-drawer.png](/draftsman/art/draftsman-drawer.png)

**TextView Inspection**

On tapping of first text "Hi Folks" we can observe this overlay. It provides information on height, width, textsize and textcolor. Dimensions can be observed in dp or px.

Inspection details overlay can be closed using cross icon and Draftsman can be closed by pressing Exit button.

![draftsman-text.png](/draftsman/art/draftsman-text.png)

**View Inspection**

Any arbitrary view can be inspected as well allowing us to inspect height, width and class name

![draftsman-view.png](/draftsman/art/draftsman-view.png)

**Margin and Padding**

Tapping around views highlights margins and paddings if available. Padding is shown with green background and margin with red line.

![draftsman-padding.png](/draftsman/art/draftsman-padding.png)

![draftsman-margin.png](/draftsman/art/draftsman-margin.png)

**Nested Views**

If there are multiple views stacked within same bounds, Draftsman will prompt you to select the view to be inspected

![draftsman-nested-views.png](/draftsman/art/draftsman-nested-views.png)

**Grid Overlay**

You can also add a size configurable grid overlay on your screen from setting drawer.

![draftsman-grid.png](/draftsman/art/draftsman-grid.png)

**Image Overlay**

You can also overlay a screenshot on top of existing screen to check for UI differences. A slider on bottom can be used to fade out overlay image.

Note: Storage read permission should be provided for this to work.

![draftsman-overlay.png](/draftsman/art/draftsman-overlay.png)
