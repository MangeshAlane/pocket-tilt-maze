# GitHub Upload Instructions for Pocket Tilt Maze

## Quick Steps to Build Your APK on GitHub

### 1. Login to GitHub
- Go to: https://github.com/login
- Sign in (or create free account at https://github.com/signup)

### 2. Create New Repository
- After login, go to: https://github.com/new
- Repository name: `pocket-tilt-maze`
- Description: "Tilt-controlled maze game for Android"
- Make it **Public** (required for free Actions)
- **DO NOT** check "Add README" or ".gitignore" (we already have these)
- Click "Create repository"

### 3. Upload Your Files
On the empty repository page:
- Click "uploading an existing file" link
- Open File Explorer: `c:\Users\Mahavir\Downloads\pocket tilt`
- Select ALL files and folders (Ctrl+A)
- Drag and drop them into the GitHub upload area
- Wait for upload to complete
- Scroll down, click "Commit changes"

### 4. Wait for Build
- Click "Actions" tab at the top
- You'll see "Build Android APK" workflow running
- Wait 5-10 minutes for green checkmark ✓
- If it fails, click on it to see errors

### 5. Download APK
- Click on the completed workflow (green checkmark)
- Scroll down to "Artifacts" section
- Click "pocket-tilt-maze-apk" to download
- Extract the ZIP file
- You'll get: `app-debug.apk`

### 6. Install on Phone
- Transfer APK to your Android phone (USB/email/Drive)
- On phone: Settings → Security → Enable "Unknown Sources"
- Tap the APK file
- Click "Install"
- Open "Pocket Tilt Maze" app
- Enjoy! 🎮

---

## Troubleshooting

**Build fails?**
- Check the Actions log for errors
- Most common: Missing files - make sure you uploaded ALL files

**Can't upload files?**
- Try smaller batches (upload folders separately)
- Or use GitHub Desktop app

**APK won't install?**
- Make sure "Unknown Sources" is enabled
- Check if you have enough storage space
- Try uninstalling any previous version

---

## What You're Getting

Your fully functional Pocket Tilt Maze game with:
- ✅ Daily Maze (new maze every day)
- ✅ Quick Maze (random mazes)
- ✅ Tilt controls using accelerometer
- ✅ Settings (vibration, size, sensitivity)
- ✅ Stats tracking
- ✅ AdMob test ads

---

## Need Help?

If you get stuck at any step, let me know which step and I'll help you through it!
