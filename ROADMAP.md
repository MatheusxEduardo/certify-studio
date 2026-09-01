# Roadmap — Certify Studio

Este roadmap apresenta a evolução planejada do Certify Studio. O planejamento poderá ser ajustado conforme os resultados dos testes técnicos e o aprendizado obtido durante o desenvolvimento.

## v0.1.0 — MVP: emissão individual

Objetivo: entregar uma jornada completa de emissão, armazenamento e validação de um certificado individual.

### Planejamento e arquitetura

- [x] Definir o escopo reduzido do MVP.
- [x] Registrar a arquitetura inicial.
- [ ] Definir as principais entidades e relacionamentos.
- [x] Realizar o spike técnico de geração de PDF.
- [x] Escolher e documentar a biblioteca de PDF.

### Estrutura do backend

- [x] Criar o projeto com Java 21 e Spring Boot.
- [x] Configurar o Maven Wrapper.
- [ ] Organizar o monólito modular por funcionalidade.
- [x] Criar endpoint de verificação de saúde.
- [ ] Configurar tratamento global de erros.
- [ ] Configurar OpenAPI/Swagger.

### Banco de dados e infraestrutura

- [ ] Configurar PostgreSQL.
- [ ] Configurar Docker Compose.
- [ ] Configurar Flyway.
- [ ] Criar as migrations iniciais.
- [ ] Configurar variáveis de ambiente.
- [ ] Documentar a execução local.

### Cadastros

- [ ] Implementar organização de demonstração.
- [ ] Implementar cadastro de participantes.
- [ ] Implementar cadastro de cursos.
- [ ] Implementar cadastro de instrutores.
- [ ] Adicionar validações de entrada.
- [ ] Impedir duplicidades conforme as regras de negócio.

### Emissão de certificados

- [ ] Implementar emissão individual.
- [ ] Criar snapshot imutável da emissão.
- [ ] Implementar idempotência.
- [ ] Criar layout fixo do certificado com PDFBox.
- [ ] Gerar o certificado em PDF.
- [ ] Calcular o hash SHA-256.
- [ ] Armazenar o documento.
- [ ] Disponibilizar o PDF para download.

### Validação e revogação

- [ ] Gerar código público único e imprevisível.
- [ ] Gerar QR Code para validação.
- [ ] Implementar consulta pública.
- [ ] Proteger informações pessoais na consulta pública.
- [ ] Implementar revogação com motivo e data.
- [ ] Preservar o histórico do certificado revogado.

### Qualidade e entrega

- [ ] Criar testes unitários das regras principais.
- [ ] Criar testes de integração.
- [ ] Utilizar Testcontainers com PostgreSQL.
- [ ] Configurar integração contínua com GitHub Actions.
- [ ] Revisar a documentação.
- [ ] Criar a release `v0.1.0`.

## v0.2.0 — Excel e emissão em lote

Objetivo: reutilizar o fluxo confiável da emissão individual para processar vários certificados.

- [ ] Disponibilizar uma planilha-modelo `.xlsx`.
- [ ] Implementar upload de planilha Excel.
- [ ] Validar formato e colunas obrigatórias.
- [ ] Validar individualmente cada linha.
- [ ] Exibir uma prévia antes da emissão.
- [ ] Identificar erros por linha.
- [ ] Impedir duplicidades no lote.
- [ ] Processar linhas válidas mesmo quando outras forem inválidas.
- [ ] Reutilizar o serviço de emissão individual.
- [ ] Gerar relatório de processamento.
- [ ] Disponibilizar os certificados em arquivo ZIP.
- [ ] Criar testes automatizados para lotes.

## v0.3.0 — Interface web

Objetivo: disponibilizar uma interface para utilização das funcionalidades do backend.

- [ ] Definir a tecnologia do frontend.
- [ ] Criar painel administrativo.
- [ ] Criar formulários de cadastro.
- [ ] Criar tela de emissão individual.
- [ ] Criar tela de emissão em lote.
- [ ] Criar tela de consulta de certificados.
- [ ] Criar página pública de validação.
- [ ] Integrar o frontend à API.

## v0.4.0 — Templates personalizáveis

Objetivo: permitir a personalização dos modelos utilizados nos certificados.

- [ ] Cadastrar múltiplos templates.
- [ ] Versionar templates.
- [ ] Adicionar imagens, fundos e logotipos.
- [ ] Configurar fontes, cores e posicionamento.
- [ ] Criar pré-visualização do certificado.
- [ ] Avaliar editor visual drag-and-drop.
- [ ] Preservar o template utilizado em cada emissão.

## Versões futuras

Funcionalidades que poderão ser avaliadas posteriormente:

- autenticação e autorização;
- múltiplos usuários por organização;
- envio de certificados por e-mail;
- armazenamento em serviço de objetos;
- auditoria de operações;
- métricas e observabilidade;
- assinatura digital;
- internacionalização;
- implantação em ambiente de nuvem.

## Princípios do desenvolvimento

- Cada versão deve entregar uma funcionalidade utilizável e demonstrável.
- O código deve evoluir por Issues e Pull Requests pequenos.
- Dados reais e informações sensíveis não devem ser versionados.
- Funcionalidades futuras não devem complicar prematuramente o MVP.
- Decisões técnicas relevantes devem ser documentadas.
- Testes devem acompanhar as regras de negócio importantes.