# Funcionalidades futuras do Sextou

## 🔥 Bombando Agora

> **Motivo do adiamento:** a Places API (New) e o Places SDK for Android não expõem lotação ao vivo, quantidade de pessoas, movimento atual nem “horários de pico”. Esta funcionalidade só deve ser retomada quando o Sextou possuir uma fonte própria, consentida e suficientemente representativa de sinais de presença.

Destaca os bares, restaurantes e locais de entretenimento com maior movimento recente entre os usuários do Sextou, ajudando o usuário a encontrar onde o rolê está mais animado naquele momento.

### Viabilidade da fonte de dados

* **Limitação do Google Maps:** A Places API e o Places SDK for Android não disponibilizam campos de lotação ao vivo, movimento atual ou “horários de pico” para consumo pela aplicação. O Sextou não deve extrair esses dados por scraping da interface do Google Maps.
* **Uso permitido dos dados do Google:** O Google Maps identifica o estabelecimento por `place_id`, fornece sua localização e permite consultar se ele está aberto. Esses dados servem para associar e validar os sinais de presença, mas não medem a quantidade de pessoas.
* **Fonte do movimento:** O indicador deve ser calculado por um serviço remoto do Sextou a partir de check-ins voluntários e sinais de presença enviados com consentimento por usuários que estejam próximos ao estabelecimento. A funcionalidade depende desse serviço e não pode operar apenas com Room ou com a Places API.

### Experiência do usuário

* A tela inicial exibe uma seção “Bombando agora”, ordenada pelo nível de movimento recente e limitada à região pesquisada pelo usuário.
* O mapa diferencia os locais bombando com um marcador de chama, mantendo acessibilidade por texto e cor de alto contraste.
* O card e a tela de detalhes apresentam apenas faixas qualitativas: “Movimento moderado”, “Movimentado” ou “Bombando agora”. A quantidade exata de pessoas não é exibida.
* O horário da última atualização deve acompanhar o indicador. Dados com mais de 15 minutos não podem ser apresentados como movimento atual.
* Quando não houver sinais recentes suficientes, nenhum nível deve ser inferido; a interface exibe “Movimento indisponível no momento”.

### Regras de negócio

* Um sinal de presença só é aceito quando o usuário realiza um check-in ou autoriza a verificação de proximidade com o estabelecimento em primeiro plano.
* O cálculo considera sinais únicos dos últimos 30 minutos e deve impedir que o mesmo dispositivo infle artificialmente o movimento.
* Um nível somente é publicado quando existirem ao menos cinco sinais únicos no período. Abaixo desse limite, o resultado permanece indisponível para proteger a privacidade dos participantes.
* O nível “Bombando agora” exige, por padrão, pelo menos dez sinais únicos e movimento 50% superior à média histórica do próprio Sextou para o mesmo local, dia da semana e faixa de horário. Os limites devem ser configuráveis no serviço remoto sem atualização do aplicativo.
* Estabelecimentos fechados, temporariamente fechados ou bloqueados pela função “Não voltar” não aparecem na seção, independentemente do nível calculado.
* O indicador representa somente a atividade observada entre usuários participantes do Sextou; ele não deve ser descrito como lotação total ou dado oficial do Google.

### Privacidade e segurança

* A participação é opcional e desativada por padrão. O aplicativo deve explicar a finalidade antes de solicitar localização ou enviar um sinal.
* Não deve existir rastreamento contínuo em segundo plano. O evento enviado contém somente um identificador anônimo e rotativo, o `place_id` e o instante do sinal; a localização exata e o histórico individual não devem ser expostos a outros usuários.
* Sinais brutos devem expirar após o período necessário ao cálculo. Apenas agregações sem identificação individual podem compor a média histórica.
* O serviço remoto deve validar proximidade, aplicar limite de requisições e adotar mecanismos contra sinais automatizados antes de contabilizar um evento.

### Arquitetura prevista

* **UI (`features`):** Tela e componentes em Jetpack Compose observam um `UiState` imutável por meio de ViewModel e `StateFlow`.
* **Domínio (`domain`):** Um UseCase combina os níveis remotos com locais abertos, preferências e bloqueios do usuário. Os modelos do domínio não dependem do SDK do Google nem de Android.
* **Dados remotos (`networking`):** Um Repository implementado com Retrofit envia sinais consentidos e consulta as agregações de movimento do serviço do Sextou.
* **Dados locais (`local`):** Room armazena somente o último resultado agregado como cache de curta duração. Um valor expirado nunca recebe o selo “agora”.
* **Testes:** A classificação dos níveis, expiração, quantidade mínima de sinais, comparação com a média e exclusão de locais fechados ou bloqueados devem possuir testes unitários no domínio.

## Outras ideias futuras

Sim. A Places API (New) hoje tem vários campos que combinam **muito bem com a proposta do Sextou** e que ainda não aparecem na sua lista. Eu priorizaria funcionalidades que ajudem o usuário a responder rapidamente: **“onde vou hoje?”**.

### Funcionalidades que eu adicionaria

1. **“Aberto até tarde” / “Ainda dá tempo”**
   Em vez de apenas “Aberto agora”, mostrar:

    * Aberto até 00h
    * Aberto até 02h
    * Fecha em 45 min
    * Aberto por mais 3h

   Você consegue derivar isso de `currentOpeningHours`, que traz inclusive horários especiais dos próximos sete dias. ([Google for Developers][1])

2. **Perfil do rolê**

   Criaria badges bem visuais na listagem:

    * 🎵 Música ao vivo
    * 🍹 Drinks
    * 🍺 Cerveja
    * 🍷 Vinho
    * ⚽ Bom para assistir jogos
    * 👥 Bom para grupos
    * 🌳 Mesas ao ar livre
    * 🐶 Aceita pets
    * 👨‍👩‍👧 Bom para crianças
    * 🥗 Opções vegetarianas

   E o interessante é que **esses atributos existem diretamente na Places API**: `liveMusic`, `servesCocktails`, `servesBeer`, `servesWine`, `goodForWatchingSports`, `goodForGroups`, `outdoorSeating`, `allowsDogs`, `goodForChildren` e `servesVegetarianFood`. ([Google for Developers][2])

3. **“Sextou Agora”**

   Essa seria uma das minhas funcionalidades principais. O usuário toca em um botão e o app monta automaticamente uma seleção considerando somente dados Google:

   **Sextou Agora**

   > Lugares abertos, próximos, bem avaliados e que ainda ficam abertos por pelo menos 2 horas.

   Você pode ranquear usando:
   `distance + rating + userRatingCount + currentOpeningHours + priceLevel`.

   Isso transforma o app de um simples catálogo em um **assistente para decidir onde sair**. Rating, quantidade de avaliações, faixa de preço e horário estão disponíveis na Places API. ([Google for Developers][2])

4. **“Qual é a vibe?”**

   A Google atualmente disponibiliza resumos gerados por IA dentro da própria Places API. O `generativeSummary` pode resumir características do estabelecimento e o `reviewSummary` sintetiza os comentários dos usuários. ([Google for Developers][3])

   Por exemplo:

   > **Sobre o lugar**
   > Bar descontraído conhecido pelos hambúrgueres, cervejas e ambiente animado para grupos.

   Isso encaixa perfeitamente na proposta do Sextou.

5. **O que as pessoas estão falando**

   Em vez de fazer o usuário ler dezenas de avaliações:

   ⭐ 4,6 · 2.340 avaliações
   **Resumo das avaliações**

   > Clientes elogiam principalmente os drinks, atendimento e ambiente. Alguns relatam demora nos horários de pico.

   A própria API possui `reviewSummary`, além de retornar avaliações individuais — atualmente até 5 reviews no objeto Place. ([Google for Developers][1])

6. **Filtro “tipo de noite”**

   Em vez de filtros técnicos, eu colocaria filtros orientados à intenção:

   **🍺 Boteco**
   cerveja + preço baixo/médio

   **🍹 Drinks**
   cocktails + bar

   **❤️ Encontro**
   restaurante + drinks + boa avaliação

   **👥 Galera**
   `goodForGroups`

   **⚽ Ver o jogo**
   `goodForWatchingSports`

   **🎵 Música ao vivo**
   `liveMusic`

   **🌳 Ao ar livre**
   `outdoorSeating`

   Os filtros continuam usando exclusivamente atributos retornados pelo Google. ([Google for Developers][3])

7. **Estacionamento**

   Para quem sai de carro isso pode ser um diferencial enorme:

   > 🅿️ Estacionamento gratuito
   > 🅿️ Estacionamento pago
   > 🚗 Valet
   > 🛣️ Estacionamento na rua

   A API possui `parkingOptions`, incluindo estacionamento gratuito/pago, garagem, rua e valet. ([Google for Developers][3])

8. **Formas de pagamento**

   Mostrar diretamente na ficha:

   > 💳 Crédito
   > 💳 Débito
   > 📱 NFC
   > 💵 Somente dinheiro

   Existe `paymentOptions` para isso. ([Google for Developers][3])

9. **Acessibilidade**

   Um filtro muito útil:

   **♿ Acessível**

   Pode considerar:

    * entrada acessível;
    * mesas acessíveis;
    * banheiro acessível;
    * estacionamento acessível.

   Esses quatro atributos são fornecidos em `accessibilityOptions`. ([Google for Developers][1])

10. **Aceita reserva**

Badge:

> 📅 Aceita reservas

Há um campo específico `reservable`. ([Google for Developers][2])

11. **Quanto vou gastar**

Você já colocou faixa de preço, mas eu daria muito destaque a isso porque agora há também `priceRange`, além de `priceLevel`.

Por exemplo:

**$** Econômico
R$ 20–50

**$$** Moderado
R$ 50–100

`priceRange` pode trazer `startPrice` e `endPrice`. ([Google for Developers][1])

12. **Depois que eu visitar → Avaliar no Google**

Como você já tem **“Já visitei”**, existe uma integração muito natural:

> Você foi ao Bar X ontem.
> ⭐ Avaliar no Google

O Google retorna `googleMapsLinks.writeAReviewUri`, além dos links para avaliações, fotos, directions e a página do estabelecimento. ([Google for Developers][1])

---

### Uma funcionalidade que eu transformaria no coração do app

Eu criaria uma tela chamada **“O que você quer fazer hoje?”**.

Ela poderia começar assim:

**Hoje é sexta 🍻**

`🍺 Beber` `🍔 Comer` `🍹 Drinks` `🎵 Música`
`⚽ Ver jogo` `❤️ Encontro` `👨‍👩‍👧 Família` `🐶 Pet friendly`

Depois:

**Quanto quer gastar?**

`$` `$$` `$$$`

Depois:

**Até onde você vai?**

`5 min` `10 min` `20 min`

E o Sextou entrega **5–10 opções**, em vez de jogar 100 restaurantes em uma lista.

Isso cria uma diferença importante:

**Google Maps:**

> “Procure restaurantes.”

**Sextou:**

> **“Me diga o que você quer fazer nesta sexta e eu te mostro onde ir.”**

Essa, para mim, é a proposta de valor mais forte para o app.

### Um detalhe importante sobre seu item “Cardápio”

Eu revisaria a funcionalidade **3. Visualização do cardápio**. A Places API não oferece, de forma geral, um **cardápio estruturado com pratos + preços**. Ela oferece atributos como `servesBeer`, `servesWine`, `servesCocktails`, `servesCoffee`, `servesDessert`, refeições e comida vegetariana, e os resumos de IA podem mencionar destaques gastronômicos, mas não equivalem a uma API completa de menu. ([Google for Developers][2])

Eu chamaria essa seção de **“O que tem por lá”** ou **“Comidas e bebidas”**, o que representa melhor o que o Google realmente consegue fornecer.

Se você quiser, eu também posso montar uma **lista fechada de umas 25 funcionalidades do Sextou**, separando em **MVP, versão 1.0 e futuras**, e indicando **qual campo exato da Places API alimenta cada uma**.

[1]: https://developers.google.com/maps/documentation/places/web-service/reference/rest/v1/places "REST Resource: places  |  Places API  |  Google for Developers"
[2]: https://developers.google.com/maps/documentation/places/web-service/data-fields "Place Data Fields (New)  |  Places API  |  Google for Developers"
[3]: https://developers.google.com/maps/documentation/places/web-service/op-overview "About the Places API (New)  |  Google for Developers"
