# Débitos de skill

## 2026-08-28

* A skill local de arquitetura orienta MVVM, Clean Architecture, Room e
  navegação em alto nível, mas não detalha a integração específica do Maps
  Compose. A feature usa `GoogleMap`/`Marker` com a chave do manifesto e deve
  ser revisada quando houver uma convenção visual ou de mapas compartilhada no
  projeto.

## 2026-08-29

* A skill local de arquitetura não detalha o fluxo de permissão de localização
  Android nem a integração com o Fused Location Provider. A implementação
  atual mantém essa responsabilidade na Activity, com um provedor pequeno e
  testável por contrato; uma skill específica deve ser adicionada caso o
  projeto passe a exigir atualizações contínuas, precisão configurável ou
  políticas de consentimento mais abrangentes.
