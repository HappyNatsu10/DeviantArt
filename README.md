# DeviantArt Extension for Mihon / Tachiyomi

A fully functional, high-performance Kotlin extension for **Mihon**, **Tachiyomi**, **Tachimanga**, **Aniyomi**, and compatible readers.

---

## 🌐 Putting the Extension Online (Free GitHub Pages Hosting)

Follow these quick steps to host your custom extension repository online so anyone can add it to Mihon anywhere in the world!

### Step 1: Create a Repository on GitHub
1. Go to [github.com/new](https://github.com/new)
2. Name your repository (e.g., `deviantart-mihon-extension`).
3. Set visibility to **Public** (required for free GitHub Pages).
4. Click **Create repository** (do NOT add a README or .gitignore as we already created them).

### Step 2: Push your local code to GitHub
Run the following commands in your terminal from this project folder:

```bash
git remote add origin https://github.com/<YOUR-GITHUB-USERNAME>/<YOUR-REPO-NAME>.git
git branch -M main
git push -u origin main
```

### Step 3: Enable GitHub Pages
1. On your GitHub repository page, click **Settings**.
2. On the left sidebar, click **Pages**.
3. Under **Build and deployment -> Source**, select **GitHub Actions**.
4. That's it! GitHub Actions will automatically build the APK and deploy your repository catalog to GitHub Pages.

---

## 📲 Your Online Mihon Extension Repository URL

Once deployed, your permanent online repository URL to enter into Mihon is:

```text
https://<YOUR-GITHUB-USERNAME>.github.io/<YOUR-REPO-NAME>/index.min.json
```

### How to Add in Mihon:
1. Open **Mihon** on your Android device.
2. Go to **More > Settings > Browse > Extension repos**.
3. Tap **Add Repo** and paste your online URL:
   `https://<YOUR-GITHUB-USERNAME>.github.io/<YOUR-REPO-NAME>/index.min.json`
4. Go to **Browse > Extensions** tab, find **DeviantArt**, and tap **Install**!

---

## ⚡ Alternative Free Online Hosting (Vercel / Netlify / Render / Cloudflare Pages)

If you prefer hosting just the `repo/` directory on Vercel or Netlify:
1. Connect your repository to **Vercel** or **Cloudflare Pages**.
2. Set Build Output Directory to `repo`.
3. Your online Mihon repository URL will be:
   `https://<your-project>.vercel.app/index.min.json` or `https://<your-project>.pages.dev/index.min.json`
