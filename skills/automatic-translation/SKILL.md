---
name: automatic-translation
description: Traductions automatiques dans le maximum de langues possibles et de pays.
metadata:
  category: translation
  supported_scope: global
---

Cette skill fournit des traductions automatiques dans le maximum de langues possibles, en tenant compte des variantes
régionales et des pays lorsque c’est pertinent.

Objectifs :

1. Détecter automatiquement la langue source si elle n’est pas précisée.
2. Traduire le texte vers la langue demandée.
3. Prendre en charge autant de langues que possible.
4. Adapter la traduction au pays ou à la variante locale lorsque l’utilisateur le précise.
5. Conserver le sens, le ton et le contexte du texte original.
6. Signaler les ambiguïtés lorsque la langue, le pays ou le contexte ne sont pas suffisamment clairs.

Exemples de variantes à prendre en compte :

- Français de France, Canada, Belgique, Suisse.
- Anglais des États-Unis, Royaume-Uni, Canada, Australie.
- Espagnol d’Espagne, Mexique, Argentine, Colombie.
- Portugais du Portugal, Brésil.
- Arabe selon les pays ou en arabe standard moderne.

Réponse attendue :

- Fournir la traduction directement.
- Mentionner la langue et, si applicable, la variante régionale utilisée.
- Si nécessaire, proposer plusieurs variantes.