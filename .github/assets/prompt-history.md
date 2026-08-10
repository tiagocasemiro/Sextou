Após cada prompt, adicione o prompt digitado no arquivo .github/assets/prompt-history.md

---

Extraia todas as definições de analytcs do arquivo .github/skills/architecture/references/re

---

› Extraia todas as definições de analytcs do arquivo .github/skills/architecture/references/repository.md para o arquivo .github/skills/architecture/assets/repository/analytics.md. Atualize a referencia de uso no arquivo .github/skills/architecture/SKILL.md

---

mova o arquivo .github/skills/architecture/assets/repository/analytics.md para o diretorio .github/skills/architecture/references

---

Crie a sessão ## Conteúdo com links locais para o arquivo .github/skills/architecture/references/repository.md

---

Crie no diretorio rais um app Android chamado Cinemateca apenas com a MainActivity. Deixe a activity em branco.

---

Compile e execute o app  no device usando o adb

---

Renomeie o pacote do app de com.example.cinemateca para com.cinemateca; Remova qualquer referencia a example do app

---

Adicione o pacote da aplicação ao manifest

---

A activity MainActivity está dando erro no manifest. Corrija

---

$android-app-architecture adicione todas as dependencias listada na skill ao projeto

---

Crie a camada de repository

Integre o app Android Cinemateca com estes endpoints da KinoCheck:

GET /trailers/trending — filmes e trailers em alta.
GET /trailers/latest — lançamentos e trailers recentes.
GET /trailers — filtros por gênero, categoria e idioma.
GET /movies?id={id} — detalhes, trailer, vídeos oficiais e recomendações.

Também permita buscar detalhes usando:

tmdb_id
imdb_id

---

Crie os usecases para disponibilisar para as view model todos os dados recebidos por repository

---

$android-app-architecture Implemente a view model da home consumindo a listagem o usecase de trending. Deixe a viewmodel preparada para receber a tela com jetpack compose

---

implemente o layout da tela de home, use o mcp do figma para ler o layout da url: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=1-411&t=h9eFD3oIOp74GJK6-4

---

Atualize o estado de loading esse layout https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=2-8&t=h9eFD3oIOp74GJK6-4

---

$android-app-architecture Adicione detecção de queda de internet, mesmo quando o device continua ligado a rede. Ao abrir o app sem conexão exiba a esta tela https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=3-136&t=h9eFD3oIOp74GJK6-4

---

Ao clicar no botão mais recentes, Exiba um botom sheet como no layout https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=5-248&t=h9eFD3oIOp74GJK6-4. Após seleção de uma nova forma de ordenar, o texto Mais Recentes ao lado do icone de ordenação será substituido pela nova frma de ordenar.

---

Ao clicar em um dos filtros "Todos", "Em Cartaz", "Lançamentos", "Em Breve", Selecione o botão clicado e aplique o filtro na listagem.

---

Considerenado que um filme fique 3 semanas em cartas e que a data de lancamento do trailer acontece 1 mes antes do lancamento do filme. Atulaize os filtros "Todos", "Em Cartaz", "Lançamentos", "Em Breve" baseando se na data de publicação do trailer

---

O campo de busca com placeholder "Buscar filmes..." deve ser construido com um input. Ao digitar cada letra no input a listagem deve ser filtrada pelo titulo do filme com o termo digitado.

---

Confira o layout implementado com o layout projetado no figma url: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=7-1092&t=h9eFD3oIOp74GJK6-4 . Se necessario faca os ajustes visuais.

---

$android-app-architecture ao clicar nos botões "Favoritar" ou "Quero assistir", os botões devem ficar selecionados por filme, como no layout do figma url: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=7-1226&t=h9eFD3oIOp74GJK6-4. Crie 2 tabelas no banco local usando room, para salvar os "favoritos" e os "quero assistir". Ao reiniciar o app esta informação deve ser preservada.

---

$android-app-architecture a implementação do room com a consulta a banco local ficou no modulo app, enquanto a implementação do acesso a dados remotos ficou no nodulo networking. Crie um modulo para implementação de acesso a dados locais, parecido com o que ja acontece com o acesso a dados remotos. Atualize a skill .github/skills/architecture/SKILL.md e seus arquivos de referencia para sempre criarem acesso local a dados em modulo proprio, seguindo as demais orientações da skill.

---

Crie a tela de detalhe do trailer clicado. A tela possui scroll com isso vou passar 2 layouts com o topo da tela e a base da tela, no meio tem o conteudo que ficou nas 2 partes. No fundo da tela tem a mesma imagem usada no topo, esticada, aplicado blur e colocado uma mascara preta com 80% de transparencia, a imagem com blur + mascara com transparencia ficam no fundo da tela por tras do conteudo. O background dos botões de compartilhar e voltar tem uma transparencia levemente escura com blur. Figma urls, topo: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=9-1645&t=h9eFD3oIOp74GJK6-4 e base: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=9-1830&t=h9eFD3oIOp74GJK6-4

---

Atualize as cores do botão de favorito e querro assistir quando selecionados. use o layout do figma url: https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=9-1986&t=h9eFD3oIOp74GJK6-4

---

Na pasta /home/tiagocasemiro/Imagens/cinemateca_launcher_icons voce vai encontrar todos os icones para adicionar ao app. Adicione o icone com todas as configurações recomentadas

---

Atualize o estado de loading da tela de detalhes https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=13-2171&t=h9eFD3oIOp74GJK6-4

---

Cire o icone usando Android vector drawable com esse layout https://www.figma.com/design/v6Ene6gKWwVDOKueK06OFN/Untitled?node-id=14-2254&t=h9eFD3oIOp74GJK6-4. Adicione bordas da cor Color(0xFF4D8EFF)

---

No meu celular sansumg o icone fica com as bordas cortadas, pq no sansung os icones tem um formato diferente. Em alguns celulares o formato é redondo. ajuste o icone de launch para funcionar bem em todos os formatos

---

Adicione uma borda azul igual a do app/src/main/res/drawable/ic_launcher.xml no icone app/src/main/res/drawable/ic_launcher_round_background.xml

---

os 2 icones da pasta app/src/main/res/mipmap estão apresentando erro

---

Os 2 drawables da pasta mipmap estão dando erro. todos os icones redondos das pastas mipmap estão quadrados.

---

Na tela de detalhes click nos materiais promoconais na tela de detalhes não funcionando. Deveriam abrir no youtube ou navegador

---

Adicionar estado  sem conexao na tela de detalhes detalhes

---

Reultilize o composable de sem conexão da home. O objetivo é padronizar a experiencia e reduzir a quantidade de código

---

Centralize o conteudo da tela de sem conexão na tela de detalhes

---

Ao clicar nos cards da home ou ao passar o mause em cima, o sombreamento do efeito de click está sem arredondamento.

---

Na tela de detalhe do trailer os botõe voltar e compartilhar estão sem o efeito blur no background. Adicione.

---

É necessário backgroundImageUrl para configurar o blur ? Porque?

---

Consegue aplicar o blur direto no fundo transparente ?

---

Crie um README.md completo e profissional para o projeto Android chamado **Cinemateca**.

O README deve parecer o de um projeto Open Source de alto nível, pronto para ser publicado no GitHub.

Utilize Markdown moderno, organizado e visualmente agradável, com tabelas, badges, callouts, listas, diagramas Mermaid e imagens quando fizer sentido.

O documento deve possuir DUAS VISÕES COMPLETAMENTE DIFERENTES.

=====================================================================
# 1 - VISÃO DE NEGÓCIO
=====================================================================

Esta primeira parte deve possuir um tom comercial e de apresentação.

O objetivo é convencer alguém que acabou de chegar ao repositório de que o projeto é bonito, moderno, profissional e muito bem desenvolvido.

Escreva como se estivesse apresentando um produto.

Comece com uma descrição impactante do projeto.

Explique o problema que o aplicativo resolve.

Mostre os diferenciais.

Mostre por que ele é um excelente exemplo de desenvolvimento Android moderno.

Descreva o aplicativo detalhadamente.

Inclua uma seção de funcionalidades extremamente atrativa.

Exemplo:

- Pesquisa instantânea
- Filmes em cartaz
- Lançamentos
- Em breve
- Favoritos
- Lista Quero Assistir
- Tela completa de detalhes
- Reprodução de trailers
- Materiais promocionais
- Recomendações relacionadas
- Interface Material Design
- Tema Claro/Escuro
- Persistência local
- Navegação fluida
- Carregamento otimizado
- Tratamento de erros
- Estados de Loading
- Estados vazios
- Atualização automática

Apresente cada funcionalidade explicando seu benefício para o usuário.

Utilize bastante Markdown.

Use emojis moderadamente.

Inclua badges no topo.

Exemplo:

- Kotlin
- Jetpack Compose
- Material Design 3
- Android
- MVVM
- Clean Architecture

Crie uma seção:

## Screenshots

Crie placeholders para imagens.

Exemplo:

![Home](docs/images/home.png)

![Detalhes](docs/images/details.png)

![Trailer](docs/images/trailer.png)

![Favoritos](docs/images/favorites.png)

Mostre as telas organizadas em tabela quando possível.

Crie uma seção:

## Fluxo do aplicativo

Utilize um diagrama Mermaid mostrando a navegação principal.

Mostre também um fluxo resumido da experiência do usuário.

Inclua uma seção:

## Destaques do projeto

Liste os principais pontos fortes.

Por exemplo:

- Arquitetura escalável
- Código limpo
- UI moderna
- Fácil manutenção
- Fácil evolução
- Separação de responsabilidades
- Componentização
- Alto desempenho

=====================================================================
# 2 - VISÃO TÉCNICA
=====================================================================

A segunda parte muda completamente o tom.

Use linguagem objetiva.

Sem marketing.

Escreva como documentação técnica.

Explique detalhadamente como o projeto foi construído.

Inclua as seguintes seções.

# Arquitetura

Explique resumidamente:

- Clean Architecture
- MVVM
- Repository Pattern
- Use Cases
- StateFlow
- UI State
- Navigation
- Injeção de Dependência
- Camadas do projeto

Inclua um diagrama Mermaid mostrando o fluxo:

UI
↓
ViewModel
↓
UseCase
↓
Repository
↓
Remote Data Source
↓
KinoCheck API

Mostre também a estrutura de pastas.

Exemplo

app/

core/

data/

domain/

presentation/

designsystem/

navigation/

di/

util/

Explique rapidamente a responsabilidade de cada módulo.

# Tecnologias utilizadas

Crie uma tabela contendo:

- Kotlin
- Jetpack Compose
- Material Design 3
- Kotlin Coroutines
- Flow / StateFlow
- ViewModel
- Navigation Compose
- Hilt/Koin (conforme utilizado)
- Coil
- Retrofit
- OkHttp
- Kotlin Serialization ou Moshi/Gson
- Paging 3 (caso utilizado)
- Room (caso utilizado)
- DataStore
- JUnit
- MockK
- Turbine
- Espresso
- Compose UI Test

# Consumo da API

Explique que o aplicativo utiliza diretamente a API da KinoCheck.

Mostre um pequeno fluxo.

App

↓

Repository

↓

Retrofit

↓

KinoCheck API

Explique rapidamente o tratamento de erros.

# Estrutura do projeto

Mostre a árvore simplificada do projeto.

# Como configurar o ambiente

Explique detalhadamente.

Instalar:

- Android Studio
- JDK
- Android SDK
- Git

Informar versões utilizadas.

Inclua uma tabela para:

Compile SDK

Minimum SDK

Target SDK

Android Gradle Plugin

Gradle

Kotlin

Compose Compiler

Java

JDK

# Como obter o projeto

```bash
git clone ...

---

Resuma muito a descrição está muito grande

---

Simplifique bem mais a parte de negocio

---

Melhore o flowchart TD use fluxograma mais visual

---

Atualise as cores dos botões favoritar e quero assistir da home para ficarem com as mesmas cores dos memsmos botões na tela de detalhes

---

Adicione a skill da pasta .github/skills/architecture a regra que toda string deve ser criada no arquiovo de strings do android

---

$android-app-architecture Procure migre todas as strings em código para o arquivo de strings

---

Nos botões de favorito o icone de coração fica preenchido quando selecionado. Deixe o icone dos botões de quero assistir também preenchidos quando selecionado

---

$android-app-architecture  Com base apenas na nossas definições de arquitetura. Crie testes arquiteturais usando konsist. Não olhe para o código, apenas crie os testes baseando se nas definições danossa skill

---

Use confiogurações como as do exemplo abaixo para configurar testes de memoria e testar a integração com o room. Simule tabelas e verifique se as querys estão retornando o esperado. Teste insert e delete  em tabelas de memoria. teste amplamente a camada de integração com banco local.
Exemplo de configuração:
@RunWith(AndroidJUnit4::class)
class ExampleRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: ExampleDao
    private lateinit var repository: ExampleRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao = database.exampleDao()
        repository = ExampleRepository(dao)
    }

    @After
    fun teardown() {
        database.close()
    }
}

---

Converta o CinematecaDatabaseTest para usar database in memory

---

Vamos ampliar a swite de testes para acesso remoto. Crie um interceptor no retrofit, adicione como ultimo da pilha de interceptors. Sobrescreva o Retrofit original pelo modificado em uma nova classe de testes. Crie arquivos json e retorne a partir desse interceptor. O novo interceptor não pode deixar a requisição ser executada, para cada teste ele retornará um json. Antes de retornar o json este interceptor lancará uma excessão para cada elemente com estado inconsistente na requisição. Caso tudo esteja ok o interceptor devolverá o json ou um status de erro quando simular erro na api remota. Neste testes vamos testar consistencia da rquisição, serialização e desserialização e tratamento dos dados retornado ainda na camada networking.

---

Rode todos os testes da aplicação

---

Ao abrir a tela de detalhes de qualquer trailer e em seguida clicar 2 vezes rapido no botão de voltar, o app fecha a tela de detalhes a tela da home e fica mostrando uma tela cinza completamente vazia. Corrija este bug de navegação

---

Ao abrir o app, Enquanto o app carrega, é exibida uma tela com o icone daaplicação e um funso cinza. Mude o fundo cinza para     val Background = Color(0xFF0A0A0F) do CinematecaColors no theme
