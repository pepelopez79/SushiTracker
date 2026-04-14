# Sushi Tracker

Aplicacion Android nativa para contabilizar piezas de sushi y comida japonesa consumidas en restaurantes de buffet libre o sesiones de sushi.

---

## Descripcion

**Sushi Tracker** es una app disenada para los amantes del sushi que quieren llevar un registro de cuantas piezas consumen en cada visita a un restaurante japones. La app permite:

- Contar piezas por tipo con un simple tap (sumar) o long-press (restar)
- Guardar sesiones con fecha y nombre del restaurante
- Ver el historial completo de todas las visitas
- Analizar estadisticas por periodo (semana, mes, ano, total)

---

## Pantallas

### 1. Splash Screen
Pantalla de bienvenida con el logo de la app y animacion de entrada. Duracion: 2.5 segundos.

### 2. Home Screen (Pantalla Principal)
Menu principal con tres botones:
- **Comenzar Sesion**: Inicia una nueva sesion de conteo
- **Historial**: Accede al historial
- **Estadisticas**: Consulta estadisticas acumuladas

### 3. Counter Screen (Contador)
Flujo de 3 fases:
1. **Fase 1 - Restaurante**: Ingresar el nombre del restaurante
2. **Fase 2 - Conteo**: Cuadricula con 12 tipos de piezas. Tap para sumar, long-press 5 segundos para restar
3. **Fase 3 - Confirmacion**: Resumen y guardar la sesion

### 4. History Screen (Historial)
Dos tabs:
- **Historial**: Lista cronologica de todas las sesiones (fecha, restaurante, total piezas). Al pulsar una sesion se abre el detalle

### 5. Session Detail Screen (Detalle)
Desglose de una sesion mostrando cada tipo de pieza y la cantidad consumida.

### 6. Stats Screen (Estadisticas)
Filtros temporales (Todos, Ano, Mes, Semana) y lista de todos los tipos de piezas con el total consumido en ese periodo, mas el gran total.

---

## Tipos de Piezas Incluidas

| ID | Nombre | Descripcion |
|---|---|---|
| nigiri | Nigiri | Arroz prensado con pescado encima |
| sashimi | Sashimi | Loncha de pescado crudo |
| maki | Maki | Rollo con alga por fuera |
| uramaki | Uramaki | Rollo con arroz por fuera |
| gunkan | Gunkan | "Barco" de arroz envuelto con alga |
| temaki | Temaki | Cono de alga relleno |
| gyoza | Gyoza | Empanadilla japonesa |
| tempura | Tempura | Langostino rebozado |
| california | California Roll | Rollo con cangrejo y aguacate |
| dragon | Dragon Roll | Rollo con anguila y aguacate |
| edamame | Edamame | Vainas de soja |
| takoyaki | Takoyaki | Bolitas de pulpo |

---

## Tecnologias

- **Lenguaje**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navegacion**: Navigation Compose
- **Persistencia**: SharedPreferences + Gson
- **Minimo SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

---

## Requisitos Previos

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17
- Gradle 8.x

---

## Instalacion

### 1. Crear nuevo proyecto en Android Studio

```
File > New > New Project > Empty Activity (Compose)
```

Configuracion:
- Name: `SushiTracker`
- Package: `com.tuapp.sushitracker`
- Language: Kotlin
- Minimum SDK: API 26

### 2. Configurar build.gradle (app level)

Agregar las dependencias en `dependencies {}`:

```kotlin
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
implementation("androidx.activity:activity-compose:1.8.2")
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.ui:ui-tooling-preview")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.7.7")
implementation("com.google.code.gson:gson:2.10.1")
implementation("androidx.compose.animation:animation")
```

### 3. Copiar los archivos de codigo

Estructura de carpetas:

```
app/src/main/java/com/tuapp/sushitracker/
├── data/
│   ├── SushiPiece.kt
│   ├── SessionRecord.kt
│   └── SessionStorage.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Type.kt
│   │   └── Theme.kt
│   ├── screens/
│   │   ├── SplashScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── CounterScreen.kt
│   │   ├── HistoryScreen.kt
│   │   ├── SessionDetailScreen.kt
│   │   └── StatsScreen.kt
│   ├── components/
│   │   └── PieceCounterItem.kt
│   └── navigation/
│       └── NavGraph.kt
└── MainActivity.kt
```

### 4. Copiar imagenes de sushi

Copiar las 12 imagenes de `public/sushi/*.jpg` a:

```
app/src/main/res/drawable/
├── nigiri.jpg
├── sashimi.jpg
├── maki.jpg
├── uramaki.jpg
├── gunkan.jpg
├── temaki.jpg
├── gyoza.jpg
├── tempura.jpg
├── california.jpg
├── dragon.jpg
├── edamame.jpg
└── takoyaki.jpg
```

### 5. Sync y Build

```
Build > Rebuild Project
```

### 6. Ejecutar

Conectar un dispositivo o usar un emulador y ejecutar la app.

---

## Uso

### Contar piezas
1. Desde Home, pulsa "Comenzar Sesion"
2. Escribe el nombre del restaurante y pulsa "Comenzar"
3. Tap en una pieza para sumar (+1)
4. Long-press 5 segundos en una pieza para restar (-1). Aparece un anillo de progreso
5. Pulsa "Terminar Sesion" cuando hayas acabado
6. Confirma para guardar

### Ver historial
1. Desde Home, pulsa "Historial"
2. En la pestana "Historial" ves todas las sesiones
3. Pulsa una sesion para ver el desglose

### Ver estadisticas
1. Desde Home, pulsa "Estadisticas"
2. Selecciona el filtro temporal: Todos, Ano, Mes, Semana
3. Consulta el total de cada tipo de pieza

---

## Paleta de Colores

| Color | Hex | Uso |
|---|---|---|
| Background | `#1B2838` | Fondo principal |
| Card | `#2A3A4A` | Tarjetas |
| Primary | `#4ECDC4` | Botones, acentos |
| Secondary | `#3D4D5C` | Botones secundarios |
| Foreground | `#FFFFFF` | Texto principal |
| Muted | `#94A3B3` | Texto secundario |

---

## Tipografia

- **Fuente**: Inter (o system sans-serif)
- **Titulos grandes**: 48sp, ExtraBold
- **Titulos pantalla**: 20sp, ExtraBold
- **Nombres piezas**: 18sp, Bold, UPPERCASE
- **Cuerpo**: 14-16sp, Regular/Medium
- **Caption**: 12-14sp, Regular

---

## Licencia

Este proyecto es de uso personal y educativo.

---

## Autor

Pepe López