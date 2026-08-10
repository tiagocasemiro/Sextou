# Planejamento:

## Informações vindas da api:

### API:

- Filmes
- Séries
- Trailers
- Teasers
- Clips
- Featurettes
- Vídeos em tendência
- Últimos trailers lançados
- IDs do IMDb e TMDB
- Thumbnails
- Visualizações
- Idioma do conteúdo
- Links do YouTube
- Recomendações relacionadas

### Principais endpoints úteis:

/movies
Dados de filmes
Trailers associados
Vídeos promocionais
Recomendações

/shows
Dados de séries
Trailers e vídeos

/trailers/trending
Trailers em alta

/trailers/latest
Últimos trailers publicados

## pronp de design:

Crie um app Android simples, com nome Cinemateca que liste os filmes próximos ou já em cartas nos cinemas.

- (TELA) uma tela principal com uma listagem de filmes,
  - Filtro da lista com lançamentos
  - Busca por nome do filme
  - Categoria em cada filme
  - numero de visualisações de cada filme
  - idioma do conteudo
  - (TELA) uma tela de detalhes para cada filme, com botão para adicionar aos favoritos.
    - (TELA) uma tela para assistir ao trailler com player embutido
    - (TELA) uma tela para assistir ao material promocional (caso o filme tenha material promocional)
    - idioma do conteudo
    - link pro youtube
  - (TELA) uma tela de recomendações relacionadas
