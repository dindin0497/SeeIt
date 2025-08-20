# SeeIt

Inclusive Android app with two core capabilities:  

1. ✋ **ASL Mode** – Speech → Text → ASL Video  
2. 👁️ **Object Detection Mode** – Camera → Detect → Speak  

---

## ✋ 1. ASL Mode  

### What It Does  
- 🎙️ Listens from mic → converts **speech to text**  
- 🔤 Maps text to **ASL signs**  
- ▶️ Fetches ASL videos from the web and **plays them in-app**  

### Key Features  
- ⚡ Partial results (near word-by-word feel)  
- 🎯 Candidate ranking (choose the best sign/video)  
- 🔄 Caching + retry/backoff to reduce lag  

### Demo  

https://github.com/user-attachments/assets/c31b4838-6ffb-413a-a6cd-38d3aea3a1af

## 👁️ 2. Object Detection Mode  

### What It Does  
- 📷 Uses **camera** to detect objects in real time  
- 🗣️ Speaks object names via **Text-to-Speech (TTS)**  
- 🏷️ Optionally shows detected labels on screen  

### Key Features  
- ⚡ Real-time detection (CameraX + ML Kit/TFLite)  
- 🔕 Debounced announcements (avoid spam)  
- 📊 Confidence threshold + label coalescing  
- 📦 Works **offline** using local models  

### Demo  
https://github.com/user-attachments/assets/88cdca3c-0b67-4257-b9bf-bb1751122672





