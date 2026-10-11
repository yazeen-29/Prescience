# Prescience UI

This folder contains presentation-only code that can be handed to a UI-focused contributor.

- `theme/` — stable visual tokens and Material theme.
- `components/CameraChrome.kt` — reusable camera controls, badges, status strips, consent card, permission state, and saved-media preview.

Screen bindings, state collection, camera/BLE lifecycle, navigation, and callbacks remain in `MainActivity.kt`. Voice and camera domain logic remain in their existing packages. UI components receive state and callbacks as parameters and contain no recording, consent-processing, model, storage, or BLE logic.

For a UI-only handoff, include this `ui/` folder, `Consent_voice/app/src/main/java/com/atreides/consentvoice/ui/`, and the presentation wiring in the two `MainActivity.kt` files. That wiring only imports components and passes existing state/callbacks through; do not include the controller, BLE, model, or media packages.
