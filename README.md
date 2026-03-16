# Desafio Solution SA
<img width="896" height="832" alt="image" src="https://github.com/user-attachments/assets/ed69ee7f-4d48-4110-9470-fc6c7bfa7611" />
Aplicação web desenvolvida como teste técnico para a vaga de Desenvolvedor na Solution SA. O sistema permite o cadastro de usuários e o gerenciamento de seus endereços, com consulta automática de CEP via API pública ViaCEP.


---

## Stack

| Camada | Tecnologia |
|---|---|
| Frontend | React + TypeScript + Vite + Tanstack Router |
| Backend | Java + Spring Boot |
| Banco de dados | PostgreSQL |
| Autenticação | HTTP Basic |
| Infraestrutura | Docker + Docker Compose |
| Testes Automatizados | JUnit + MockMVC |


---

## Funcionalidades

- Cadastro e autenticação de usuários com CPF e senha
- Controle de acesso por perfil (Administrador e Usuário comum)
- Cadastro de múltiplos endereços por usuário
- Preenchimento automático de endereço via CEP (ViaCEP)
- Definição de endereço principal com troca automática
- Promoção automática de novo endereço principal ao remover o atual
- Interface responsiva
- Testes de integração com JUnit e MockMVC

---

## Pré-requisitos para rodar o projeto

- [Docker](https://www.docker.com/) com Docker Compose
- Java
- NodeJS

---

## Como rodar

```bash
# Clone o repositório
git clone https://github.com/cauamattosprj/desafio-solution-sa.git
cd desafio-solution-sa

# Suba os containers
docker-compose up -d --build
```

Depois de subir:

- Frontend: [http://localhost](http://localhost)
- Backend: [http://localhost:8080](http://localhost:8080)
- Banco de dados: `localhost:5432`

---

## Credenciais padrão

O banco de dados sobe vazio. Para acessar o sistema, cadastre um usuário administrador via front-end ou na rota da API `POST /auth/user`:

```json
{
  "cpf": "000.000.000-00",
  "name": "Exemplo",
  "password": "sua-senha",
  "role": "ADMIN"
}
```

---

## Estrutura do projeto

```
desafio-solution-sa/
├── backend/          # API REST Spring Boot
│   ├── src/
│   │   ├── main/java/com/cauamattosprj/solutionsa/
│   │   │   ├── config/       # Configurações (CORS)
│   │   │   ├── controllers/  # Endpoints REST
│   │   │   ├── dtos/         # Classes de padronização de request e responses HTTP
│   │   │   ├── models/       # Entidades JPA
│   │   │   ├── repositories/ # Repositórios Spring Data
│   │   │   ├── security/     # UserDetails e autenticação
│   │   │   └── services/     # Regras de negócio
│   │   └── test/             # Testes de integração
│   └── Dockerfile
├── frontend/         # Projeto React com Vite
│   └── Dockerfile
└── docker-compose.yml
```

---

## Endpoints principais

| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/auth/user` | Cadastrar usuário | Público |
| POST | `/auth/login` | Login | Público |
| GET | `/users` | Listar usuários | ADMIN |
| GET | `/users/{id}` | Buscar usuário | Autenticado |
| PUT | `/users/{id}` | Atualizar usuário | Autenticado |
| DELETE | `/users/{id}` | Remover usuário | Autenticado |
| GET | `/users/{id}/addresses` | Listar endereços | Autenticado |
| POST | `/users/{id}/addresses` | Cadastrar endereço | Autenticado |
| PUT | `/users/{id}/addresses/{aid}` | Atualizar endereço | Autenticado |
| DELETE | `/users/{id}/addresses/{aid}` | Remover endereço | Autenticado |
| GET | `/viacep/{cep}` | Consultar CEP | Autenticado |

---

## Testes

O projeto contém testes de integração com JUnit 5 e MockMvc cobrindo os principais fluxos: autenticação, controle de acesso por perfil, regras de negócio de endereços e integração com ViaCEP.

```bash
# Rodar os testes (precias do Java 21 instalado)
cd backend
./mvnw test
```

---

## Decisões técnicas e limitações

Devido a situações pessoais, não pude dedicar grande quantidade de tempo ao projeto, e isso irá ser notado em alguns momentos. Algumas coisas que só fui perceber mais para frente que poderiam ser melhoradas acabaram ficando do jeito em que estavam para economizar tempo e conseguir entregar o projeto.

**Autenticação via HTTP Basic Auth**
A estratégia escolhida foi HTTP Basic Auth por ser nativa do Spring Security e não exigir muita configuração adicional. Eu estava planejando, caso conseguisse finalizar em tempo hábil, adicionar JWT, mas como não foi o caso, optei por manter Basic Auth mesmo.

**Sem migrations de banco de dados**
O schema é gerenciado pelo `hibernate.ddl-auto=update` somente. Em um projeto em produção, o correto seria usar **Flyway** ou alguma ferramenta semelhante para controlar as migrations.

**CORS aberto**
O CORS está configurado com `allowedOriginPatterns("*")` na API do backk-end para simplificar o desenvolvimento. Em produção, deveria haver restrição de URLs.

**Testes incompletos**
Os testes de integração cobrem os fluxos principais, mas não têm cobertura mais ampla. A ideia inicialmente era seguir um TDD, mas acabei optando por ir apenas cobrindo os fluxos principais e seguir com a implementação antes dos testes.

---

## Uso de Inteligência Artificial

Parte deste projeto foi desenvolvida com auxílio de ferramentas de IA, de forma transparente:

- **Geração dos Dockerfiles e docker-compose.yml** — a configuração de containers foi gerada com auxílio de IA e ajustada manualmente para corrigir alguns erros.
- **Testes de integração** — os testes foram gerados com auxílio de IA com base nos requisitos funcionais do projeto e ajustados para refletir a estrutura real do projeto.
- **Base da UI com ShadCN** - Essa parte eu poderia ter feito toda a mão pois sempre fiz meus projetos com ShadCN, no entanto optei por estar gerando a base da UI e fluxos do Tanstack Router com o Copilot, pois acabei investindo muito mais tempo no back-end. No entanto, tive que ajustar basicamente tudo, pois o layout entregue pelo Copilot ainda é muito primitivo e fica com aquela visual clássico de coisa feita por IA, o que desvaloriza o projeto, na minha opinião.

Todo o código gerado foi revisado e compreendido, não apenas no copia e cola. O uso de IA foi uma decisão consciente devido ao meu momento atual.

---

## Diferenciais implementados

- [x] Docker para subir o projeto
- [x] Organização do backend em camadas (Optei por criar todo o backend em uma arquitetura MVC)
- [x] Testes automatizados (integração com JUnit 5 + MockMvc)
- [x] Tratamento de erros da API
- [ ] React Query
- [x] shadcn/ui
- [ ] Paginação nas listagens
- [ ] JWT
