# 🛒 ComprazFx

> Interface gráfica para chamada aos endpoints do **Compraz**.

![JavaFX](https://img.shields.io/badge/JavaFX-UI-blue?style=for-the-badge&logo=java)  
![Maven](https://img.shields.io/badge/Maven-Build-orange?style=for-the-badge&logo=apachemaven)  

## 📌 Descrição  

**ComprazFx** é uma aplicação desenvolvida em **JavaFX** que permite visualizar e cadastrar compras a partir de notas fiscais.  
A interface interage com uma API REST para buscar informações sobre compras, além de possibilitar o **upload de arquivos PDF**.  

---

## 🚀 Tecnologias Utilizadas  

- **JavaFX** 🎨 – Interface gráfica  
- **Maven** 🏗️ – Gerenciamento de dependências  
- **OkHttp** 🌐 – Cliente HTTP para envio de arquivos  
- **Gson** 🛠️ – Manipulação de JSON  
- **Spring RestTemplate** 🔗 – Consumo de APIs  
- **Java 17+** ☕ – Linguagem principal  

---

## 🛠️ Funcionalidades  

✅ Busca de itens comprados por estabelecimento e período 📊  
✅ Exibição de lista de compras em tabela interativa 📄  
✅ Upload de arquivos PDF de notas fiscais 📎  
✅ Geração de relatórios em PDF 📑  
✅ Splash screen personalizada para carregamento inicial 🎨  

---

## ▶️ Como Executar  

1. **Clone o repositório**  
   ```sh
   git clone https://github.com/seu-usuario/comprazfx.git
2. **Acesse a pasta do projeto**
   ```sh
   cd comprazfx
3. **Compile e execute com Maven**
   ```sh
   mvn clean javafx:run
   
Observação: Certifique-se de que sua API Compraz está rodando localmente (http://localhost:8080) antes de executar a aplicação.
b

📤 Endpoints Consumidos
A aplicação interage com os seguintes endpoints da API Compraz:

📌 Buscar Itens
 ``` bash
  GET http://localhost:8080/api/item/itens
```
📌 Somatório de valores
 ``` bash
  GET http://localhost:8080/api/item/soma-valor-unitario
 ````
📌 Upload de PDF
``` bash
  POST http://localhost:8080/api/pdf/upload
```
📌 Gerar Relatório PDF
``` bash
GET http://localhost:8080/api/item/exportar/pdf
````
