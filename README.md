# Morphe Patches

Custom patches for [Morphe](https://github.com/MorpheApp), the Android app patcher. Each patch changes how a supported app looks or behaves, without touching the original APK on disk.

## Usage

1. Install [Morphe Manager](https://github.com/MorpheApp) on your Android device.
2. Add this repository as a patch source: `https://github.com/heltonricardo/morphe-patches`
3. Pick the app, provide its APK, select the patches you want and apply.
4. Uninstall the original app if needed (the patched build is signed differently), then install the patched one.

## Available patches

### Waze

Supported version: `5.27.5.0` (APKM)

| Patch | Description |
| --- | --- |
| Hide route balloons | Removes the street name balloons shown along the route on the map. |

## Building locally

Requires JDK 21.

```bash
./gradlew :patches:buildAndroid
```

The bundle is generated at `patches/build/libs/patches-*.mpp`.

## Releases

Releases are automated with semantic-release and Conventional Commits. Merges to `main` publish stable releases, and `dev` publishes pre-releases.

## License

GPL-3.0
