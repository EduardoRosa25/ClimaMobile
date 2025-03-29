# Aplicativo de Previsão do Tempo

Este é um aplicativo Android que mostra a previsão do tempo atual e para as próximas 6 horas.

## Configuração

### 1. Chave da API OpenWeatherMap

1. Acesse [OpenWeatherMap](https://openweathermap.org/) e crie uma conta
2. Obtenha sua chave de API na seção "API keys"
3. Substitua `YOUR_API_KEY` no arquivo `app/src/main/res/values/api_keys.xml` pela sua chave de API

### 2. Ícones do Clima

O aplicativo usa ícones do OpenWeatherMap. Você precisa adicionar os ícones correspondentes na pasta `app/src/main/res/drawable` com o formato `weather_XX` onde XX é o código do ícone retornado pela API.

Por exemplo:
- `weather_01d.png` para céu limpo durante o dia
- `weather_01n.png` para céu limpo durante a noite
- `weather_02d.png` para céu parcialmente nublado durante o dia
- etc.

Você pode baixar os ícones do [OpenWeatherMap Icons](https://openweathermap.org/weather-conditions)

## Funcionalidades

- Mostra a previsão do tempo atual com:
  - Nome da localização
  - Dia da semana
  - Ícone do clima
  - Condição do tempo
  - Temperatura

- Mostra a previsão para as próximas 6 horas com:
  - Hora
  - Ícone do clima
  - Temperatura

## Tecnologias Utilizadas

- Kotlin
- Jetpack Compose
- Hilt (Injeção de Dependência)
- Ktor (Cliente HTTP)
- OpenWeatherMap API 