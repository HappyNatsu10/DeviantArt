import os
import json
import hashlib

REPO_DIR = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "repo")
INDEX_PATH = os.path.join(REPO_DIR, "index.min.json")

def calculate_sha256(filepath):
    sha256_hash = hashlib.sha256()
    with open(filepath, "rb") as f:
        for byte_block in iter(lambda: f.read(4096), b""):
            sha256_hash.update(byte_block)
    return sha256_hash.hexdigest()

def update_repo_index():
    os.makedirs(REPO_DIR, exist_ok=True)
    
    # Base extension metadata
    extension_entry = {
        "name": "DeviantArt",
        "pkg": "eu.kanade.tachiyomi.extension.all.deviantart",
        "apk": "tachiyomi-all.deviantart-v1.4.1.apk",
        "lang": "all",
        "code": 2,
        "version": "1.4.1",
        "nsfw": 0,
        "hasReadme": True,
        "hasChangelog": False,
        "sources": [
            {
                "id": 6512398401928374152,
                "lang": "all",
                "name": "DeviantArt",
                "baseUrl": "https://www.deviantart.com"
            }
        ]
    }
    
    apk_path = os.path.join(REPO_DIR, extension_entry["apk"])
    if os.path.exists(apk_path):
        extension_entry["sha256"] = calculate_sha256(apk_path)
        extension_entry["size"] = os.path.getsize(apk_path)
    
    repo_data = [extension_entry]
    
    # Save minified JSON
    with open(INDEX_PATH, "w", encoding="utf-8") as f:
        json.dump(repo_data, f, separators=(',', ':'))
        
    print(f"✅ Successfully updated {INDEX_PATH}")
    print(f"📦 Extensions in catalog: {len(repo_data)}")

if __name__ == "__main__":
    update_repo_index()
