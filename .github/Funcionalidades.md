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

## ⭐️ Favoritar Estabelecimentos
Permite ao usuário salvar os botecos e trailers que ele mais frequenta e ama.

* Integração Google Maps: A API do Google fornece apenas o identificador único do local (place_id).
* Persistência Local: Foco 100% no banco de dados local. O app cria uma tabela Favoritos e salva o place_id, o nome do local e a data em que foi favoritado. Quando o usuário abre a aba de favoritos, o app lê esses IDs do banco local e exibe a lista instantaneamente.

## 📍 Lista para Conhecer (Quero Ir)
Funciona como um bookmark de locais desejados para os próximos fins de semana.

* Integração Google Maps: Utiliza o place_id do estabelecimento visualizado.
* Persistência Local: O banco local armazena esses IDs em uma tabela Lista_Para_Conhecer. Permite que o usuário consulte seus planos de rolê mesmo se estiver totalmente offline ou sem sinal de internet no subúrbio.

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
Para começarmos a colocar a mão na massa na parte técnica do seu app exclusivo para Android, o que você gostaria de definir agora?

* A estrutura das tabelas do banco de dados local (Room/SQLite) para gerenciar os favoritos e notas?
* A lista de palavras-chave exatas em português que o código vai usar para caçar os karaokês e parquinhos nas reviews do Google?
* A configuração inicial do projeto no Android Studio com as dependências da Google Play Services?


[1] [https://www.luiztools.com.br](https://www.luiztools.com.br/post/tutorial-crud-em-android-com-sqlite-e-recyclerview/)
[2] [https://www.thiengo.com.br](https://www.thiengo.com.br/banco-de-dados-local-com-a-room-api-youtuber-android-app-parte-6)
[3] [https://developers.google.com](https://developers.google.com/maps/documentation/places/web-service?hl=pt-br)
[4] [https://developers.google.com](https://developers.google.com/maps/architecture/discover-with-place-search-element?hl=pt-br)
[5] [https://www.youtube.com](https://www.youtube.com/watch?v=rpqi7Y1NQZY)
[6] [https://developers.google.com](https://developers.google.com/maps?hl=pt-br)
[7] [https://developers.google.com](https://developers.google.com/maps/documentation/capabilities-explorer?hl=pt-br)
