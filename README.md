# Sextou
O Sextou é um aplicativo de entretenimento, lazer e gastronomia focado em consumo imediato de baixo custo, cultura de subúrbio e estabelecimentos raiz. O app resolve o problema do usuário que quer descompressão de fim de semana (diurna ou noturna) sem frescura, destacando locais com comida pronta para consumo e diferenciais de entretenimento 

## Integração com Google Places

A integração usa o Places SDK for Android (New) e está isolada em dois módulos:

```text
UseCase (escolhe os tipos e critérios)
    -> PlacesRepository.Remote (somente modelos de domínio)
        -> PlacesRemoteImpl
            -> PlacesGateway
                -> PlacesClient
```

O Repository não decide se a busca é por restaurante, bar ou outro tipo. A camada de negócio preenche `includedTypes`, `excludedTypes`, `includedPrimaryTypes` e os demais critérios de `NearbySearchRequest`. Os identificadores precisam pertencer à [tabela de tipos filtráveis do Places](https://developers.google.com/maps/documentation/places/android-sdk/place-types?utm_campaign=gmp_git_agentskills_v1#table-a).

Operações disponíveis:

- `searchNearby`: busca até 20 lugares dentro de um raio de até 50 km;
- `searchByText`: busca por uma consulta textual com viés de localização e filtros opcionais;
- `getDetails`: recupera dados amplos do lugar selecionado, incluindo contatos, endereços, horários, preço, reviews, comodidades, acessibilidade, links, metadados de fotos e resumos quando disponíveis;
- `getPhoto`: resolve uma URI de foto com dimensões opcionais, sem expor `Bitmap` no domínio.

`food_truck` não é um tipo filtrável da tabela A atual. A regra de negócio pode representar “Food truck” no domínio e convertê-la em uma `PlaceTextSearchRequest` localizada; essa tradução não pertence ao Repository.

### Chave de API

Para desenvolvimento, defina a propriedade fora do repositório, em `~/.gradle/gradle.properties`:

```properties
PLACES_API_KEY=sua_chave
```

Em produção, use uma chave própria, restrita ao package `com.sextou`, ao SHA-1 do certificado do app e somente ao Places API (New). Nunca versione a chave. Para protótipos também existe a [Maps Demo Key](https://mapsplatform.google.com/maps-demo-key?utm_campaign=gmp_git_agentskills_v1), que não deve ser usada em produção.

### Atribuição, privacidade e custo

Toda tela que apresentar conteúdo retornado deve mostrar `providerAttribution` (`Google Maps`) em uma linha dedicada e preservar as atribuições de autores de fotos e reviews. Solicite consentimento revogável antes de usar a localização do aparelho e não persista conteúdo do Google indiscriminadamente.

Os campos de `getDetails` abrangem SKUs de custo elevado, inclusive Enterprise/Enterprise Plus e recursos que podem não existir em todos os locais ou regiões. Para produção, considere evoluir `PlaceDetailsRequest` com perfis de campos definidos pela camada de negócio para pagar somente pelo necessário. O uso está sujeito aos [Termos do Google Maps Platform](https://cloud.google.com/maps-platform/terms?utm_campaign=gmp_git_agentskills_v1).
