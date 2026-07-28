## 📋 Plano de Negócios: Aplicativo "Sextou"## 1. Sumário Executivo
O Sextou é um aplicativo de entretenimento, lazer e gastronomia focado em consumo imediato de baixo custo, cultura de subúrbio e estabelecimentos raiz. O app resolve o problema do usuário que quer descompressão de fim de semana (diurna ou noturna) sem frescura, destacando locais com comida pronta para consumo e diferenciais de entretenimento (como karaokê, música ao vivo e playgrounds/espaço kids).
------------------------------
## 2. Proposta de Valor

* Para o Usuário: Encontrar o "rolê de combate" perfeito para a sexta-feira e fim de semana. O app elimina a gourmetização e entrega opções onde se come bem gastando pouco e há entretenimento real (música, cantoria ou espaço para os filhos brincarem).
* Para o Ecossistema: Dar visibilidade orgânica para trailers, botecos de calçada, adegas, biroscas e comércios de bairro que são ignorados pelos grandes algoritmos comerciais.

------------------------------
## 3. Segmentação de Mercado e Público-Alvo

* O Público "Sextou": Trabalhadores, estudantes e famílias de subúrbio/periferia que buscam lazer acessível.
* A Persona Noturna: O jovem ou adulto que quer um litrão barato, espetinho na calçada, karaokê ou música ao vivo para descontrair após o trabalho.
* A Persona Diurna (Fim de Semana): Pais e mães que buscam um restaurante simples, pizzaria ou quiosque com preço justo e que tenha um parquinho ou espaço para as crianças correrem enquanto eles relaxam.

------------------------------
## 4. Arquitetura da Solução (Filtros de Tecnologia)
O aplicativo não fará exclusão por preço ou região no código, mas usará a inteligência da Google Places API para moldar o catálogo:

* Critério de Comida Pronta (Varejo): Filtro rígido por includedTypes (bar, restaurant, meal_takeaway, meal_delivery, cafe, bakery, convenience_store, liquor_store, night_club). Bloqueio automático de termos crus (açougue, hortifrúti, peixaria).
* Filtros de Lazer Ativos: Mapeamento de liveMusic, goodForChildren e outdoorSeating.
* Mineração de Reviews: Varredura de palavras-chave nas avaliações do Google para taguear os diferenciais ("sinuca", "parquinho", "karaokê", "litrão").

------------------------------
## 5. Estratégia de Marketing e Posicionamento
O marketing do aplicativo trabalhará para atrair o público de baixo custo de forma natural, sem precisar banir restaurantes caros no código:

* Identidade Visual "Raiz": Cores quentes (amarelo, vermelho, laranja), uso de gírias locais, ícones de copo americano, espetinho e microfone. O design grita "subúrbio" e afasta o público gourmet.
* Categorias de Apelo Popular (Home do App):
* 🎙️ Cantando e Bebendo: Foco em Karaokê/Música ao vivo.
    * 🧒 Criança Corre, Pai Relaxa: Foco em Playground/Espaço Kids.
    * 🍢 Podrões & Espetinhos: Foco em Trailers e Kombis.
    * 🍻 Salvando a Noite: Adegas e Distribuidoras com calçada.
* Efeito Comunidade: Como o app estará na Play Store, o crescimento inicial será orgânico através do compartilhamento de "achados" em redes sociais (TikTok e Instagram) por páginas de culinária popular da periferia.

------------------------------
## 6. Modelo de Monetização (Crescimento Sustentável)
Como o app nascerá para uso pessoal e escala orgânica, a monetização deve ser leve para cobrir os custos da API do Google e servidores:

1. Anúncios Não-Invasivos (Google AdMob): Banners discretos no rodapé da lista ou entre os pinos do mapa, focados em produtos de massa (marcas de cerveja, refrigerante, aplicativos de transporte).
2. Destaque Premium Local (Futuro): O dono do boteco de subúrbio ou o dono do trailer de lanche pode pagar uma assinatura simbólica (ex: R$ 19,90/mês) para ter o seu pino brilhando em destaque no mapa quando alguém buscar por perto na sexta-feira.

------------------------------
## 7. Estrutura de Custos

* Desenvolvimento: Baixo custo inicial se desenvolvido em ferramentas No-Code (FlutterFlow/Bubble) ou frameworks ágeis (React Native).
* Infraestrutura: Servidor básico (Firebase, Supabase ou AWS) para salvar os dados de login e favoritos dos usuários.
* Google Maps API: O maior custo fixo. Utilizar estratégias de caching (salvar os resultados das buscas no seu próprio banco por 7 a 15 dias) para evitar fazer requisições repetidas ao Google toda vez que um usuário abrir o app, mantendo a operação extremamente barata ou dentro da cota gratuita de testes do Google Cloud.

------------------------------
Para darmos o próximo passo na criação do Sextou, o que você prefere definir agora?

* A lista exata de palavras-chave em português para o script varrer nas avaliações do Google?
* O esboço das telas principais (UX/UI) para desenhar o fluxo do usuário?
* A estratégia técnica de cache para economizar dinheiro com a API do Google?


