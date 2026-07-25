# DeviantArt Extension for Mihon / Tachiyomi

A fully functional, high-performance Kotlin extension for **Mihon**, **Tachiyomi**, **Tachimanga**, **Aniyomi**, and compatible readers.

## 🌟 Key Features

- 🖼️ **Popular & Latest Feed**: Browse trending, popular, and latest artworks directly from DeviantArt.
- 🔍 **Advanced Search & Filtering**:
  - Search by keywords (e.g. `landscape`, `wallpaper`, `concept art`).
  - Search by Artist / User using `@username` or `by:username`.
  - Filter by Category (Digital Art, Photography, Anime/Manga, 3D Art, Traditional, Wallpapers).
  - Sort by Popular, Latest, or All-Time Popular.
- ⚡ **High Resolution Image Extractor**: Resolves Wix Media parameters to deliver maximum resolution images without watermarks or downscaling.
- 🛡️ **Stable RSS & oEmbed Core**: Built on DeviantArt's official RSS 2.0 XML and oEmbed endpoints for 100% uptime and protection against web layout changes.

---

## 🛠️ Repository Structure

```text
DeviantArt Ext/
├── app/
│   ├── build.gradle.kts
│   └── src/main/java/eu/kanade/tachiyomi/extension/all/deviantart/
│       ├── DeviantArt.kt         # Core HttpSource extension class
│       ├── DeviantArtFilters.kt  # Custom search & category filters
│       ├── DeviantArtDto.kt      # Data Transfer Objects
│       └── DeviantArtUtils.kt    # Image & HTML utilities
├── repo/
│   └── index.min.json            # Mihon Extension Repo Index
├── server.py                     # Local repo server script
├── build.gradle.kts              # Root build configuration
└── settings.gradle.kts           # Gradle settings
```

---

## 🚀 How to Add & Use in Mihon

### Option A: Local Repo Server (Easiest)

1. Run the Python repository server:
   ```bash
   python server.py
   ```
2. Note the Repository URL displayed in the terminal output (e.g., `http://192.168.1.5:8080/index.min.json`).
3. Open **Mihon** on your Android device.
4. Navigate to **More > Settings > Browse > Extension repos**.
5. Tap **Add Repo** and paste your repository URL.
6. Go to **Browse > Extensions** tab, find **DeviantArt**, and tap **Install**.

### Option B: Hosting on GitHub Pages

1. Push this project to GitHub.
2. Enable GitHub Pages on the `repo` directory or branch.
3. Add the GitHub Pages URL to Mihon under **Settings > Browse > Extension repos**:
   `https://<your-username>.github.io/<repo-name>/index.min.json`

---

## 🔨 Building from Source

To compile the `.apk` package locally using Gradle:

```bash
gradlew assembleRelease
```

The output `.apk` file will be generated in `app/build/outputs/apk/release/`.
