# Sushi Tracker - Contexto Tecnico

Este documento contiene toda la informacion tecnica, arquitectura, flujos de navegacion, modelos de datos y detalles de implementacion necesarios para desarrollar y mantener la aplicacion.

---

## Indice

1. [Arquitectura General](#arquitectura-general)
2. [Navegacion y Flujos](#navegacion-y-flujos)
3. [Modelos de Datos](#modelos-de-datos)
4. [Sistema de Persistencia](#sistema-de-persistencia)
5. [Componentes UI](#componentes-ui)
6. [Interacciones y Gestos](#interacciones-y-gestos)
7. [Sistema de Temas](#sistema-de-temas)
8. [Logica de Negocio](#logica-de-negocio)
9. [Estructura de Archivos](#estructura-de-archivos)
10. [Dependencias](#dependencias)
11. [Consideraciones de UX](#consideraciones-de-ux)
12. [Posibles Mejoras Futuras](#posibles-mejoras-futuras)

---

## Arquitectura General

La aplicacion sigue una arquitectura simple basada en:

- **UI Layer**: Jetpack Compose con pantallas (`Screen`) y componentes reutilizables
- **Navigation**: Jetpack Navigation Compose con rutas definidas en `sealed class Screen`
- **Data Layer**: Modelos de datos simples (`data class`) y persistencia con SharedPreferences
- **State Management**: `remember`, `mutableStateOf` y `rememberSaveable` para estado local

### Diagrama de Capas

```
┌─────────────────────────────────────────┐
│               UI Layer                   │
│  ┌─────────────────────────────────────┐│
│  │           Screens                   ││
│  │  Splash, Home, Counter, History,   ││
│  │  SessionDetail, Stats              ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │         Components                  ││
│  │    PieceCounterItem, TabRow        ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │           Theme                     ││
│  │    Color, Type, Theme              ││
│  └─────────────────────────────────────┘│
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│           Navigation Layer              │
│              NavGraph                   │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│             Data Layer                  │
│  ┌─────────────────────────────────────┐│
│  │           Models                    ││
│  │   SushiPiece, SessionRecord        ││
│  └─────────────────────────────────────┘│
│  ┌─────────────────────────────────────┐│
│  │          Storage                    ││
│  │       SessionStorage               ││
│  └─────────────────────────────────────┘│
└─────────────────────────────────────────┘
```

---

## Navegacion y Flujos

### Rutas Definidas

```kotlin
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Counter : Screen("counter")
    data object History : Screen("history")
    data object Stats : Screen("stats")
}
```

### Flujo Principal

```
                    ┌──────────┐
                    │  Splash  │
                    └────┬─────┘
                         │ (2.5s auto)
                         ▼
                    ┌──────────┐
              ┌─────│   Home   │─────┐
              │     └────┬─────┘     │
              │          │           │
              ▼          ▼           ▼
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │ Counter  │ │ History  │ │  Stats   │
        └──────────┘ └────┬─────┘ └──────────┘
                          │
                          ▼
                   ┌────────────┐
                   │  Detail    │
                   │ (en dialog)│
                   └────────────┘
```

### Flujo del Counter Screen (3 Fases)

```
┌─────────────────────────────────────────────────────┐
│                    FASE 1                           │
│              Nombre Restaurante                     │
│                                                     │
│  ┌─────────────────────────────────────────────┐   │
│  │  TextField: "Restaurante Sakura"            │   │
│  └─────────────────────────────────────────────┘   │
│                                                     │
│            [ Comenzar ]                            │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│                    FASE 2                           │
│             Contador de Piezas                      │
│                                                     │
│  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐                  │
│  │Nigiri│ │Sashi│ │Maki │ │Urama│  ... (12 items)  │
│  │  5   │ │  3  │ │  8  │ │  2  │                  │
│  └─────┘ └─────┘ └─────┘ └─────┘                  │
│                                                     │
│           Total: 18 piezas                         │
│                                                     │
│            [ Terminar Sesion ]                     │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│                    FASE 3                           │
│               Confirmacion                          │
│                                                     │
│  Restaurante: Sakura                               │
│  Total: 18 piezas                                  │
│                                                     │
│  - Nigiri: 5                                       │
│  - Sashimi: 3                                      │
│  - Maki: 8                                         │
│  - Uramaki: 2                                      │
│                                                     │
│    [ Cancelar ]      [ Guardar ]                   │
└─────────────────────────────────────────────────────┘
```

---

## Modelos de Datos

### SushiPiece

Representa un tipo de pieza de sushi disponible para contar.

```kotlin
data class SushiPiece(
    val id: String,       // Identificador unico: "nigiri", "sashimi", etc.
    val name: String,     // Nombre para mostrar: "Nigiri", "Sashimi", etc.
    val imageRes: Int     // Recurso drawable: R.drawable.nigiri
)
```

**Lista predefinida**: 12 tipos de piezas en `SUSHI_PIECES`.

### SessionRecord

Representa una sesion de conteo guardada.

```kotlin
data class SessionRecord(
    val id: String,                    // UUID unico
    val date: String,                  // ISO 8601: "2024-03-15T14:30:00"
    val restaurantName: String,        // Nombre del restaurante
    val pieces: Map<String, Int>,      // {"nigiri": 5, "maki": 8, ...}
    val totalPieces: Int               // Suma total de todas las piezas
)
```

### StatsFilter

Enum para filtrar estadisticas por periodo.

```kotlin
enum class StatsFilter {
    ALL,    // Todos los tiempos
    YEAR,   // Este ano
    MONTH,  // Este mes
    WEEK    // Esta semana
}
```

### StatsResult

Resultado del calculo de estadisticas.

```kotlin
data class StatsResult(
    val pieceStats: Map<String, Int>,  // Total por tipo de pieza
    val total: Int                      // Gran total
)
```

---

## Sistema de Persistencia

### SessionStorage

Clase encargada de gestionar el almacenamiento de sesiones usando SharedPreferences.

**Archivo**: `data/SessionStorage.kt`

**Metodos principales**:

| Metodo | Descripcion |
|--------|-------------|
| `getSessions()` | Obtiene todas las sesiones guardadas |
| `saveSession(session)` | Guarda una nueva sesion (al inicio de la lista) |
| `getSessionById(id)` | Busca una sesion por su ID |
| `getRanking(limit)` | Obtiene las N sesiones con mas piezas |
| `getStats(filter)` | Calcula estadisticas por periodo |

**Formato de almacenamiento**: JSON serializado con Gson.

**Clave SharedPreferences**: `"sushi_tracker_sessions"` / `"sessions"`

### Ejemplo de datos almacenados

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "date": "2024-03-15T14:30:00",
    "restaurantName": "Restaurante Sakura",
    "pieces": {
      "nigiri": 12,
      "sashimi": 8,
      "maki": 15
    },
    "totalPieces": 35
  }
]
```

---

## Componentes UI

### PieceCounterItem

Componente que muestra un tipo de pieza con su contador y gestiona las interacciones.

**Props**:
- `piece: SushiPiece` - Datos de la pieza
- `count: Int` - Cantidad actual
- `onIncrement: () -> Unit` - Callback al hacer tap
- `onDecrement: () -> Unit` - Callback al completar long-press

**Comportamiento**:
- **Tap**: Incrementa el contador
- **Long-press 5 segundos**: Muestra anillo de progreso circular y decrementa al completar
- **Feedback visual**: Anillo verde que se llena durante el long-press

**Estados internos**:
- `isLongPressing: Boolean` - Si esta en proceso de long-press
- `progress: Float` - Progreso del anillo (0f a 1f)

### TabRow Personalizado (Historial/Ranking, Stats)

Tabs con estilo pill (bordes redondeados completos).

**Estilos**:
- Tab activo: `Background = Primary (#4ECDC4)`, `Text = PrimaryForeground`
- Tab inactivo: `Background = Transparent`, `Text = MutedForeground`

---

## Interacciones y Gestos

### Tap para Sumar

```kotlin
Modifier.pointerInput(Unit) {
    detectTapGestures(
        onTap = { onIncrement() }
    )
}
```

### Long-Press para Restar (5 segundos)

```kotlin
Modifier.pointerInput(Unit) {
    detectTapGestures(
        onLongPress = {
            // Inicia contador de 5 segundos
            // Muestra anillo de progreso
            // Al completar: onDecrement()
        }
    )
}
```

**Implementacion con Coroutines**:
- `LaunchedEffect` que actualiza `progress` cada frame
- Duracion total: 5000ms
- Si se suelta antes: cancela y resetea

---

## Sistema de Temas

### Color.kt

```kotlin
val Background = Color(0xFF1B2838)      // Navy oscuro
val Foreground = Color(0xFFFFFFFF)      // Blanco
val Card = Color(0xFF2A3A4A)            // Navy medio
val Primary = Color(0xFF4ECDC4)         // Verde menta
val PrimaryForeground = Color(0xFF1B2838)
val Secondary = Color(0xFF3D4D5C)       // Gris azulado
val MutedForeground = Color(0xFF94A3B3) // Gris claro
val Border = Color(0xFF394959)
val ItemBg = Color(0xFFFFFFFF)          // Blanco (items sushi)
val ItemFg = Color(0xFF1A1A1A)          // Negro (texto items)
val Gold = Color(0xFFF5B800)            // Medalla oro
val Silver = Color(0xFFBFBFBF)          // Medalla plata
val Bronze = Color(0xFFC87533)          // Medalla bronce
```

### Type.kt

```kotlin
val SushiTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        letterSpacing = (-0.02).em
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)
```

---

## Logica de Negocio

### Calculo de Estadisticas

```kotlin
fun getStats(filter: StatsFilter): StatsResult {
    val sessions = getSessions()
    val now = LocalDate.now()

    val startDate = when (filter) {
        StatsFilter.ALL -> LocalDate.MIN
        StatsFilter.YEAR -> now.withDayOfYear(1)
        StatsFilter.MONTH -> now.withDayOfMonth(1)
        StatsFilter.WEEK -> now.with(previousOrSame(DayOfWeek.MONDAY))
    }

    val filtered = sessions.filter { session ->
        val sessionDate = LocalDateTime.parse(session.date).toLocalDate()
        !sessionDate.isBefore(startDate)
    }

    // Agrupa y suma por tipo de pieza
    val pieceStats = mutableMapOf<String, Int>()
    var total = 0
    for (session in filtered) {
        for ((pieceId, count) in session.pieces) {
            pieceStats[pieceId] = (pieceStats[pieceId] ?: 0) + count
            total += count
        }
    }

    return StatsResult(pieceStats, total)
}
```

### Generacion de Ranking

```kotlin
fun getRanking(limit: Int = 10): List<SessionRecord> {
    return getSessions()
        .sortedByDescending { it.totalPieces }
        .take(limit)
}
```

### Medallas del Ranking

| Posicion | Color | Icono |
|----------|-------|-------|
| 1 | Gold (#F5B800) | Corona o "1" |
| 2 | Silver (#BFBFBF) | "2" |
| 3 | Bronze (#C87533) | "3" |
| 4-10 | Card (#2A3A4A) | Numero |

---

## Estructura de Archivos

```
app/src/main/java/com/tuapp/sushitracker/
│
├── MainActivity.kt                    # Entry point
│
├── data/
│   ├── SushiPiece.kt                 # Modelo + lista SUSHI_PIECES
│   ├── SessionRecord.kt              # Modelo de sesion guardada
│   └── SessionStorage.kt             # Persistencia SharedPreferences
│
└── ui/
    ├── theme/
    │   ├── Color.kt                  # Paleta de colores
    │   ├── Type.kt                   # Tipografia
    │   └── Theme.kt                  # MaterialTheme wrapper
    │
    ├── screens/
    │   ├── SplashScreen.kt           # Splash animado
    │   ├── HomeScreen.kt             # Menu principal
    │   ├── CounterScreen.kt          # Contador (3 fases)
    │   ├── HistoryScreen.kt          # Historial + Ranking tabs
    │   ├── SessionDetailScreen.kt    # Detalle de sesion
    │   └── StatsScreen.kt            # Estadisticas con filtros
    │
    ├── components/
    │   └── PieceCounterItem.kt       # Item de pieza con long-press
    │
    └── navigation/
        └── NavGraph.kt               # Definicion de rutas
```

---

## Dependencias

```kotlin
// build.gradle (app)
dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // Compose
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.animation:animation")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
```

---

## Consideraciones de UX

### Feedback Tactil
- Usar `Modifier.clickable` con `indication` para ripple effect
- Vibracion al completar long-press (opcional)

### Accesibilidad
- `contentDescription` en todas las imagenes
- Tamanos de toque minimo 48dp
- Contraste de colores WCAG AA

### Animaciones
- Splash: Fade in + flotacion suave del logo
- Transiciones entre pantallas: Slide horizontal
- Long-press: Anillo de progreso circular animado
- Numeros del contador: Animacion de escala al cambiar

### Estados Vacios
- Historial vacio: Mensaje "No hay sesiones guardadas"
- Ranking vacio: Mensaje "Completa tu primera sesion"
- Estadisticas vacias: Mostrar 0 en todos los valores

---

## Posibles Mejoras Futuras

1. **Base de datos Room**: Migrar de SharedPreferences a Room para mejor rendimiento con muchos datos
2. **Backup en la nube**: Sincronizacion con Firebase/Supabase
3. **Graficos**: Charts de consumo mensual/semanal
4. **Tipos personalizables**: Permitir agregar/editar tipos de piezas
5. **Exportar datos**: CSV o PDF con historial
6. **Widgets**: Widget de Android con acceso rapido
7. **Notificaciones**: Recordatorio semanal de estadisticas
8. **Modo oscuro/claro**: Toggle de tema (actualmente solo dark)
9. **Localizacion**: Soporte multi-idioma
10. **Tests**: Unit tests y UI tests con Compose Testing

---

## Notas de Implementacion

### Long-Press con Progreso

El componente `PieceCounterItem` implementa un patron de long-press con feedback visual:

1. Al detectar `onLongPress`, inicia un `LaunchedEffect`
2. Loop que actualiza `progress` de 0f a 1f en 5000ms
3. `Canvas` dibuja arco circular proporcional a `progress`
4. Si `progress >= 1f`, ejecuta `onDecrement()` y resetea
5. Si se suelta antes, `DisposableEffect` cancela y resetea

### Formato de Fechas

- **Almacenamiento**: ISO 8601 (`LocalDateTime.now().toString()`)
- **Display**: `dd/MM/yyyy` para fechas, `dd/MM/yyyy HH:mm` si se necesita hora

### IDs de Sesion

Generados con `UUID.randomUUID().toString()` para unicidad garantizada.

---

*Documento generado para el proyecto Sushi Tracker - Android Native App*
