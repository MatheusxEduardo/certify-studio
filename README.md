# Certify Studio

Plataforma para emissão, armazenamento e validação de certificados digitais em PDF.

> O projeto está em desenvolvimento. A primeira versão terá como foco a emissão individual e a validação pública de certificados.

## Sobre o projeto

O Certify Studio está sendo desenvolvido para demonstrar a construção de uma aplicação backend completa utilizando Java e Spring Boot.

A aplicação permitirá cadastrar os dados necessários para uma emissão, gerar o certificado em PDF, preservar um snapshot imutável das informações e disponibilizar uma consulta pública por código de validação e QR Code.

O projeto também tem como objetivo aplicar práticas utilizadas em projetos profissionais:

- organização do trabalho com Issues e Pull Requests;
- versionamento com Git e GitHub;
- migrations de banco de dados;
- testes automatizados;
- documentação de API;
- execução com Docker;
- integração contínua.

## Escopo do MVP

A primeira versão deverá permitir:

- cadastrar uma organização de demonstração;
- cadastrar participantes;
- cadastrar cursos;
- cadastrar instrutores;
- emitir um certificado individual;
- gerar e armazenar o documento em PDF;
- calcular o hash SHA-256 do arquivo;
- criar um snapshot imutável da emissão;
- gerar código público e QR Code;
- validar publicamente um certificado;
- revogar um certificado com motivo;
- baixar o documento emitido.

O escopo detalhado está disponível em [docs/MVP_SCOPE.md](docs/MVP_SCOPE.md).

## Arquitetura

O backend será desenvolvido como um monólito modular organizado por funcionalidade.

Dentro de cada módulo será aplicado o padrão Controller–Service–Repository:

- **Controller:** recebe requisições e produz respostas HTTP;
- **Service:** executa casos de uso e regras de negócio;
- **Repository:** realiza o acesso aos dados;
- **DTO:** representa os dados de entrada e saída da API;
- **Model:** representa as entidades do domínio.

Entidades de persistência não serão retornadas diretamente pela API.
A decisão arquitetural e suas justificativas estão registradas em [ADR 0001 — Use a Modular Monolith Architecture](docs/adr/0001-use-modular-monolith-architecture.md).

## Tecnologias planejadas

- Java 21;
- Spring Boot;
- Spring Web;
- Spring Data JPA;
- Bean Validation;
- PostgreSQL;
- Flyway;
- Maven;
- JUnit 5;
- Mockito;
- Testcontainers;
- OpenAPI/Swagger;
- Docker;
- GitHub Actions.

A tecnologia de geração de PDF será escolhida após a realização de um teste técnico.

## Estrutura atual

```text
certify-studio/
├── docs/
│   ├── adr/
│   │   └── 0001-use-modular-monolith-architecture.md
│   └── MVP_SCOPE.md
├── .gitignore
├── README.md
└── ROADMAP.md
```