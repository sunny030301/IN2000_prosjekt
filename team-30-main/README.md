# 🌻 Sunflower - IN2000, våren 2024, team-30
## Appens struktur, dokumentasjon, etc. 

Rapportdokumentasjon er tilgjengelig [her](https://drive.google.com/file/d/1rfuxM6H7Ohm4ox6lqL9vbKitRRL4pouL/view?usp=sharing)

`ARCHITECTURE.md` er tilgjengelig kildekodens prosjektet rot ([link](https://github.uio.no/IN2000-V24/team-30/blob/main/ARCHITECTURE.md))

`MODELING.md` er tilgjengelig kildekodens prosjektet rot ([link](https://github.uio.no/IN2000-V24/team-30/blob/main/MODELING.md))


## Hvordan kjøre appen

Appen kjøres i Android Studio, gjennom en virtuell Android enhet eller ved å koble til en Android-telefon via USB/Wi-Fi.

1. Last ned [Android Studio](https://developer.android.com/studio).
2. Last ned kildekoden som en ZIP-fil her på GitHub. (Code → Download ZIP)
3. Pakk ut ZIP-filen og åpne mappen som prosjekt i Android Studio.
4. Velg metode for å kjøre app.
   1. **Metode 1 (anbefales): Kjør appen gjennom en virtuell Android-enhet innebygd i Android Studio**
        - Dersom det det ikke er opprettet en virtuell enhet fra før kan du følge [disse instruksene](https://developer.android.com/studio/run/managing-avds#createavd) for å opprette en virtuell enhet i Android Studio 
        - Viktig: Velg en _System image_ med API-nivå på minst `24`, men vi anbefaler `34`!
   2. **Metode 2: Koble til en Android-telefon via USB eller Wi-Fi.**
        - Følg [disse instruksene](https://developer.android.com/studio/run/device#setting-up) for å koble til en Android-telefon via USB eller Wi-Fi.
5. Kjør appen!

## Valgt case
Vi valgte [case 3: vær- og farevarsel for yngre brukere](https://in2000.met.no/2024/3-yngre.html). Årsaken til at vi valgte denne casen er fordi vi alle har hatt/ har et forhold til vær- og farevarsel og bruker slike applikasjoner ofte. Dermed går vi ikke blinde når det kommer til implementasjonen av applikasjonen. I tillegg blir det også enklere å kunne sette funksjonelle krav fra start.

## Biblioteker
- [LocationForecast](https://api.met.no/weatherapi/locationforecast/2.0/documentation) (gjennom IFI-proxy)
- [MetAlert](https://api.met.no/weatherapi/metalerts/2.0/documentation) (gjennom IFI-proxy)
- [Google Maps Places API](https://developers.google.com/maps/documentation/places/web-service/overview)
- [Google Maps TimeZone API](https://developers.google.com/maps/documentation/timezone/overview)
- [metno/weathericons](https://github.com/metno/weathericons/tree/main/weather)
- [nrkno/yr-warning-icons](https://nrkno.github.io/yr-warning-icons/)
