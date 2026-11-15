# Azumiao Secret (Android)

An Android Jetpack Compose rewrite of **阿祖喵的秘密基地 (Azumiao Secret)**, preserving the dreamy pastel photo gallery and interactive three-row marquee carousel.

## Features
- **Asset Preloading & Caching**: Preloads all 25 bundled photos (`app/src/main/assets/images/1.jpg`–`25.jpg`) into Coil's memory cache on launch with a smooth progress overlay (`秘密基地资源装载中…`).
- **Staggered Shuffle & Rotation**: Uses Fisher-Yates shuffling plus circular array offsets (`0`, `8`, `16`) across three infinite-scrolling rows (`forward`, `backward`, `forward`) so identical photos never align vertically.
- **3D Ribbon Perspective & Interactive Zoom**: Renders each card with Y-axis 3D tilt (`±15°`); pressing, hovering, or tapping any card pauses its row and smoothly expands the card to full opacity and scale.
- **Animated Pastel Backdrop & Floating Header**: Recreates the 15-second 4-stop diagonal gradient (`#FF9A9E` → `#FAD0C4` → `#A18CD1`), floating title with layered pastel shadows, and monospace pill subtitle.
