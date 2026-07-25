import http.server
import socketserver
import os
import socket

PORT = 8080
DIRECTORY = os.path.join(os.path.dirname(os.path.abspath(__file__)), "repo")

class CORSRequestHandler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        self.send_header('Access-Control-Allow-Origin', '*')
        self.send_header('Access-Control-Allow-Methods', 'GET, OPTIONS')
        self.send_header('Cache-Control', 'no-store, no-cache, must-revalidate')
        super().end_headers()

    def do_OPTIONS(self):
        self.send_response(200, "ok")
        self.end_headers()

def get_local_ip():
    try:
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except Exception:
        return "127.0.0.1"

if __name__ == "__main__":
    local_ip = get_local_ip()
    print("=" * 60)
    print("🚀 DeviantArt Mihon Extension Repo Server Running!")
    print("=" * 60)
    print(f"👉 Local Mihon Repository URL: http://{local_ip}:{PORT}/index.min.json")
    print(f"👉 Localhost URL:             http://127.0.0.1:{PORT}/index.min.json")
    print("=" * 60)
    print("How to add to Mihon:")
    print("1. Open Mihon app on your device")
    print("2. Go to More > Settings > Browse > Extension Repos")
    print(f"3. Tap 'Add' and paste: http://{local_ip}:{PORT}/index.min.json")
    print("4. Go to Browse > Extensions tab and install DeviantArt!")
    print("=" * 60)

    with socketserver.TCPServer(("", PORT), CORSRequestHandler) as httpd:
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nServer stopped.")
