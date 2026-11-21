# Mobile Studio Android (Smartphone & Tablet Pro Audio Workstation)

> **Powered by Waldemar Krucinski (`vir2osesound`)**  
> Offizielles natives Android / Android Studio Projekt für Mobile Studio & Beatcloud Sequencer.

---

## 🎧 Über das Projekt

Dieses Repository stellt die native Android-App (Kotlin / AndroidX / Hardware Accelerated WebView) für **Mobile Studio** bereit:
- **Echtzeit-Synchronisierung:** Lädt die Audio-Engine von `https://mobile-studio.waldemar-krucinski.com/`. Jede Änderung am Web-Code auf GitHub landet sofort in der Android-App.
- **Low-Latency Pro Audio:** Hardware-beschleunigtes Web Audio mit direkter Freigabe für Audio-Puffer ohne Einschränkung.
- **Hardware Haptic Feedback:** Taktiles Feedback über die Android Vibrator API beim Triggern von Pads und Bewegen von Mixern.
- **Vollständige Kante-zu-Kante Immersion:** Automatische Ausblendung der Android-Navigations- und Statusleiste im Smartphone-Querformat für ein kompromissloses 10-Spur-Erlebnis.

---

## 📦 Store-Binaries & Builds

Über GitHub Actions wird bei jedem Push automatisch gebaut:
1. **`.aab` (Android App Bundle):** `MobileStudio_PlayStore_Release.aab` – die offizielle Datei zum Hochladen in die **Google Play Console**.
2. **`.apk` (Universal APK):** `MobileStudio_Direct_Install.apk` – die fertige, signierte Installationsdatei zum direkten Übertragen per USB, WhatsApp, Drive oder Download auf jedes Android-Gerät.
3. **`release.keystore` (Release Signaturschlüssel):** Der kryptografische Signaturschlüssel:
   * **Key Alias:** `mobilestudio`
   * **Key Password:** `vir2osesound`
   * **Store Password:** `vir2osesound`

👉 **Direkter Download der fertigen Pakete:**  
[GitHub Actions Runs – Mobile Studio Android](https://github.com/vir2ose-code/mobile-studio-android/actions/runs/44846862630)

---

## 🛠️ In Android Studio öffnen

1. **Repository klonen:**
   ```bash
   git clone https://github.com/vir2ose-code/mobile-studio-android.git
   ```
2. **In Android Studio öffnen:**
   * Wähle *Open* und wähle den Ordner `mobile-studio-android`.
   * Gradle synchronisiert das Projekt automatisch.
3. **Starten:**
   * Wähle dein angeschlossenes Android-Gerät oder einen Emulator (z. B. Pixel 8 Pro).
   * Drücke auf **Run (`Shift + F10`)**.

---

© 2026 Waldemar Krucinski (vir2osesound). Alle Rechte vorbehalten.
