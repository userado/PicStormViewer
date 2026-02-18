# 📸 PicStormViewer  
*A lightweight, fast, no‑nonsense image viewer built for speed and simplicity.*

PicStormViewer is designed with one core philosophy:  
**switching between images should always be instant**, even on older hardware.

To achieve this, the viewer avoids RAM‑heavy caching and only applies optional transformations (zoom, rotation, fit‑to‑window) when the user explicitly requests them.

---

## 🖥️ Tested Environment

PicStormViewer was developed and tested using:

```
java 21.0.8 2025-07-15 LTS
Java(TM) SE Runtime Environment (build 21.0.8+12-LTS-250)
Java HotSpot(TM) 64-Bit Server VM (build 21.0.8+12-LTS-250, mixed mode, sharing)
```

It should run on any modern Java version, but Java 21+ is recommended.

---

## 🚀 Usage

Open a terminal in the program folder:

```bash
cd /path/to/PicStormViewer
java PicStormViewer.java
```

You will be prompted to select an image.  
Once opened, you can navigate and control the viewer entirely from the keyboard.

---

## 🎛️ Features & Controls

PicStormViewer keeps things fast by applying optional features **only when triggered**:

### 🔄 Navigation
- **Right Arrow** → Next image  
- **Left Arrow** → Previous image  

### 🖼️ View Controls
- **F** → Toggle *Fit to Window*  
  - *Note: This mode performs scaling and may slightly reduce performance.*
- **R** → Rotate right (90°)
- **L** → Rotate left (90°)
- **+ / -** → Zoom in / Zoom out
- **0** → Reset zoom

### 🌙 Theme
- **D** → Toggle Dark Mode / Light Mode

### 🖥️ Window
- **F11** → Toggle Fullscreen  
- **X** (window close button) → Quit

---

## ⚡ Why Choose PicStormViewer?

PicStormViewer is intentionally minimal:

- **No RAM‑heavy caching**  
  Images are loaded on demand to keep memory usage low.

- **Perfect for older machines**  
  Designed to run smoothly even on slow CPUs or limited RAM.

- **Clean, simple, fast**  
  No clutter, no unnecessary UI — just your images.

- **Optional features only when needed**  
  Nothing slows down image switching unless you explicitly enable it.

---

## 🎯 Project Philosophy

The goal is to keep the program **as fast as possible** while still offering useful tools like zoom, rotation, and fit‑to‑window — but only when the user wants them.

PicStormViewer is intentionally small and easy to understand, making it ideal for:

- learning Java Swing  
- experimenting with image processing  
- extending with your own features  
- running on older or low‑power systems  

---

## 🤝 Contributing

You are welcome to:

- modify the code  
- add new features  
- share your version  
- contribute improvements  

PicStormViewer is meant to be a simple, hackable tool — make it your own.
