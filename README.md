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

### Why It Matters for the Deaf Community  
- 🤟 Many Deaf and Hard of Hearing (HoH) individuals **use ASL as their primary language**, not written/spoken English.  
- 🗣️ This feature bridges the gap by **translating spoken words into sign language videos**, enabling more natural understanding.  
- 🧑‍🤝‍🧑 It promotes **inclusion** in everyday scenarios such as conversations, announcements, and meetings.  
- 🌍 Designed with accessibility in mind: large buttons, high contrast, and customizable modes.  

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

### Why It Matters for People with Visual Impairments  
- 👓 Helps users with **eye problems or low vision** identify objects in their surroundings.  
- 🗣️ Provides **audio feedback** so users can hear what’s in front of them without needing to see it.  
- 🧭 Improves independence by enabling safer navigation and better awareness of the environment.  
- 🖼️ Optional on-screen labels can support users with **partial vision**, combining text with speech.  


### Key Features  
- ⚡ Real-time detection (CameraX + ML Kit/TFLite)  
- 🔕 Debounced announcements (avoid spam)  
- 📊 Confidence threshold + label coalescing  
- 📦 Works **offline** using local models  

### Demo  
https://github.com/user-attachments/assets/88cdca3c-0b67-4257-b9bf-bb1751122672





