Aqui está a especificação técnica das funcionalidades do Sextou, desenhada exclusivamente para Android e baseada na integração entre a Google Places API e o seu Banco de Dados Local (como Room ou SQLite). [1, 2]
------------------------------
## 🛠️ Especificação de Features: App "Sextou"## 🪟 Funcionalidades Principais (Core Features)## 📋 Listagem de Estabelecimentos
Exibe os locais de entretenimento e comida pronta em formato de lista rolável para o usuário.

* Integração Google Maps: Busca inicial via Text Search ou Nearby Search usando o parâmetro includedTypes com as categorias selecionadas (bar, restaurant, meal_takeaway, convenience_store, etc.). Retorna nome, foto principal, nota, endereço e status de abertura. [3, 4]
* Persistência Local: O app armazena temporariamente os dados retornados (cache de 7 a 15 dias) para evitar requisições repetidas à API e economizar custos de infraestrutura.

## 🗺️ Visualização em Mapa Interativo
Exibe os estabelecimentos em um mapa interativo do Google, facilitando a navegação geográfica.

* Integração Google Maps: Utilização do Google Maps SDK para Android. A API plota os pinos (markers) na tela utilizando as coordenadas de latitude e longitude (location) obtidas na busca do Places. [5, 6, 7]
* Persistência Local: O banco local faz a ponte para colorir ou modificar os pinos com base no comportamento do usuário (ex: exibir um pino com ícone de coração se o local estiver favoritado no banco de dados).

## 🍽️ Visualização do Cardápio
Permite ao usuário consultar, na tela de detalhes do estabelecimento, um resumo das opções de comida e bebida informadas pelo Google Maps e acessar a fonte externa quando houver um cardápio mais completo.

* **Resumo no Sextou:** Exibe faixa de preço e indicadores como café da manhã, brunch, almoço, jantar, sobremesa, café, cerveja, vinho, coquetéis, comida vegetariana e cardápio infantil. Também informa se o local oferece consumo no estabelecimento, retirada, entrega ou retirada na calçada.
* **Integração Google Maps:** Ao abrir os detalhes, o app consulta o estabelecimento pelo `place_id` usando o Places SDK for Android e solicita somente os campos necessários: `PRICE_LEVEL`, `PRICE_RANGE`, `SERVES_BREAKFAST`, `SERVES_BRUNCH`, `SERVES_LUNCH`, `SERVES_DINNER`, `SERVES_DESSERT`, `SERVES_COFFEE`, `SERVES_BEER`, `SERVES_WINE`, `SERVES_COCKTAILS`, `SERVES_VEGETARIAN_FOOD`, `MENU_FOR_CHILDREN`, `DINE_IN`, `TAKEOUT`, `DELIVERY`, `CURBSIDE_PICKUP`, `PHOTO_METADATAS`, `WEBSITE_URI` e `GOOGLE_MAPS_URI`. A máscara de campos deve ser restrita para controlar latência e cobrança.
* **Fotos:** Quando disponíveis, as fotos do estabelecimento podem complementar a visualização. Como o Places não identifica quais fotos representam o cardápio, elas devem aparecer como “fotos do local”, sem serem apresentadas como uma lista oficial de pratos.
* **Cardápio completo:** A ação “Ver cardápio completo” abre primeiro o site oficial (`websiteUri`), quando informado, ou a página do estabelecimento no Google Maps (`googleMapsUri`) como alternativa. O botão não deve ser exibido quando nenhum dos dois endereços estiver disponível.
* **Dados indisponíveis:** Campos ausentes ou com valor desconhecido devem ser omitidos. Se nenhum atributo de alimentação estiver disponível, a tela informa “Cardápio não informado pelo estabelecimento” e ainda oferece o acesso externo, caso exista.
* **Persistência Local:** Armazena o resumo vinculado ao `place_id` apenas como cache temporário, respeitando as políticas de armazenamento e atribuição do Google Maps Platform. Ao existir conexão, o app prioriza uma consulta atualizada antes de exibir informações sensíveis a mudança, como faixa de preço e modalidades de atendimento.
* **Limitação da fonte:** A Places API não disponibiliza ao Sextou os itens, descrições e preços de cardápios completos de estabelecimentos arbitrários. A Google Business Profile API possui dados itemizados, mas seu acesso exige autorização OAuth da conta proprietária de cada local; por isso, ela não faz parte desta funcionalidade principal.

## ⭐ Nota do Estabelecimento
Exibe a avaliação média atribuída pelos usuários do Google ao estabelecimento para ajudar na comparação entre os locais.

* **Exibição:** Mostra a nota de `1,0` a `5,0`, com uma casa decimal, acompanhada da quantidade de avaliações e da identificação de que a informação vem do Google. Exemplo: “4,6 ★ no Google (328 avaliações)”.
* **Integração Google Maps:** O app solicita os campos `RATING` e `USER_RATING_COUNT` do Places SDK for Android. A nota é geral e representa a experiência no estabelecimento; ela não deve ser apresentada como uma avaliação exclusiva da comida, bebida, atendimento ou ambiente.
* **Dados indisponíveis:** Quando `RATING` estiver ausente, a interface exibe “Ainda sem avaliação no Google”. A quantidade de avaliações só deve ser mostrada quando `USER_RATING_COUNT` estiver disponível.
* **Persistência Local:** A nota e a quantidade de avaliações podem ser mantidas apenas como cache temporário associado ao `place_id`. Sempre que houver conexão, a tela de detalhes deve priorizar os valores atualizados.

## 💲 Faixa de Preço de Comidas e Bebidas
Apresenta uma estimativa do nível de preço do estabelecimento para ajudar o usuário a escolher um local compatível com seu orçamento.

* **Exibição:** Mostra o nível retornado pelo Google em uma escala visual de preço, de gratuito a muito caro, e exibe os valores monetários mínimo e máximo quando uma faixa estiver disponível. A moeda retornada pela API deve ser preservada.
* **Integração Google Maps:** O app solicita `PRICE_LEVEL` e `PRICE_RANGE` pelo Places SDK for Android. Esses campos descrevem o estabelecimento como um todo e não distinguem os preços de comidas dos preços de bebidas.
* **Limitação da fonte:** A Places API não fornece uma nota de custo-benefício nem uma avaliação de preço em estrelas. Por isso, a interface deve usar os termos “nível de preço” ou “faixa de preço” e nunca apresentar o dado como opinião dos clientes.
* **Dados indisponíveis:** Quando os dois campos estiverem ausentes, a interface exibe “Faixa de preço não informada”. Se apenas um deles existir, somente a informação disponível deve ser mostrada.
* **Persistência Local:** Os valores podem ser armazenados apenas como cache temporário associado ao `place_id`, com atualização prioritária quando houver conexão.

## ⭐️ Favoritar Estabelecimentos
Permite ao usuário salvar os botecos e trailers que ele mais frequenta e ama.

* Integração Google Maps: A API do Google fornece apenas o identificador único do local (place_id).
* Persistência Local: Foco 100% no banco de dados local. O app cria uma tabela Favoritos e salva o place_id, o nome do local e a data em que foi favoritado. Quando o usuário abre a aba de favoritos, o app lê esses IDs do banco local e exibe a lista instantaneamente.

## 📍 Lista para Conhecer (Quero Ir)
Funciona como um bookmark de locais desejados para os próximos fins de semana.

* Integração Google Maps: Utiliza o place_id do estabelecimento visualizado.
* Persistência Local: O banco local armazena esses IDs em uma tabela Lista_Para_Conhecer. Permite que o usuário consulte seus planos de rolê mesmo se estiver totalmente offline ou sem sinal de internet no subúrbio.

### ✅ Já Visitei o Estabelecimento
Permite ao usuário marcar os botecos, trailers e restaurantes onde ele já esteve, criando um histórico pessoal de locais conferidos.
*   **Integração Google Maps:** Utiliza o `place_id` fornecido pela API do Google para identificar o local de forma única na tela de detalhes ou diretamente no card da listagem.
*   **Persistência Local (Room/SQLite):** O aplicativo cria uma tabela chamada `Historico_Visitas`. Quando o usuário ativa essa função, o `place_id` é salvo com o registro da data. Na interface, o card ganha um indicador visual (ex: um check verde escrito "Já fui!").

### 🚫 Não Voltar no Estabelecimento
Funciona como uma "lista negra" privada do usuário para sinalizar locais com atendimento ruim, cerveja quente ou que não valem o custo-benefício.
*   **Integração Google Maps:** Vincula a ação de bloqueio ao `place_id` do estabelecimento correspondente.
*   **Persistência Local (Room/SQLite):** O aplicativo armazena o ID em uma tabela chamada `Lista_Negra`.
*   **Regra de Negócio Local:** Toda vez que o aplicativo carregar os resultados vindos da API do Google Maps, o código fará uma checagem rápida no banco local. Se o `place_id` constar na `Lista_Negra`, o estabelecimento é **totalmente ocultado** do mapa e da listagem do usuário, garantindo um feed livre de ciladas.


------------------------------
## 💡 Sugestão de Funcionalidades Secundárias (Foco em Entretenimento e Lazer)## 🎙️ Filtros Rápidos de Entretenimento (Tags do Sextou)
Botões no topo da tela para filtrar locais com Karaokê, Música ao Vivo ou Parquinhos.

* Integração Google Maps: O app solicita o campo places.amenityOptions (para extrair liveMusic e goodForChildren) e o campo reviews na API de Detalhes. Um script interno varre o texto das avaliações do Google buscando palavras-chave ("karaokê", "brinquedoteca").
* Persistência Local: Após a primeira varredura do Google, o banco de dados local armazena as tags geradas vinculadas ao place_id. Assim, quando outro usuário aplicar o filtro "Karaokê", o app faz a busca no banco local de forma ultra rápida.

## 🚗 Rota Automática para o Rolê
Permite ao usuário traçar a rota da sua localização atual até o boteco ou trailer escolhido.

* Integração Google Maps: O app utiliza uma Intent nativa do Android para abrir o aplicativo do Google Maps (ou Waze) já configurado com a coordenada de destino do local, ou utiliza a Directions API para desenhar o trajeto na tela do próprio app.
* Persistência Local: Nenhuma (operação puramente em tempo real).

## 🕒 Alerta de "Tá Aberto agora?" (Status em Tempo Real)
Garante que o usuário não dê "com a cara na porta" ao ir atrás de um podrão ou birosca de madrugada.

* Integração Google Maps: Retorna o campo current_opening_hours.open_now (booleano) e a string com os horários de funcionamento detalhados do dia da semana atual.
* Persistência Local: Armazena o horário padrão de funcionamento no banco de dados para consulta offline, mas sempre prioriza a checagem online se houver conexão.

## 📝 Notas e Anotações Pessoais do Boteco
Permite ao usuário escrever lembretes privados sobre o local (Ex: "Pedir a batata com calabresa, a de frango não vale a pena" ou "O parquinho fica nos fundos").

* Integração Google Maps: Nenhuma.
* Persistência Local: Totalmente gerenciado pelo banco local (Room). O app cria uma tabela Notas_Usuario associando o texto criado ao place_id correspondente.

------------------------------
## Referências oficiais

* [Campos de dados da Places API](https://developers.google.com/maps/documentation/places/web-service/data-fields)
* [Campos do modelo `Place` no Places SDK for Android](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place.Field)
* [Modelo `Place` e limites de nota e preço](https://developers.google.com/maps/documentation/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place)
* [Uso de máscaras de campos](https://developers.google.com/maps/documentation/places/web-service/choose-fields)
* [Cardápios na Google Business Profile API](https://developers.google.com/my-business/reference/rest/v4/accounts.locations/getFoodMenus)
