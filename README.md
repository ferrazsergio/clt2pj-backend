# CLT2PJ Backend

API para simulação de benefícios e comparativo entre CLT e PJ.

## Descrição

Este projeto backend oferece endpoints para simular salários líquidos, benefícios, reservas de emergência e outros cálculos comparativos entre modelos de contratação CLT e PJ.

## Tecnologias

- Java 21
- Spring Boot
- Spring Web, Spring Security
- Maven
- Swagger/OpenAPI (springdoc-openapi)
- Docker

## Como rodar localmente

```bash
# Compile e rode o projeto
mvn spring-boot:run
```

Acesse a API em:  
`http://localhost:8080`

## Documentação da API

Acesse a documentação interativa do Swagger:

- [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Rodando os testes

```bash
mvn test
```

## Rodando via Docker

```bash
# Gere o JAR
mvn clean package
# Construa a imagem
docker build -t clt2pj-backend .
# Rode o container
docker run -p 8080:8080 clt2pj-backend
```

## Usando Docker Compose

```bash
docker-compose up --build
```

## Endpoints principais

- `POST /simulacao` - Simula valores comparativos entre CLT e PJ

## Melhorias futuras

- [ ] Pipeline CI/CD com GitHub Actions
- [ ] Histórico de simulações
- [ ] Autenticação JWT
- [ ] Persistência em banco de dados
- [ ] Logging e tratamento global de erros
- [ ] Versionamento da API (`/api/v1`)

## Como contribuir

1. Faça um fork do projeto
2. Crie sua branch: `git checkout -b minha-feature`
3. Commit e push: `git commit -am 'Minha feature' && git push origin minha-feature`
4. Abra um Pull Request

---

**Dúvidas ou sugestões:**  
Abra uma issue ou entre em contato!
