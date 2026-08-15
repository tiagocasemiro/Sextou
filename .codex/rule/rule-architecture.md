* Use somente a stack determinada na skill de arquitetura @skills/architecture. Use os frameworks, libs, patterns, nomenclaturas definidas nessa skill
* Caso não seja encontrado algo que seja util para o projeto, após adicionar ao projeto, relate a adição no arquivo @assets/skill-debts.md
* Crie testes para toda a lógica de domínio. Teste todas as possibilidades e variações de regra, sempre em nível unitário, sem depender de recursos externos.
* Concentre-se em testar um comportamento por teste. Evite testes muito grandes.
* Seja claro e objetivo na descrição do teste.
* Garanta que o código escrito esteja coberto pelos testes.
* Crie expectativas consistentes, verificando de fato o que importa.
* Feche conexões ou de plataformas de streams após os testes, quando necessário.
* Use `beforeEach` para inicialização.
* Use `afterEach` se precisar liberar recursos