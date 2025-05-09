# 📱 LekNaCzas - Mobilny Asystent Przyjmowania Leków

![LekNaCzas Logo](https://img.shields.io/badge/LekNaCzas-v1.0-blue)

## 📋 Spis treści

- 📖 O projekcie
- ✨ Funkcje
- 📱 Zrzuty ekranu
- 🛠️ Technologie
- 📂 Struktura projektu
- ⚙️ Instalacja
- 🚀 Uruchomienie
- 👥 Autorzy
- 📄 Licencja

## 📖 O projekcie

**LekNaCzas** to nowoczesna aplikacja mobilna zaprojektowana, aby pomóc użytkownikom w systematycznym przyjmowaniu leków. Aplikacja umożliwia łatwe zarządzanie listą leków, monitorowanie harmonogramu dawkowania oraz śledzenie historii przyjmowania. Dzięki przypomnieniom i statystykom, użytkownicy mogą poprawić regularność terapii i zwiększyć jej skuteczność.

Projekt został stworzony z myślą o prostocie użytkowania i efektywności, aby zapewnić najlepsze wsparcie w codziennej rutynie przyjmowania leków.

## ✨ Funkcje

- 🔐 **Bezpieczne konto użytkownika**
  - Rejestracja i logowanie z wykorzystaniem Firebase Authentication
  - Ochrona danych medycznych użytkownika

- 📝 **Zarządzanie lekami**
  - Dodawanie nowych leków z określeniem nazwy, dawki, częstotliwości i jednostki
  - Usuwanie niepotrzebnych leków z listy
  - Oznaczanie statusu przyjęcia leku (wzięty/niewzięty)

- 📅 **Kalendarz przyjmowania leków**
  - Przejrzysty widok kalendarza z historią przyjmowania leków
  - Kod kolorystyczny do oznaczania statusów (wzięty, pominięty, zaplanowany)
  - Możliwość edycji statusu leku dla dat z przeszłości

- 📊 **Statystyki i motywacja**
  - Śledzenie aktualnej serii regularnego przyjmowania leków
  - Wyświetlanie najdłuższej serii w historii
  - Wskaźnik skuteczności przyjmowania leków w ostatnim tygodniu
  - Motywacyjne wiadomości dostosowane do osiągnięć użytkownika

- 🔔 **System powiadomień**
  - Automatyczne przypomnienia o konieczności wzięcia leku
  - Powiadomienia wysyłane dwa razy dziennie (10:00 i 20:00)
  - Możliwość oznaczenia leku jako przyjętego bezpośrednio z powiadomienia

## 🛠️ Technologie

- **Język programowania**
  - Kotlin 1.9.22

- **Frameworki i biblioteki**
  - Android Jetpack Compose (UI)
  - Firebase Authentication (uwierzytelnianie)
  - Firebase Firestore (baza danych)
  - WorkManager (planowanie powiadomień)
  - Navigation Compose (nawigacja)
  - ViewModel i LiveData (architektura MVVM)

- **Narzędzia**
  - Android Studio
  - Gradle
  - Git

## 📂 Struktura projektu

Projekt używa architektury MVVM (Model-View-ViewModel) dla czystej separacji logiki biznesowej od interfejsu użytkownika:

```
app/
├── src/main/
│   ├── java/com/example/leknaczas/
│   │   ├── model/           # Modele danych
│   │   ├── repository/      # Warstwa dostępu do danych
│   │   ├── viewmodel/       # ViewModele zarządzające stanem UI
│   │   ├── ui/
│   │   │   ├── components/  # Komponenty wielokrotnego użytku
│   │   │   ├── screens/     # Ekrany aplikacji
│   │   │   └── theme/       # Motywy i style
│   │   ├── navigation/      # System nawigacji
│   │   └── notification/    # System powiadomień
│   └── res/                 # Zasoby (teksty, ikony, style)
└── build.gradle.kts         # Konfiguracja budowania
```

## ⚙️ Instalacja

1. **Wymagania wstępne**
   - Android Studio (najnowsza wersja)
   - JDK 11 lub nowszy
   - Połączenie internetowe (do pobierania zależności)

2. **Klonowanie repozytorium**
   ```bash
   git clone https://github.com/kolynski/leknaczas.git
   cd leknaczas
   ```

3. **Konfiguracja Firebase**
   - Utwórz projekt w [Firebase Console](https://console.firebase.google.com/)
   - Dodaj aplikację Android z pakietem `com.example.leknaczas`
   - Pobierz plik google-services.json i umieść go w katalogu app
   - Włącz Authentication (Email/Password) i Firestore w konsoli Firebase

4. **Budowanie projektu**
   - Otwórz projekt w Android Studio
   - Synchronizuj projekt z plikami Gradle
   - Zbuduj projekt używając opcji "Build > Make Project"

## 🚀 Uruchomienie

1. **Uruchomienie na emulatorze**
   - Skonfiguruj emulator z Android API 24 lub nowszym
   - Kliknij "Run" w Android Studio, wybierając utworzony emulator

2. **Uruchomienie na urządzeniu fizycznym**
   - Podłącz urządzenie przez USB i włącz debugowanie USB
   - Kliknij "Run" w Android Studio, wybierając podłączone urządzenie

3. **Testowanie**
   - Utwórz konto użytkownika lub zaloguj się
   - Dodaj kilka leków do swojej listy
   - Wypróbuj oznaczanie leków jako przyjęte/nieprzyjęte
   - Sprawdź kalendarz i statystyki, aby zobaczyć swoje postępy

## 👥 Autorzy

Projekt został stworzony przez:

- [**@kolynski**](https://github.com/kolynski)
- [**@LiCHUTKO**](https://github.com/LiCHUTKO) 

⭐ Jeśli podoba Ci się ten projekt, zostaw gwiazdkę na GitHub! ⭐

📧 W razie pytań, sugestii lub problemów skontaktuj się z autorami lub utwórz nowy Issue w repozytorium.