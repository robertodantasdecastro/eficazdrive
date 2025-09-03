EficazDrive
===========

PT-BR

EficazDrive é um serviço Android em segundo plano que monitora a tela para identificar solicitações de corridas (Uber/99/Bolt/InDrive/Rappi, etc.), executa OCR on-device (ML Kit), extrai os dados por meio de regras configuráveis por plataforma, calcula métricas (tempo, distância, custos, lucro estimado, etc.) e exibe um popup em overlay com os resultados em JSON. O projeto foi desenhado para ser simples, robusto, eficiente e seguro, priorizando desempenho e baixa latência.

Principais recursos
- Serviço foreground com captura de tela via MediaProjection
- OCR on-device (Google ML Kit) para alto desempenho e privacidade
- Extração por regras (regex/markers) por plataforma, definidas em `assets/config/platform_rules.json`
- Cálculos configuráveis por expressões em `assets/config/calculations.json`
- Popup overlay com JSON e botão de fechar; tempo de exibição configurável em `assets/config/app.json`
- Código moderno em Kotlin com coroutines, organizado por camadas

Instalação (sem Play Store)
1. Pré-requisitos: Android SDK, adb, Java 17+ e Gradle instalados (ou use os scripts em `scripts/`).
2. Conecte um dispositivo via USB com depuração habilitada.
3. Execute:
   - `bash scripts/build.sh`
   - `bash scripts/install.sh`
4. Abra o app (permissões):
   - Conceda a permissão de sobreposição (draw over other apps)
   - Conceda permissão de captura de tela (MediaProjection) quando solicitado
   - O serviço iniciará e o popup será mostrado quando uma solicitação for detectada

Configuração
- `app/src/main/assets/config/app.json`: tempo de exibição do popup, janela/área de captura
- `app/src/main/assets/config/platform_rules.json`: markers e regex por plataforma para extrair campos
- `app/src/main/assets/config/calculations.json`: fórmulas para derivar métricas (ex.: lucro estimado)

Como funciona (resumo)
1. O serviço captura periodicamente um frame da tela
2. O OCR converte a imagem em texto
3. O mecanismo de regras identifica a plataforma e extrai valores via regex
4. As fórmulas configuradas são avaliadas para gerar métricas adicionais
5. O popup overlay exibe todos os dados em JSON e fecha automaticamente

Licença
MIT (ver LICENSE)

Contribuição
Veja CONTRIBUTING.md

Publicação no GitHub
Consulte PUBLISHING.md para instruções e boas práticas.

EN

EficazDrive is an Android foreground service that monitors the screen to detect ride requests (Uber/99/Bolt/InDrive/Rappi, etc.), performs on-device OCR (ML Kit), extracts data via per‑platform configurable rules, computes metrics (time, distance, costs, estimated profit, etc.), and shows a JSON overlay popup. The project is designed to be simple, robust, efficient, and secure, prioritizing performance and low latency.

Key features
- Foreground service with MediaProjection screen capture
- On-device OCR (Google ML Kit) for high performance and privacy
- Rule-based extraction (regex/markers) per platform in `assets/config/platform_rules.json`
- Configurable calculations via expressions in `assets/config/calculations.json`
- Overlay popup with JSON and close button; display time in `assets/config/app.json`
- Modern Kotlin + coroutines layered architecture

Installation (without Play Store)
1. Prereqs: Android SDK, adb, Java 17+ and Gradle (or use `scripts/`).
2. Connect a device via USB with debugging enabled.
3. Run:
   - `bash scripts/build.sh`
   - `bash scripts/install.sh`
4. Open the app (permissions):
   - Grant overlay permission (draw over other apps)
   - Grant screen capture (MediaProjection) when prompted
   - The service will start and the popup will appear upon detection

Configuration
- `app/src/main/assets/config/app.json`: popup display time, capture window/area
- `app/src/main/assets/config/platform_rules.json`: per‑platform markers and regex to extract fields
- `app/src/main/assets/config/calculations.json`: formulas to derive metrics (e.g., estimated profit)

How it works (summary)
1. The service periodically captures a screen frame
2. OCR converts the image into text
3. The rule engine identifies the platform and extracts values via regex
4. Configured formulas are evaluated to compute additional metrics
5. The overlay popup displays all data in JSON and auto-closes

License
MIT (see LICENSE)

Contributing
See CONTRIBUTING.md

GitHub Publishing
See PUBLISHING.md.


