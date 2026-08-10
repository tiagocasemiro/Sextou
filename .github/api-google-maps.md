# Funcionalidades do Sextou e integração com Google Maps

Atualizado em 10 de agosto de 2026.

## 1. Objetivo do produto

O Sextou ajuda o usuário a encontrar opções próximas, acessíveis e disponíveis para consumo imediato, principalmente nos fins de semana. O foco é gastronomia, lazer, cultura de subúrbio e estabelecimentos locais ou "raiz", com uma experiência simples para decidir onde ir, o que comer, quanto gastar e quais atrações encontrar.

## 2. Funcionalidades propostas para o Sextou

### 2.1 Descoberta de estabelecimentos

| Funcionalidade | Descrição | Fonte principal |
| --- | --- | --- |
| Perto de mim | Lista estabelecimentos próximos à localização atual, ordenados por distância ou relevância. | Google Places + localização do dispositivo |
| Mapa e lista | Permite alternar entre marcadores no mapa e uma lista de resultados. | Maps JavaScript API + base Sextou |
| Busca por nome ou categoria | Pesquisa por termos como bar, boteco, churrasco, hambúrguer, pizzaria, comida brasileira, música ao vivo ou roda de samba. | Places Text Search + categorias Sextou |
| Filtros gastronômicos | Filtra por tipo de comida, faixa de preço, avaliação, distância, entrega, retirada, consumo no local e opções vegetarianas. | Google Places + base Sextou |
| Filtros de entretenimento | Filtra por música ao vivo, transmissão de esportes, espaço externo, ambiente familiar, pet friendly e outras características. | Google Places + curadoria Sextou |
| Aberto agora | Exibe somente locais em funcionamento no momento da consulta. | Horários atuais do Google Places |
| Aberto no fim de semana | Mostra locais que abrem na sexta à noite, sábado ou domingo, incluindo horários especiais quando disponíveis. | Horários regulares e atuais do Google Places |
| Comida agora | Destaca estabelecimentos cuja cozinha, retirada ou entrega está funcionando, quando esses horários secundários estiverem disponíveis. | Google Places + confirmação do estabelecimento |
| Baixo custo | Prioriza locais com menor faixa de preço e ofertas cadastradas. | Faixa de preço do Google Places + preços Sextou |
| Destaques do bairro | Recomenda estabelecimentos populares, tradicionais ou bem avaliados em cada região. | Avaliações Google + curadoria Sextou |

### 2.2 Cardápios e ofertas

| Funcionalidade | Descrição | Fonte principal |
| --- | --- | --- |
| Cardápio do estabelecimento | Lista categorias, itens, descrições, preços, fotos e disponibilidade. | Base própria, cadastro do estabelecimento ou parceiro de pedidos |
| Pratos disponíveis hoje | Permite ao estabelecimento marcar itens esgotados, pratos do dia e horários de disponibilidade. | Portal do estabelecimento |
| Cardápio de fim de semana | Destaca feijoada, churrasco, petiscos, café da manhã, brunch, almoço, jantar e outros itens oferecidos em dias específicos. | Portal do estabelecimento + base Sextou |
| Combos e promoções | Exibe promoções como dose dupla, balde de cerveja, combos, rodízio e happy hour. | Portal do estabelecimento |
| Faixa de preço estimada | Apresenta uma visão rápida do custo do local e, quando houver cardápio próprio, o preço real dos itens. | Google Places + base Sextou |
| Preferências alimentares | Identifica opções vegetarianas e permite cadastrar opções veganas, sem glúten, sem lactose e outras restrições. | Google Places + dados próprios |
| Formas de atendimento | Informa se há consumo no local, entrega, retirada, reserva ou retirada na calçada. | Google Places + estabelecimento |
| Link externo para cardápio ou pedido | Abre o site oficial, WhatsApp ou plataforma de pedidos quando o cardápio não estiver cadastrado no Sextou. | Site do Google Places + cadastro Sextou |

> **Limitação importante:** a Google Places API não fornece um cardápio completo e padronizado de todos os estabelecimentos. Ela pode informar o site oficial, faixa de preço, fotos, horários e atributos como servir almoço, jantar, cerveja ou comida vegetariana. Para garantir cardápios com itens e preços, o Sextou precisa manter dados próprios, receber cadastro dos estabelecimentos ou integrar-se a parceiros. A cobertura do Google também não garante uma enumeração absolutamente completa de todos os negócios de uma região.

### 2.3 Página do estabelecimento

Cada estabelecimento pode ter uma página contendo:

- nome, endereço, distância e posição no mapa;
- situação do negócio e indicador de aberto ou fechado;
- horários normais e horários especiais dos próximos dias;
- horários secundários, quando disponíveis: cozinha, happy hour, retirada, entrega, brunch, almoço e jantar;
- telefone, site, link do Google Maps e redes sociais cadastradas;
- fotos, faixa de preço, nota e quantidade de avaliações;
- até cinco avaliações retornadas pelo Places, respeitando as atribuições exigidas;
- recursos como música ao vivo, área externa, acessibilidade, estacionamento, banheiro, ambiente para grupos, crianças, animais e transmissão de esportes;
- tipos de comida e bebida: café da manhã, brunch, almoço, jantar, sobremesa, café, cerveja, vinho, coquetéis e comida vegetariana;
- cardápio próprio, promoções, atrações e agenda do estabelecimento;
- ações para favoritar, compartilhar, traçar rota, ligar, reservar ou pedir.

### 2.4 Agenda e entretenimento

| Funcionalidade | Descrição | Fonte principal |
| --- | --- | --- |
| Agenda do fim de semana | Lista shows, rodas de samba, karaokê, DJs, futebol, feiras e eventos locais por data e horário. | Base Sextou + estabelecimentos |
| O que está acontecendo agora | Mostra eventos que já começaram ou começam em breve perto do usuário. | Base Sextou + geolocalização |
| Entrada e couvert | Informa se o evento é gratuito, tem entrada, couvert artístico ou consumação mínima. | Estabelecimento |
| Recomendações combinadas | Prioriza locais que oferecem comida e entretenimento no mesmo período. | Base Sextou + atributos do Places |
| Alertas personalizados | Notifica sobre promoções, eventos e estabelecimentos favoritos no fim de semana. | Base Sextou |

### 2.5 Conta e comunidade

- favoritos e listas pessoais, como “botecos para conhecer”;
- histórico de lugares visualizados ou visitados;
- compartilhamento de estabelecimentos, cardápios, promoções e eventos;
- avaliação própria do Sextou para preço, comida, atendimento e ambiente;
- denúncia de informação incorreta, estabelecimento fechado ou preço desatualizado;
- sugestões de novos locais e eventos pela comunidade;
- preferências de distância, orçamento, alimentação e tipos de rolê.

### 2.6 Área do estabelecimento

- reivindicar e validar o perfil do negócio;
- atualizar descrição, contatos, redes sociais e comodidades;
- cadastrar e atualizar cardápios, preços, fotos e disponibilidade;
- publicar promoções e eventos com data de início e fim;
- configurar horários específicos da cozinha e do happy hour;
- responder a dúvidas e acompanhar visualizações, rotas e cliques;
- sinalizar lotação, fila, mesas disponíveis ou item esgotado em tempo quase real.

## 3. Funcionalidades disponíveis nas APIs web do Google Maps

“API aberta”, neste documento, significa uma API pública para desenvolvedores. O Google Maps Platform não é um serviço anônimo ou irrestrito: em geral, exige projeto Google Cloud com faturamento, API habilitada e chave de API ou OAuth. Preços, cotas, cobertura regional e estágio de lançamento variam por recurso.

### 3.1 APIs recomendadas para o núcleo do Sextou

| API ou recurso | Funcionalidades disponíveis | Uso no Sextou |
| --- | --- | --- |
| **Maps JavaScript API** | Mapas web interativos em 2D e 3D, controles, eventos, marcadores, janelas de informação, formas, sobreposições, animações e gráficos WebGL. | Mapa principal, seleção de região e visualização dos estabelecimentos. |
| **Places API (New)** | Busca por texto ou proximidade, autocomplete, detalhes do local e fotos. Retorna identidade, endereço, coordenadas, tipos, contatos, horários, preço, avaliações e diversos atributos do estabelecimento. | Descoberta, filtros e página do estabelecimento. |
| **Places Library para JavaScript** | Acesso aos recursos de lugares diretamente na aplicação web, incluindo busca, detalhes e componentes de autocomplete. | Campo de busca e seleção de lugares no mapa. |
| **Places UI Kit** | Componentes prontos e personalizáveis para exibir busca, detalhes e conteúdo de lugares no frontend. | Acelera um MVP, desde que o visual e as regras de uso atendam à identidade do Sextou. |
| **Routes API** | Calcula rotas, distância, duração, tráfego e matrizes entre origens e destinos para diferentes meios de transporte. | Botão “Como chegar”, tempo de viagem e ordenação por acessibilidade. |
| **Geocoding API** | Converte endereço em latitude/longitude e coordenadas em endereço. | Cadastro de estabelecimentos e pesquisa por bairro ou endereço. |
| **Geolocation API** | Estima a localização de um dispositivo por antenas de celular e pontos Wi-Fi. | Alternativa quando a geolocalização nativa do navegador não for adequada. |
| **Address Validation API** | Valida, corrige e padroniza endereços e seus componentes. | Melhora a qualidade dos endereços cadastrados pelos estabelecimentos. |
| **Time Zone API** | Informa o fuso horário de uma coordenada. | Interpretação correta de horários e eventos em diferentes regiões. |

### 3.2 Dados úteis oferecidos pelo Places API (New)

| Grupo | Exemplos de dados utilizáveis |
| --- | --- |
| Identificação | ID estável do lugar, nome, tipo principal, demais tipos e status do negócio. |
| Localização | Endereço formatado, componentes do endereço, coordenadas, viewport e Plus Code. |
| Contato | Telefone nacional/internacional, site oficial e link no Google Maps. |
| Funcionamento | Horário regular, horário atual dos próximos sete dias e dias/horários especiais. |
| Horários específicos | Cozinha, happy hour, café da manhã, brunch, almoço, jantar, entrega, retirada e drive-through, quando cadastrados na origem. |
| Reputação | Nota de 1 a 5, quantidade de avaliações e até cinco avaliações relevantes. |
| Conteúdo visual | Referências de até dez fotos por lugar, com atribuição do autor quando exigida. |
| Preço | Faixa ou nível de preço, quando disponível. |
| Alimentação | Serve café da manhã, brunch, almoço, jantar, cerveja, vinho, coquetéis, café, sobremesa ou comida vegetariana. |
| Atendimento | Consumo no local, entrega, retirada, retirada na calçada e reserva. |
| Ambiente | Música ao vivo, área externa, adequado para grupos, crianças, animais ou assistir esportes. |
| Estrutura | Estacionamento, formas de pagamento, banheiro e opções de acessibilidade. |

Nem todos os campos existem para todos os lugares. O Sextou deve tratar dados ausentes como “não informado”, e não como uma resposta negativa.

### 3.3 Busca e descoberta com Places

- **Nearby Search:** encontra lugares dentro de um raio e permite incluir ou excluir tipos, como `restaurant`, `bar`, `bakery`, `cafe`, `night_club` e categorias específicas de culinária.
- **Text Search:** pesquisa expressões livres, como “feijoada aberta domingo”, “samba com comida” ou “bar barato em Madureira”, com filtros geográficos e de atributos disponíveis.
- **Autocomplete:** sugere lugares, endereços e consultas enquanto o usuário digita.
- **Place Details:** recupera os campos escolhidos de um estabelecimento a partir do Place ID.
- **Place Photos:** fornece fotos associadas ao lugar e permite solicitar dimensões adequadas para a interface.
- **Places Aggregate API:** gera contagens e, em certos casos, IDs de lugares que correspondem a critérios dentro de uma área; pode apoiar análises de cobertura e expansão por bairro.

### 3.4 Recursos de visualização de mapas

| Recurso | Breve descrição |
| --- | --- |
| Mapas 2D e 3D | Exibem ruas, terreno, satélite e construções, com câmera, inclinação e rotação quando suportadas. |
| Marcadores avançados | Identificam estabelecimentos com ícones, cores, conteúdo HTML e tratamento de colisões. |
| Agrupamento de marcadores | Reúne muitos estabelecimentos próximos para evitar poluição visual. |
| Janelas de informação | Mostram um resumo do local ao selecionar um marcador. |
| Formas e desenhos | Exibem círculos de alcance, bairros, polígonos, linhas e áreas selecionadas. |
| Estilo personalizado | Adapta cores e elementos do mapa à identidade visual do Sextou usando Map IDs e estilos em nuvem. |
| Estilo orientado a dados | Colore limites administrativos ou datasets próprios de acordo com quantidade, categoria ou popularidade. |
| Camada GeoJSON | Renderiza dados geográficos próprios, como regiões atendidas e circuitos gastronômicos. |
| Camadas de tráfego, transporte e ciclovias | Exibem condições de trânsito, redes de transporte público e infraestrutura para bicicletas. |
| Street View | Mostra panoramas de 360 graus para o usuário reconhecer a fachada e o entorno. |
| WebGL Overlay View | Adiciona gráficos 2D/3D e animações sincronizadas com o mapa vetorial. |
| Controles e eventos | Permite zoom, tela cheia, tipo de mapa, gestos, cliques, arraste e controles personalizados. |
| Localização e idioma | Ajusta idioma, região e experiência do mapa ao público atendido. |

### 3.5 Outras APIs web que podem complementar o app

| API | Breve descrição | Possível uso no Sextou |
| --- | --- | --- |
| **Maps Embed API** | Incorpora um mapa interativo por `iframe` nos modos lugar, visualização, rota, Street View ou busca. | Página pública simples ou compartilhável sem construir um mapa completo. |
| **Maps Static API** | Gera uma imagem de mapa por URL com centro, zoom, marcadores, caminhos e estilo. | Imagem de compartilhamento, e-mail ou tela que não precisa de interação. |
| **Street View Static API** | Retorna uma imagem estática de um panorama do Street View. | Prévia da fachada ou da rua do estabelecimento. |
| **Map Tiles API** | Fornece tiles 2D, tiles 3D fotorrealistas e tiles do Street View. | Experiências cartográficas avançadas; não é necessária para o MVP. |
| **Aerial View API** | Cria e entrega vídeos aéreos renderizados com imagens geoespaciais 3D. | Destaques promocionais de regiões ou estabelecimentos, onde houver cobertura. |
| **Elevation API** | Obtém a altitude de um ou vários pontos. | Pouco relevante para o MVP; pode apoiar rotas de caminhada ou bicicleta. |
| **Roads API** | Relaciona coordenadas a vias próximas e pode fornecer informações como limites de velocidade. | Rastreamento ou experiências futuras de mobilidade. |
| **Route Optimization API** | Otimiza rotas com várias paradas e veículos. | Roteiros gastronômicos, excursões ou logística futura. |
| **Air Quality API** | Retorna índices, poluentes e recomendações de saúde por local. | Informação contextual para eventos externos. |
| **Pollen API** | Retorna índices e tipos de pólen por local. | Alerta opcional para atividades ao ar livre. |
| **Weather API** | Retorna condições atuais, previsões horárias/diárias e histórico recente. | Recomendar ambientes internos ou externos conforme o clima. |

As APIs **Directions**, **Distance Matrix** e **Places Legacy** ainda podem aparecer em exemplos antigos, mas estão em modo Legacy. Para novas implementações, devem ser priorizadas **Routes API** e **Places API (New)**.

## 4. Arquitetura de dados sugerida

O Google Maps deve ser usado como fonte de descoberta e contexto geográfico, não como banco exclusivo do produto.

| Entidade própria | Dados sugeridos |
| --- | --- |
| Estabelecimento Sextou | `placeId` do Google, perfil reivindicado, descrição editorial, redes sociais, selo de verificação e curadoria. |
| Cardápio | Categorias, itens, descrição, preço, foto, disponibilidade, restrições alimentares e dias/horários. |
| Evento | Nome, descrição, início, fim, preço/entrada, atrações, classificação e estabelecimento. |
| Promoção | Título, regras, preço, período de validade, dias da semana e limite de uso. |
| Favorito | Usuário, estabelecimento e listas pessoais. |
| Avaliação Sextou | Notas de preço, comida, atendimento e ambiente, comentário e moderação. |
| Atualização operacional | Lotação, fila, mesas, cozinha aberta, item esgotado e data da última confirmação. |

O `placeId` funciona como vínculo com o Google Places, enquanto os dados exclusivos do Sextou ficam sob controle da aplicação. Deve-se revisar as políticas do Google antes de armazenar, modificar ou exibir conteúdo retornado pelas APIs.

## 5. Priorização sugerida

### MVP

1. Localização atual, mapa e lista de estabelecimentos.
2. Busca por proximidade, texto e categoria.
3. Filtros: aberto agora, aberto no fim de semana, preço, distância, comida e entretenimento.
4. Página do estabelecimento com horário, contato, fotos, nota, atributos e rota.
5. Cardápio próprio básico com itens, preços e disponibilidade.
6. Favoritos e compartilhamento.
7. Portal simples para o estabelecimento atualizar cardápio, promoções e eventos.

### Evolução

1. Agenda de eventos e promoções em tempo real.
2. Recomendações personalizadas por orçamento e tipo de rolê.
3. Avaliações próprias e contribuição da comunidade.
4. Alertas de fim de semana, lotação e itens disponíveis.
5. Roteiros com várias paradas e informações de clima.

## 6. Requisitos técnicos, segurança e custos

- criar um projeto Google Cloud com faturamento habilitado;
- habilitar somente as APIs necessárias;
- usar máscaras de campo no Places para solicitar apenas os dados exibidos e controlar custos;
- restringir chaves web por domínio e por API;
- usar uma chave diferente no backend, protegida por IP ou OAuth quando suportado;
- nunca colocar a chave de web services do servidor no frontend ou no repositório;
- configurar cotas, alertas de orçamento, monitoramento e tratamento de indisponibilidade;
- respeitar atribuições, regras de exibição, limites de armazenamento e termos específicos de cada API;
- não assumir que um campo ausente significa “não”; apresentar “não informado” quando apropriado;
- manter cardápios, promoções e eventos em uma base própria com data da última atualização.

## 7. Referências oficiais

- [Google Maps Platform — documentação e catálogo de produtos](https://developers.google.com/maps/documentation)
- [Maps JavaScript API — visão geral](https://developers.google.com/maps/documentation/javascript/overview)
- [Places API (New) — visão geral](https://developers.google.com/maps/documentation/places/web-service/op-overview)
- [Places API — campos de dados](https://developers.google.com/maps/documentation/places/web-service/data-fields)
- [Places API — Nearby Search](https://developers.google.com/maps/documentation/places/web-service/nearby-search)
- [Places API — referência do recurso Place](https://developers.google.com/maps/documentation/places/web-service/reference/rest/v1/places)
- [Routes API — visão geral](https://developers.google.com/maps/documentation/routes/overview)
- [Maps Embed API](https://developers.google.com/maps/documentation/embed/embedding-map)
- [Maps Static API](https://developers.google.com/maps/documentation/maps-static/start)
- [Segurança de APIs do Google Maps Platform](https://developers.google.com/maps/api-security-best-practices)
- [Primeiros passos no Google Maps Platform](https://developers.google.com/maps/get-started)
- [Produtos e recursos em modo Legacy](https://developers.google.com/maps/legacy)

