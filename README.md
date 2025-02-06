# 📦 ComprazFX - Gestor de Compras por Cupom Fiscal

Bem-vindo ao **ComprazFX**, uma aplicação desktop e offline para gestão de compras a partir de cupoms fiscais em PDF. O objetivo do ComprazFX é facilitar o controle de despesas, permitindo o cadastro, visualização e geração de relatórios de compras de forma prática e eficiente. 🚀

## 🌟 **Principais Funcionalidades**

- 📥 **Importação de PDFs**: Extração automática de dados de cupoms fiscais em PDF.
- 🗃️ **Gestão de Compras e Itens**: Cadastro, consulta e gerenciamento de compras e itens.
- 📊 **Relatórios Detalhados**: Geração de relatórios em PDF com resumo de gastos.
- 🔍 **Filtros Avançados**: Pesquisa por nome do estabelecimento e período de tempo.
- 🖥️ **Interface Gráfica Moderna**: Desenvolvida com JavaFX para uma experiência fluida e intuitiva.

## ⚙️ **Tecnologias Utilizadas**

- **Java 17** ☕
- **JavaFX** para a interface gráfica 🎨
- **Spring Boot** para a estrutura do backend 🚀
- **JPA (Hibernate)** para persistência de dados 🗂️
- **SQLite** como banco de dados local 💾
- **Apache PDFBox** para extração de texto de PDFs 📄
- **iText PDF** para geração de relatórios em PDF 📝

## 🚀 **Como Executar o Projeto**

1. **Pré-requisitos:**
   - Java 17 ou superior
   - Maven ou Gradle (para build)

2. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/comprazfx.git
   cd comprazfx
   ```

3. **Execute a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```
   ou
   ```bash
   java -jar target/comprazfx.jar
   ```

## 🗂️ **Estrutura do Projeto**

```
comprazfx/
├── src/
│   ├── main/java/com/marciliojr/comprazfx/
│   │   ├── model/               # Entidades JPA (Compra, Item, Estabelecimento)
│   │   ├── service/             # Lógica de negócios e serviços
│   │   ├── repository/          # Interfaces do Spring Data JPA
│   │   ├── infra/               # Utilitários e integração (PDF, DB)
│   │   ├── ApplicationFX.java   # Classe principal do JavaFX
│   │   └── SpringBootApp.java   # Classe principal do Spring Boot
│   └── resources/
│       ├── application.properties # Configuração do banco de dados
│       └── main-view.fxml        # Layout da interface gráfica
└── database/                    # Banco de dados SQLite local
```

## 📄 **Exemplos de Uso**

- **Importar uma cupom fiscal:**
  1. Clique em "Carregar PDF" e selecione o arquivo.
  2. Insira o nome do estabelecimento.
  3. Clique em "Cadastrar" para importar os dados.

- **Gerar relatório:**
  1. Aplique filtros de data e nome do estabelecimento.
  2. Clique em "Gerar PDF" para obter um relatório completo.

## 🛠️ **Configurações Importantes**

O banco de dados está configurado para funcionar localmente usando SQLite. O arquivo de configuração está localizado em `application.properties`:

```properties
spring.datasource.url=jdbc:sqlite:database.db
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## 🤝 **Contribuições**

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e enviar pull requests. ✨

## 📢 **Licença**

Este projeto está licenciado sob a [MIT License](LICENSE).

