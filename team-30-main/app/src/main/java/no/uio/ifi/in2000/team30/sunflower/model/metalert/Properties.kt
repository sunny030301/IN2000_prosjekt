package no.uio.ifi.in2000.team30.sunflower.model.metalert

/**
 * data class, representing the weather alerts properties containing
 * different information about the weather alert (area, awareness_level etc.).
 * current json hierarchy: "features" -> "properties" -> here
 */
data class Properties(
    val MunicipalityId: String = "",
    val administrativeId: String = "",
    val altitude_above_sea_level: Int = 0,
    val area: String = "",                   // example: "Kyst- og fjordstrøk av Finnmark" //trenger
    val awarenessResponse: String = "",      // example: "Følg med" //trenger
    val awarenessSeriousness: String = "",   // example: "Utfordrende situasjon" //trenger
    val awareness_level: String = "",        // example: "2; yellow; Moderate" //trenger?
    val awareness_type: String = "",         // example: "7; coastalevent" //kanskje
    val ceiling_above_sea_level: Int = 0,
    val certainty: String = "",
    val consequences: String = "",
    val contact: String = "",
    val county: List<String> = emptyList(),           // example: ["56"]
    val description: String = "",            // example: "Alert: Lokalt moderat ising på skip i utsatte kyst- og fjordstrøk.  Faren avtar torsdag formiddag." //trenger
    val event: String = "",
    val eventAwarenessName: String = "",
    val eventEndingTime: String = "",
    val geographicDomain: String = "",
    val id: String = "",
    val incidentName: String = "",
    val instruction: String = "",
    val municipality: List<String> = emptyList(),
    val resources: List<Resource> = emptyList(),
    val riskMatrixColor: String = "",        // example: "Yellow"
    val status: String = "",                 // example: "Actual" // example 2: "Test"
    val severity: String = "",               // example: "Moderate
    val title: String = "",                  // example: "Moderat ising på skip, gult nivå, Kyst- og fjordstrøk av Finnmark, 2024-03-12T12:00:00+00:00, 2024-03-14T09:00:00+00:00" //severity
    val triggerLevel: String = "",           // example: "Alert"
    val type: String = "",
    val web: String = ""
)