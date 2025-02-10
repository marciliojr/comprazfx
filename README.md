# 📦 ComprazFX - Gestor de Compras por Cupom Fiscal

ComprazFX é um aplicativo de gestão de compras por cupom fiscal (emitido pela sefaz), desenvolvido em JavaFX com integração ao banco de dados SQLite. O sistema permite o cadastro e gerenciamento de compras, estabelecimentos e itens extraídos de arquivos PDF, além de gerar relatórios em PDF.

## Funcionalidades
- Upload de arquivos PDF para extração automática de dados de compras.
- Cadastro de estabelecimentos e itens de forma automática a partir dos PDFs.
- Visualização de compras filtradas por data e estabelecimento.
- Geração de relatórios em PDF com resumo dos itens e valores totais.
- Integração com banco de dados SQLite para armazenamento local.

## Tecnologias Utilizadas
- **JavaFX:** Interface gráfica.
- **Spring Boot:** Gerenciamento de dependências e injeção de dependência.
- **SQLite:** Banco de dados local.
- **Apache PDFBox:** Extração de texto de arquivos PDF.
- **iText:** Geração de arquivos PDF.

## Como Executar o Projeto
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/comprazfx.git
   ```
2. Navegue até o diretório do projeto:
   ```bash
   cd comprazfx
   ```
3. Compile e execute o projeto com o Maven ou diretamente pela sua IDE.
   ```bash
   ./mvnw clean install
   java -jar target/comprazfx.jar
   ```

## Estrutura do Projeto
```
comprazfx/
├── model/                # Entidades JPA (Compra, Estabelecimento, Item)
├── repository/           # Repositórios Spring Data JPA
├── service/              # Serviços para lógica de negócio
├── infra/                # Utilitários e inicializadores
├── ApplicationFX.java    # Classe principal do JavaFX
├── SpringBootApp.java    # Inicialização do Spring Boot
└── resources/            # Arquivos FXML e imagens
```

# CHANGELOG

## [1.0.0] - 2024-02-10
### Adicionado
- Importação de arquivos PDF com extração automática de dados.
- Cadastro automático de estabelecimentos e itens.
- Filtragem de compras por data e nome do estabelecimento.
- Geração de relatórios em PDF com somatório de valores.
- Interface gráfica responsiva com JavaFX.
- Banco de dados local SQLite para persistência de dados.

### Corrigido
- Validação de arquivos PDF inválidos.
- Correção de problemas de formatação de datas durante o cadastro.

### Alterado
- Melhorias de performance na extração de dados dos PDFs.
- Ajustes visuais na interface do usuário.



