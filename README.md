# 💰 FinanceControl

Aplicativo Android para controle de despesas pessoais, desenvolvido em **Java**, com foco em **boas práticas de arquitetura**, **organização de código** e **aprendizado progressivo**.

O projeto foi construído passo a passo, evoluindo de uma implementação simples para uma arquitetura **MVVM**, seguindo padrões usados no mercado.

---

## 📱 Funcionalidades

- ✅ Cadastro de despesas
- ✅ Descrição, valor e categoria
- ✅ Data da despesa (automática ou selecionada)
- ✅ Data de vencimento opcional
- ✅ Lista de despesas com RecyclerView
- ✅ Filtro por texto
- ✅ Filtro por categoria
- ✅ Resumo financeiro:
  - Total gasto
  - Maior gasto
- ✅ Persistência local com Room
- ✅ Atualização automática da lista
- ✅ Mensagens tratadas como eventos únicos (Toast)

---

## 🧠 Arquitetura

O projeto utiliza **MVVM (Model–View–ViewModel)**, separando responsabilidades de forma clara:

### 🔹 Model
- `Despesa` (Entity)
- `DespesaDao`
- `AppDatabase` (Room)

### 🔹 View
- `MainActivity`
- Responsável apenas por:
  - Interface
  - Eventos do usuário
  - Observação de dados (`LiveData`)

### 🔹 ViewModel
- `DespesaViewModel`
- Responsável por:
  - Lógica de negócio
  - Validações
  - Filtros
  - Resumo financeiro
  - Comunicação com o banco

---

## 🗄️ Persistência de Dados

- Utiliza **Room Database**
- Operações executadas fora da Main Thread
- Dados mantidos mesmo após fechar o app

---

## 🔄 Atualização de UI

- Uso de **LiveData**
- RecyclerView atualiza automaticamente
- Nenhum acesso direto ao banco na Activity

---

## 🚫 Boas práticas aplicadas

- ✅ Uso de `ExecutorService`
- ✅ Tratamento correto de eventos únicos
- ✅ Código organizado e comentado
- ✅ Pensado para aprendizado e evolução

---

## 🛠️ Tecnologias utilizadas

- Java
- Android SDK
- Room
- RecyclerView
- LiveData
- ViewModel
- Git & GitHub

---

## 🎯 Objetivo do projeto

Este projeto tem como objetivo principal **aprendizado prático** de:
- Arquitetura Android
- Organização de código
- Boas práticas de desenvolvimento
- Evolução incremental de um app real

---

## 🚀 Próximos passos (planejados)

- 📦 Implementar camada Repository
- 📊 Gráfico de gastos por categoria
- 🧪 Testes unitários no ViewModel
- 🎨 Melhorias de UI/UX

---

## 👤 Autor

**Talles Mello**  
Estudante e desenvolvedor em evolução contínua 🚀  
GitHub: [https://github.com/Tallesmello](https://github.com/Tallesmello)
Linkedln: [https://www.linkedin.com/in/tallesmello/](https://www.linkedin.com/in/tallesmello/)
