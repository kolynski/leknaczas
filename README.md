# 💊 LekNaCzas - Twój Mobilny Asystent Przyjmowania Leków

![LekNaCzas](https://img.shields.io/badge/LekNaCzas-v1.0-blue?style=for-the-badge)
![Android](https://img.shields.io/badge/Android-API%2024+-green?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple?style=for-the-badge&logo=kotlin)
![Firebase](https://img.shields.io/badge/Firebase-Enabled-orange?style=for-the-badge&logo=firebase)

## 📋 Spis treści

- 📖 [O projekcie](#-o-projekcie)
- ✨ [Funkcje](#-funkcje)
- 🎯 [Główne cechy](#-główne-cechy)
- 🛠️ [Technologie](#️-technologie)
- 📂 [Struktura projektu](#-struktura-projektu)
- ⚙️ [Instalacja](#️-instalacja)
- 🚀 [Uruchomienie](#-uruchomienie)
- 📖 [Jak używać](#-jak-używać)
- 👥 [Autorzy](#-autorzy)
- 📄 [Licencja](#-licencja)

## 📖 O projekcie

**LekNaCzas** to zaawansowana aplikacja mobilna na Android, stworzona z myślą o osobach, które potrzebują systematycznego wsparcia w przyjmowaniu leków. Aplikacja łączy w sobie nowoczesny design Material Design 3 z praktycznymi funkcjami zarządzania terapią lekową.

🎯 **Nasza misja**: Pomóc użytkownikom w regularnym przyjmowaniu leków poprzez intuicyjny interfejs, inteligentne przypomnienia i motywujące statystyki.

🏆 **Dlaczego LekNaCzas?**
- ✅ Kompleksowe zarządzanie terapią lekową
- ✅ Automatyczne przypomnienia dostosowane do trybu życia
- ✅ Wizualny kalendarz z historią przyjmowania
- ✅ Motywujące statystyki i system osiągnięć
- ✅ Bezpieczne przechowywanie danych w chmurze

## ✨ Funkcje

### 🔐 **Bezpieczne zarządzanie kontem**
- 📧 Rejestracja i logowanie przy użyciu Firebase Authentication
- 🔒 Szyfrowane przechowywanie danych medycznych w Firebase Firestore
- 👤 Personalizowane doświadczenie dla każdego użytkownika
- 🔄 Synchronizacja danych między urządzeniami

### 💊 **Inteligentne zarządzanie lekami**
- ➕ **Dodawanie leków** z pełną specyfikacją:
  - 🏷️ Nazwa leku
  - 💉 Dawka (ilość i jednostka)
  - ⏰ Częstotliwość przyjmowania (1x, 2x, 3x dziennie, co drugi dzień, raz w tygodniu)
- 🗑️ **Usuwanie niepotrzebnych leków** z listy
- ✅ **Oznaczanie statusu** - wzięty/niewzięty dla każdego dnia
- 📊 **Zarządzanie zapasami** - śledzenie ilości pozostałych tabletek/dawek

### 📅 **Zaawansowany kalendarz medyczny**
- 🗓️ **Wizualny kalendarz miesięczny** z kolorowym oznaczeniem statusów
- 🎨 **System kolorów**:
  - 🟢 **Zielony** - lek wzięty
  - 🔴 **Czerwony** - lek pominięty  
  - 🔵 **Niebieski** - lek zaplanowany
- 📝 **Edycja historii** - możliwość zmiany statusu dla dat z przeszłości
- 📱 **Responsywny design** dostosowany do różnych rozmiarów ekranów
- 🔍 **Szczegółowy widok dnia** z listą wszystkich zaplanowanych leków

### 📊 **Statystyki i motywacja**
- 🔥 **Aktualna seria** - liczba dni pod rząd z regularnymi przyjęciami
- 🏆 **Najdłuższa seria** w historii użytkownika
- 📈 **Wskaźnik skuteczności** przyjmowania leków w ostatnim tygodniu (%)
- 💬 **Inteligentne komunikaty motywacyjne** dostosowane do osiągnięć
- 🎯 **System progresji** zachęcający do regularności

### 🔔 **Zaawansowany system powiadomień**
- ⏰ **Automatyczne przypomnienia** o stałych godzinach (10:00 i 20:00)
- 🎯 **Inteligentne filtrowanie** - powiadomienia tylko dla nie przyjętych leków
- 📱 **Interaktywne powiadomienia** z możliwością oznaczenia leku jako przyjętego
- 🔕 **Automatyczne anulowanie** powiadomień po oznaczeniu leku
- 🛠️ **WorkManager** zapewniający niezawodność działania w tle

### 🎨 **Nowoczesny interfejs użytkownika**
- 🎨 **Material Design 3** z nowoczesnymi komponentami
- 🌓 **Adaptacyjne kolory** dostosowane do systemu
- 📱 **Intuicyjna nawigacja** z zakładkami (Leki/Kalendarz/Statystyki)
- 💫 **Płynne animacje** i przejścia między ekranami
- ♿ **Dostępność** zgodna ze standardami Android

## 🎯 Główne cechy

### 📱 **Architektura MVVM**
Aplikacja wykorzystuje nowoczesną architekturę Model-View-ViewModel zapewniającą:
- 🔄 Czyste rozdzielenie logiki biznesowej od interfejsu
- 🧪 Łatwość testowania i utrzymania kodu
- 🚀 Wydajne zarządzanie stanem aplikacji

### ☁️ **Integracja z Firebase**
- 🔐 **Firebase Authentication** - bezpieczne zarządzanie kontami
- 📊 **Firebase Firestore** - baza danych NoSQL w czasie rzeczywistym
- 🔄 **Automatyczna synchronizacja** danych między urządzeniami
- 🌐 **Offline support** - aplikacja działa bez połączenia internetowego

### 🎨 **Material Design 3**
- 🌈 **Dynamic Color** - kolory dostosowane do tapety systemowej
- 🌓 **Tryby jasny/ciemny** automatycznie dostosowane
- 📱 **Responsive layout** działający na różnych rozmiarach ekranów
- ♿ **Accessibility** zgodne ze standardami dostępności

## 🛠️ Technologie

### 🚀 **Główne technologie**
- ![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple?style=flat-square&logo=kotlin) **Język programowania**
- ![Android](https://img.shields.io/badge/Android-API%2024+-green?style=flat-square&logo=android) **Platforma mobilna** (min SDK 24, target SDK 34)
- ![Compose](https://img.shields.io/badge/Jetpack%20Compose-2023.10.01-blue?style=flat-square) **Nowoczesny UI toolkit**

### 🔧 **Biblioteki i frameworki**
- 🎨 **UI & UX**
  - `Jetpack Compose` - deklaratywny UI
  - `Material Design 3` - nowoczesny design system
  - `Compose Navigation 2.7.7` - nawigacja między ekranami
  - `Material Icons Extended` - bogaty zestaw ikon

- ☁️ **Backend & Baza danych** 
  - `Firebase Authentication` - zarządzanie użytkownikami
  - `Firebase Firestore` - baza danych NoSQL
  - `Firebase Analytics` - analityka użytkowania

- 🏗️ **Architektura & Zarządzanie stanem**
  - `ViewModel & LiveData` - architektura MVVM
  - `Lifecycle Runtime Compose 2.7.0` - zarządzanie cyklem życia
  - `Coroutines & Flow` - programowanie asynchroniczne

- 🔔 **System powiadomień**
  - `WorkManager 2.8.1` - niezawodne zadania w tle
  - `Notification API` - zaawansowane powiadomienia
  - `BroadcastReceiver` - obsługa akcji z powiadomień

### 🛠️ **Narzędzia deweloperskie**
- 🔨 **Android Studio** (najnowsza wersja)
- 📦 **Gradle 8.8.0** z Kotlin DSL
- 🔧 **Git** - kontrola wersji
- 🧪 **JUnit & Espresso** - testy jednostkowe i UI

## 📂 Struktura projektu

Projekt wykorzystuje czystą architekturę **MVVM (Model-View-ViewModel)** z separacją warstw:

```
📁 app/src/main/
├── 📁 java/com/example/leknaczas/
│   ├── 📱 LekNaCzasApp.kt              # Główna klasa aplikacji
│   ├── 📱 MainActivity.kt              # Główna aktywność
│   │
│   ├── 📁 model/                       # 🎯 Warstwa danych
│   │   └── 💊 Lek.kt                   # Model leku z logiką biznesową
│   │
│   ├── 📁 repository/                  # 🗄️ Warstwa dostępu do danych
│   │   ├── 🔐 AuthRepository.kt        # Zarządzanie uwierzytelnianiem
│   │   ├── 🔐 IAuthRepository.kt       # Interfejs auth repository  
│   │   ├── 💊 LekRepository.kt         # Zarządzanie lekami
│   │   └── 💊 ILekRepository.kt        # Interfejs lek repository
│   │
│   ├── 📁 viewmodel/                   # 🧠 Warstwa logiki biznesowej
│   │   ├── 🔐 AuthViewModel.kt         # ViewModel uwierzytelniania
│   │   └── 💊 LekViewModel.kt          # ViewModel zarządzania lekami
│   │
│   ├── 📁 ui/                          # 🎨 Warstwa prezentacji
│   │   ├── 📁 components/              # 🧩 Komponenty wielokrotnego użytku
│   │   │   ├── 💊 LekItem.kt           # Komponent elementu leku
│   │   │   ├── 📅 MedicineCalendar.kt  # Komponent kalendarza
│   │   │   └── 🏆 StreakCard.kt        # Komponent karty serii
│   │   │
│   │   ├── 📁 screens/                 # 📱 Główne ekrany aplikacji
│   │   │   ├── 🏠 HomeScreen.kt        # Główny ekran z zakładkami
│   │   │   ├── 🔑 LoginScreen.kt       # Ekran logowania
│   │   │   └── 📝 RegisterScreen.kt    # Ekran rejestracji
│   │   │
│   │   └── 📁 theme/                   # 🎨 Motywy i stylizacja
│   │       ├── 🎨 Color.kt             # Definicje kolorów
│   │       ├── 🎨 Theme.kt             # Główny motyw aplikacji
│   │       └── 📝 Type.kt              # Definicje typografii
│   │
│   ├── 📁 navigation/                  # 🧭 System nawigacji
│   │   └── 🧭 AppNavigation.kt         # Konfiguracja nawigacji
│   │
│   └── 📁 notification/                # 🔔 System powiadomień
│       ├── 📢 NotificationService.kt   # Serwis powiadomień
│       ├── ⚡ MedicationActionReceiver.kt # Obsługa akcji z powiadomień
│       ├── ⏰ MedicationReminderWorker.kt # Worker przypominający
│       └── 📅 MedicationScheduler.kt   # Planowanie przypomnień
│
└── 📁 res/                             # 📦 Zasoby aplikacji
    ├── 📁 drawable/                    # 🖼️ Ikony i grafiki
    ├── 📁 mipmap-*/                    # 📱 Ikony aplikacji (różne DPI)
    ├── 📁 values/                      # 🎨 Wartości i style
    │   ├── 🎨 colors.xml               # Kolory
    │   ├── 🔤 strings.xml              # Teksty aplikacji
    │   └── 🎨 themes.xml               # Motywy XML
    └── 📁 xml/                         # ⚙️ Konfiguracje XML
```

### 🏗️ **Kluczowe zasady architektury**

- **🔄 Jednokierunkowy przepływ danych** - od UI do Repository przez ViewModel
- **🧪 Dependency Injection** - łatwe testowanie i wymiana implementacji  
- **📱 Reactive Programming** - użycie Flow i StateFlow dla reaktywnego UI
- **🛡️ Error Handling** - centralne zarządzanie błędami w każdej warstwie
- **♻️ Lifecycle Awareness** - komponenty świadome cyklu życia Android

## ⚙️ Instalacja

### 🔧 **Wymagania systemowe**
- 💻 **Android Studio** Hedgehog (2023.1.1) lub nowszy
- ☕ **JDK 11** lub nowszy (zalecane JDK 17)
- 📱 **Android SDK** z API Level 24+ (Android 7.0)
- 🌐 **Połączenie internetowe** do pobierania zależności i konfiguracji Firebase
- 📦 **Minimum 4 GB RAM** dla komfortowego działania Android Studio

### 📥 **Instrukcja instalacji krok po kroku**

#### 1️⃣ **Klonowanie repozytorium**
```bash
# Klonowanie projektu
git clone https://github.com/your-username/leknaczas.git

# Przejście do katalogu projektu  
cd leknaczas
```

#### 2️⃣ **Konfiguracja Firebase** 🔥
> **⚠️ Ważne**: Ten krok jest niezbędny do działania aplikacji!

1. **Utwórz nowy projekt Firebase**:
   - Przejdź do [Firebase Console](https://console.firebase.google.com/)
   - Kliknij "Dodaj projekt" lub "Create a project"
   - Wprowadź nazwę projektu (np. "LekNaCzas")

2. **Skonfiguruj aplikację Android**:
   - W konsoli Firebase wybierz "Dodaj aplikację" → Android
   - **Package name**: `com.example.leknaczas`
   - **App nickname**: LekNaCzas (opcjonalne)
   - **Debug signing certificate**: zostaw puste (dla developmentu)

3. **Pobierz plik konfiguracyjny**:
   - Pobierz plik `google-services.json`
   - **Umieść go w**: `app/google-services.json` (główny katalog modułu app)

4. **Włącz wymagane usługi Firebase**:
   - **Authentication** → Sign-in method → Email/Password (włącz)
   - **Firestore Database** → Create database → Start in test mode
   
5. **Skonfiguruj reguły Firestore** (opcjonalne, dla bezpieczeństwa):
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

#### 3️⃣ **Przygotowanie środowiska Android Studio**
1. **Otwórz Android Studio**
2. **File** → **Open** → wybierz folder `leknaczas`
3. **Zaufaj projektowi** gdy zostaniesz o to poproszony
4. **Poczekaj na zakończenie synchronizacji** Gradle (może potrwać kilka minut)

#### 4️⃣ **Instalacja zależności**
```bash
# Synchronizuj projekt z plikami Gradle
./gradlew build

# Lub w Android Studio: 
# File → Sync Project with Gradle Files
```

### 🔍 **Weryfikacja instalacji**
- ✅ Brak błędów w **Build** → **Make Project**
- ✅ Plik `google-services.json` znajduje się w katalogu `app/`
- ✅ Firebase Authentication i Firestore są włączone
- ✅ Wszystkie zależności zostały pobrane

## 🚀 Uruchomienie

### 📱 **Opcja 1: Emulator Android**
1. **Utwórz AVD (Android Virtual Device)**:
   - **Tools** → **AVD Manager** → **Create Virtual Device**
   - Wybierz urządzenie (np. Pixel 6)
   - **System Image**: Android 14 (API 34) lub nowszy
   - Nadaj nazwę i kliknij **Finish**

2. **Uruchom aplikację**:
   - Kliknij ▶️ **Run** lub `Shift + F10`
   - Wybierz utworzony emulator
   - Poczekaj na uruchomienie aplikacji

### 📲 **Opcja 2: Urządzenie fizyczne**
1. **Włącz tryb deweloperski**:
   - **Ustawienia** → **Informacje o telefonie** → kliknij 7x **Numer kompilacji**
   
2. **Włącz debugowanie USB**:
   - **Ustawienia** → **Opcje deweloperskie** → **Debugowanie USB**
   
3. **Podłącz urządzenie**:
   - Połącz telefon USB-em z komputerem
   - Zaakceptuj prompt o debugowaniu USB
   
4. **Uruchom aplikację**:
   - Kliknij ▶️ **Run**
   - Wybierz swoje urządzenie z listy

### 🎯 **Pierwsze uruchomienie**
1. **Utwórz konto** - kliknij "Nie masz konta? Zarejestruj się"
2. **Wprowadź email i hasło** (min. 6 znaków)
3. **Zaloguj się** do aplikacji
4. **Dodaj pierwszy lek** w zakładce "Leki"
5. **Sprawdź kalendarz** i statystyki w odpowiednich zakładkach

### 🔧 **Rozwiązywanie problemów**
- **🚫 Firebase Error**: Sprawdź czy `google-services.json` jest w odpowiednim miejscu
- **📦 Build Error**: Wykonaj `Clean Project` i `Rebuild Project`  
- **🌐 Network Error**: Sprawdź połączenie internetowe
- **📱 Device Not Found**: Sprawdź czy debugowanie USB jest włączone

## 📖 Jak używać

### 🚀 **Pierwsze kroki**

#### 1️⃣ **Rejestracja i logowanie**
- 📝 **Nowe konto**: Kliknij "Nie masz konta? Zarejestruj się"
- 📧 **Email**: Wprowadź prawidłowy adres email
- 🔐 **Hasło**: Minimum 6 znaków (zalecane: duże/małe litery + cyfry)
- ✅ **Logowanie**: Po rejestracji automatyczne przekierowanie lub ręczne logowanie

#### 2️⃣ **Zarządzanie lekami** 💊

**Dodawanie nowego leku:**
1. Przejdź do zakładki **"Leki"** 
2. Kliknij przycisk ➕ **"Dodaj lek"**
3. Wypełnij formularz:
   - 🏷️ **Nazwa**: np. "Ibuprofen", "Witamina D"
   - 💉 **Ilość**: np. "200", "1000" 
   - 📏 **Jednostka**: mg, ml, sztuki, krople
   - ⏰ **Częstotliwość**: 
     - 1x dziennie
     - 2x dziennie  
     - 3x dziennie
     - Co drugi dzień
     - Raz w tygodniu
4. Kliknij **"Dodaj"**

**Zarządzanie istniejącymi lekami:**
- ✅ **Oznacz jako wzięty**: Kliknij ikonę ✓ przy leku
- 🗑️ **Usuń lek**: Przytrzymaj i wybierz "Usuń" lub użyj opcji menu

#### 3️⃣ **Kalendarz przyjmowania** 📅

**Nawigacja po kalendarzu:**
- 👆 **Przełączanie miesięcy**: Strzałki ◀️ ▶️ w górnej części
- 🎯 **Wybór dnia**: Kliknij na dowolną datę
- 📱 **Legenda kolorów**:
  - 🟢 **Zielony** = wszystkie leki wzięte
  - 🔴 **Czerwony** = niektóre leki pominięte
  - 🔵 **Niebieski** = leki zaplanowane (przyszłe dni)

**Edycja historii:**
1. Kliknij na wybraną datę (przeszłą lub obecną)
2. Zobacz listę leków na ten dzień
3. Zmień status każdego leku indywidualnie
4. Potwierdź zmiany

#### 4️⃣ **Statystyki i motywacja** 📊

**Aktywne statystyki:**
- 🔥 **Aktualna seria**: Ile dni pod rząd przyjmujesz wszystkie leki
- 🏆 **Najdłuższa seria**: Twój osobisty rekord
- 📈 **Skuteczność**: Procent poprawnie przyjętych leków w ostatnim tygodniu

**Motywacyjne komunikaty:**
- 🎯 Dynamiczne wiadomości w zależności od osiągnięć
- 💪 Zachęta do kontynuowania serii
- 🎉 Gratulacje za milowe kamienie

#### 5️⃣ **System powiadomień** 🔔

**Automatyczne przypomnienia:**
- ⏰ **Godziny**: 10:00 (rano) i 20:00 (wieczorem)
- 🎯 **Inteligentne filtrowanie**: Tylko dla nie przyjętych leków
- 📱 **Interaktywne**: Kliknij "Weź lek" bezpośrednio z powiadomienia

**Zarządzanie powiadomieniami:**
- 🔔 Automatyczne włączenie po dodaniu leku
- 🔕 Anulowanie po oznaczeniu leku jako przyjętego
- ⚙️ Systemowe ustawienia powiadomień w Android

### 💡 **Wskazówki i sztuczki**

#### 🎯 **Maksymalizacja skuteczności**
- 📅 **Regularne sprawdzanie kalendarza** - wizualna motywacja
- 🔔 **Nie ignoruj powiadomień** - kluczowe dla utrzymania nawyku  
- 📊 **Śledź statystyki** - daj się zmotywować postępami
- 🎯 **Ustal rutynę** - najlepiej o stałych porach dnia

#### 🛠️ **Rozwiązywanie problemów**
- 🔄 **Odświeżanie danych**: Użyj ikony odświeżania w prawym górnym rogu
- 📱 **Synchronizacja**: Aplikacja automatycznie synkuje dane z chmurą
- 🔔 **Brak powiadomień**: Sprawdź ustawienia systemu Android
- 🌐 **Problemy z połączeniem**: Aplikacja działa offline, dane zsynchronizują się później

#### 🎨 **Personalizacja**
- 🌓 **Motyw**: Automatycznie dostosowuje się do ustawień systemowych
- 🎨 **Kolory**: Dynamiczne kolory Material You (Android 12+)
- 📱 **Interfejs**: Dostosowuje się do rozmiaru ekranu i orientacji

### 🏥 **Najlepsze praktyki medyczne**

> **⚠️ Ważne**: LekNaCzas to narzędzie wspomagające, nie zastępuje konsultacji medycznej!

- 👨‍⚕️ **Konsultuj z lekarzem** wszystkie zmiany w terapii
- 📋 **Regularne przeglądy** listy leków z farmaceutą
- 🚨 **Skutki uboczne**: Natychmiast skontaktuj się z lekarzem
- 📅 **Stałe godziny**: Przyjmuj leki o tych samych porach każdego dnia
- 💊 **Dokończ kurację**: Nie przerywaj antybiotyków przedwcześnie

### 🔒 **Bezpieczeństwo danych**

- 🔐 **Szyfrowanie**: Wszystkie dane są szyfrowane w Firebase
- 👤 **Prywatność**: Dane dostępne tylko dla Twojego konta
- ☁️ **Kopia zapasowa**: Automatyczna synchronizacja z chmurą
- 🔄 **Multi-device**: Dostęp z różnych urządzeń po zalogowaniu

## 👥 Autorzy

Projekt został stworzony przez zespół pasjonatów zdrowia cyfrowego:

### 🚀 **Główni deweloperzy**
- 👨‍💻 [**@kolynski**](https://github.com/kolynski) - *Lead Developer & Architecture*
  - 🏗️ Architektura aplikacji i backend Firebase
  - 🎨 Implementacja UI/UX w Jetpack Compose
  - 🔔 System powiadomień i WorkManager
  
- 👨‍💻 [**@LiCHUTKO**](https://github.com/LiCHUTKO) - *Frontend Developer & Design*
  - 📱 Zaawansowane komponenty UI 
  - 📊 System statystyk i kalendarza
  - 🎯 Optymalizacja UX i dostępności

### 🎯 **Cele projektu**
Ten projekt powstał jako część nauki nowoczesnego rozwoju aplikacji mobilnych na Android, z wykorzystaniem najnowszych technologii:
- 📚 **Edukacyjny** - demonstracja best practices w Android development
- 💡 **Praktyczny** - rzeczywiste rozwiązanie problemu adherencji lekowej  
- 🌟 **Inspiracyjny** - pokazanie możliwości Jetpack Compose i Firebase

### 🤝 **Wkład społeczności**
Zapraszamy do współpracy! Projekt jest otwarty na:
- 🐛 **Zgłaszanie błędów** - [Issues](https://github.com/your-username/leknaczas/issues)
- 💡 **Propozycje funkcji** - [Feature Requests](https://github.com/your-username/leknaczas/issues/new)
- 🔧 **Pull Requests** - ulepszenia i nowe funkcje
- 📖 **Dokumentacja** - pomocy w tłumaczeniach i dokumentacji

### 📬 **Kontakt**
- 📧 **Email**: leknaczas.app@gmail.com
- 💬 **Discord**: [LekNaCzas Community](https://discord.gg/leknaczas)
- 🐦 **Twitter**: [@LekNaCzasApp](https://twitter.com/leknaczas)

---

## 🏆 Podziękowania

Specjalne podziękowania dla:

- 🎨 **Google Material Design Team** - za wspaniały system projektowania
- 🔥 **Firebase Team** - za potężne narzędzia backend-as-a-service
- 📱 **Android Jetpack Team** - za rewolucyjny Jetpack Compose
- 🎓 **Android Developer Community** - za nieustanne wsparcie i inspirację

## 📄 Licencja

```
MIT License

Copyright (c) 2024 LekNaCzas Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🌟 Wspieraj projekt

Jeśli **LekNaCzas** pomógł Ci w codziennym zarządzaniu lekami:

⭐ **Zostaw gwiazdkę na GitHub** - to motywuje nas do dalszego rozwoju!

🗣️ **Podziel się z innymi** - opowiedz znajomym o aplikacji

🐛 **Zgłoś błąd** - pomóż nam ulepszyć aplikację

💡 **Zaproponuj funkcję** - Twoje pomysły są cenne!

🔧 **Wyślij Pull Request** - bezpośredni wkład w rozwój

---

## 🚀 Roadmapa rozwoju

### 📅 **Planowane funkcje v2.0**
- 🏥 **Integracja z API lekarzy** - automatyczne pobieranie recept
- 📊 **Zaawansowane raporty** - eksport do PDF dla lekarza
- 🤖 **AI Assistant** - inteligentne przypomnienia na podstawie zachowań
- 📱 **Widget na ekran główny** - szybki dostęp do dzisiejszych leków
- 🌍 **Wielojęzyczność** - wsparcie dla innych języków
- ⌚ **Android Wear** - powiadomienia na smartwatch

### 🎯 **Długoterminowe cele**
- 🍎 **iOS Version** - aplikacja na iPhone'y
- 🌐 **Web Dashboard** - dostęp przez przeglądarkę
- 👨‍⚕️ **Portal dla lekarzy** - monitorowanie pacjentów
- 📈 **Analytics & Insights** - zaawansowane analizy zdrowotne

---

<div align="center">

### 💊 Dziękujemy, że dbasz o swoje zdrowie z **LekNaCzas**! 

![Made with ❤️](https://img.shields.io/badge/Made%20with-❤️-red?style=for-the-badge)
![Powered by Kotlin](https://img.shields.io/badge/Powered%20by-Kotlin-purple?style=for-the-badge&logo=kotlin)
![Built with Firebase](https://img.shields.io/badge/Built%20with-Firebase-orange?style=for-the-badge&logo=firebase)

**Twoje zdrowie jest najważniejsze. Nie zapomnij o dzisiejszym leku! 💊✨**

</div>